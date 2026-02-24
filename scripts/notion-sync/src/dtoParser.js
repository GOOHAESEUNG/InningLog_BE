import fs from 'fs';
import path from 'path';
import { glob } from 'glob';

const dtoCache = new Map();

/**
 * DTO 클래스명으로 파일 경로를 탐색
 */
export async function resolveDtoPath(dtoClassName, basePath) {
  const pattern = path.join(basePath, `src/main/**/dto/**/${dtoClassName}.java`);
  const files = await glob(pattern);
  return files.length > 0 ? files[0] : null;
}

/**
 * Java DTO 파일에서 필드 정보 추출
 * Lombok class / Java record 두 패턴 모두 지원
 */
export function parseDtoFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf-8');
  const fields = [];

  // Java record 패턴 감지: public record Foo( ... ) {}
  const recordMatch = content.match(/public\s+record\s+\w+\s*\(([\s\S]*?)\)\s*\{/);
  if (recordMatch) {
    return parseRecordFields(recordMatch[1]);
  }

  // Lombok class 패턴: private Type fieldName;
  return parseLombokFields(content);
}

/**
 * Java record 컴포넌트에서 필드 추출
 * 예: @Schema(description = "제목", example = "야구") String title,
 */
function parseRecordFields(recordBody) {
  const fields = [];
  // 각 컴포넌트를 쉼표로 분리 (어노테이션 내부 쉼표 제외)
  const components = splitRecordComponents(recordBody);

  for (const comp of components) {
    const trimmed = comp.trim();
    if (!trimmed) continue;

    const field = extractFieldInfo(trimmed);
    if (field) fields.push(field);
  }

  return fields;
}

/**
 * record 컴포넌트를 쉼표 기준으로 분리 (괄호 depth 고려)
 */
function splitRecordComponents(body) {
  const components = [];
  let current = '';
  let depth = 0;

  for (const ch of body) {
    if (ch === '(' || ch === '{') depth++;
    else if (ch === ')' || ch === '}') depth--;

    if (ch === ',' && depth === 0) {
      components.push(current);
      current = '';
    } else {
      current += ch;
    }
  }
  if (current.trim()) components.push(current);
  return components;
}

/**
 * Lombok 클래스에서 필드 추출
 * private String title; 위에 @Schema 어노테이션
 */
function parseLombokFields(content) {
  const fields = [];
  const lines = content.split('\n');

  let pendingSchema = null;

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i].trim();

    // @Schema 어노테이션 감지 (여러 줄에 걸칠 수 있음)
    if (line.startsWith('@Schema')) {
      pendingSchema = collectAnnotation(lines, i, '@Schema');
    }

    // 필드 선언 감지: private/protected Type fieldName;
    const fieldMatch = line.match(/^private\s+(.+?)\s+(\w+)\s*(?:=.*)?;/);
    if (fieldMatch) {
      const rawType = fieldMatch[1];
      const fieldName = fieldMatch[2];

      const schemaInfo = pendingSchema ? parseSchemaAnnotation(pendingSchema) : {};

      fields.push({
        name: fieldName,
        type: simplifyType(rawType),
        rawType: rawType,
        description: schemaInfo.description || '',
        example: schemaInfo.example || ''
      });
      pendingSchema = null;
    }

    // @Schema가 아닌 다른 어노테이션이면 schema를 유지
    if (!line.startsWith('@') && !fieldMatch && line !== '') {
      pendingSchema = null;
    }
  }

  return fields;
}

/**
 * 단일 컴포넌트(record) 또는 필드에서 정보 추출
 */
function extractFieldInfo(component) {
  // @Schema 어노테이션 추출
  const schemaMatch = component.match(/@Schema\s*\(([^)]*(?:\([^)]*\)[^)]*)*)\)/);
  const schemaInfo = schemaMatch ? parseSchemaAnnotation(schemaMatch[0]) : {};

  // 타입과 이름 추출 (마지막 "Type name" 패턴)
  // 어노테이션 제거 후 파싱
  const cleaned = component.replace(/@\w+\s*(\([^)]*(?:\([^)]*\)[^)]*)*\))?/g, '').trim();
  const parts = cleaned.split(/\s+/);

  if (parts.length < 2) return null;

  const fieldName = parts[parts.length - 1];
  const rawType = parts.slice(0, parts.length - 1).join(' ');

  return {
    name: fieldName,
    type: simplifyType(rawType),
    rawType: rawType,
    description: schemaInfo.description || '',
    example: schemaInfo.example || ''
  };
}

/**
 * 여러 줄에 걸친 어노테이션 수집
 */
function collectAnnotation(lines, startIdx, annotationName) {
  let result = '';
  let depth = 0;
  let started = false;

  for (let i = startIdx; i < lines.length; i++) {
    const line = lines[i].trim();
    result += (result ? ' ' : '') + line;

    for (const ch of line) {
      if (ch === '(') { depth++; started = true; }
      else if (ch === ')') depth--;
    }

    if (started && depth === 0) break;
    if (!started && !line.includes('(')) break;
  }

  return result;
}

/**
 * @Schema(description = "...", example = "...") 파싱
 */
function parseSchemaAnnotation(text) {
  const result = {};

  // description 추출 (텍스트 블록 또는 일반 문자열)
  const descMatch = text.match(/description\s*=\s*(?:"""([\s\S]*?)"""|"([^"]*)")/);
  if (descMatch) {
    result.description = (descMatch[1] || descMatch[2] || '').trim();
  }

  // example 추출
  const exampleMatch = text.match(/example\s*=\s*"([^"]*)"/);
  if (exampleMatch) {
    result.example = exampleMatch[1];
  }

  return result;
}

/**
 * Java 타입 단순화: List<String> → String[], Long → number 등
 */
function simplifyType(rawType) {
  // 제네릭 List/Set 처리
  const listMatch = rawType.match(/(?:List|Set)<(.+?)>/);
  if (listMatch) return `${simplifyType(listMatch[1])}[]`;

  // Map 처리
  const mapMatch = rawType.match(/Map<(.+?),\s*(.+?)>/);
  if (mapMatch) return `Map<${simplifyType(mapMatch[1])}, ${simplifyType(mapMatch[2])}>`;

  // 기본 타입 매핑
  const typeMap = {
    'String': 'String',
    'Long': 'Long',
    'long': 'long',
    'Integer': 'Integer',
    'int': 'int',
    'Boolean': 'Boolean',
    'boolean': 'boolean',
    'Double': 'Double',
    'double': 'double',
    'Float': 'Float',
    'float': 'float',
    'LocalDate': 'LocalDate',
    'LocalDateTime': 'LocalDateTime',
    'LocalTime': 'LocalTime'
  };

  return typeMap[rawType] || rawType;
}

/**
 * 타입 문자열에서 DTO 클래스명 추출 (중첩 파싱용)
 * 예: List<ImageCreateReqDto> → ImageCreateReqDto
 *     JourCreateResDto → JourCreateResDto
 */
function extractDtoClassName(rawType) {
  const listMatch = rawType.match(/(?:List|Set)<(.+?)>/);
  const innerType = listMatch ? listMatch[1] : rawType;

  // DTO 클래스인지 판별 (Dto로 끝나거나 Res/Req 패턴)
  if (innerType.match(/(?:Dto|Result|Res|Req)$/)) {
    return innerType;
  }
  return null;
}

/**
 * 캐시 적용 DTO 파싱 (중첩 DTO depth 2까지 재귀)
 */
export async function getCachedDto(dtoClassName, basePath, depth = 0) {
  if (!dtoClassName || depth > 2) return null;

  const cacheKey = dtoClassName;
  if (dtoCache.has(cacheKey)) return dtoCache.get(cacheKey);

  const filePath = await resolveDtoPath(dtoClassName, basePath);
  if (!filePath) {
    dtoCache.set(cacheKey, null);
    return null;
  }

  const fields = parseDtoFile(filePath);

  // 중첩 DTO 재귀 파싱
  for (const field of fields) {
    const nestedDto = extractDtoClassName(field.rawType);
    if (nestedDto) {
      const nestedFields = await getCachedDto(nestedDto, basePath, depth + 1);
      if (nestedFields) {
        field.nestedFields = nestedFields;
      }
    }
  }

  dtoCache.set(cacheKey, fields);
  return fields;
}

/**
 * 캐시 초기화
 */
export function clearDtoCache() {
  dtoCache.clear();
}
