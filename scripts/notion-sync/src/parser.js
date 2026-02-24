import fs from 'fs';
import path from 'path';
import { glob } from 'glob';

/**
 * Java 컨트롤러 파일에서 API 정보를 추출
 */
export async function parseControllers(basePath) {
  const controllerPattern = path.join(basePath, 'src/main/**/controller/**/*.java');
  const files = await glob(controllerPattern);

  const apis = [];

  for (const file of files) {
    const content = fs.readFileSync(file, 'utf-8');
    const fileApis = parseControllerFile(content, file);
    apis.push(...fileApis);
  }

  return apis;
}

/**
 * 단일 컨트롤러 파일 파싱
 */
function parseControllerFile(content, filePath) {
  const apis = [];

  // 도메인 추출 (패키지 경로에서)
  const domainMatch = filePath.match(/domain\/([^/]+)\/controller/);
  const domain = domainMatch ? domainMatch[1] : 'unknown';

  // 클래스 레벨 @RequestMapping 경로 추출
  const classPathMatch = content.match(/@RequestMapping\s*\(\s*["']([^"']+)["']\s*\)/);
  const classPath = classPathMatch ? classPathMatch[1] : '';

  // 각 메서드의 API 정보 추출
  const methodPattern = /@Operation\s*\(\s*summary\s*=\s*"([^"]+)"[\s\S]*?\)[\s\S]*?@(Get|Post|Patch|Put|Delete)Mapping\s*\(?\s*(?:value\s*=\s*)?["']?([^"'\)\s,]*)["']?\s*\)?/g;

  let match;
  while ((match = methodPattern.exec(content)) !== null) {
    const summary = match[1];
    const method = match[2].toUpperCase();
    const methodPath = match[3] || '';

    // 전체 경로 조합
    let fullPath = classPath;
    if (methodPath) {
      fullPath = classPath.endsWith('/')
        ? classPath + methodPath.replace(/^\//, '')
        : classPath + (methodPath.startsWith('/') ? methodPath : '/' + methodPath);
    }

    apis.push({
      name: summary,
      domain: domain,
      method: method,
      path: fullPath || '/'
    });
  }

  // @Operation 없이 매핑만 있는 경우도 처리
  const simpleMappingPattern = /@(Get|Post|Patch|Put|Delete)Mapping\s*\(?\s*(?:value\s*=\s*)?["']?([^"'\)\s,]*)["']?\s*\)?/g;
  const existingPaths = new Set(apis.map(a => `${a.method}:${a.path}`));

  while ((match = simpleMappingPattern.exec(content)) !== null) {
    const method = match[1].toUpperCase();
    const methodPath = match[2] || '';

    let fullPath = classPath;
    if (methodPath) {
      fullPath = classPath.endsWith('/')
        ? classPath + methodPath.replace(/^\//, '')
        : classPath + (methodPath.startsWith('/') ? methodPath : '/' + methodPath);
    }

    const key = `${method}:${fullPath || '/'}`;
    if (!existingPaths.has(key)) {
      // @Operation이 없는 경우 메서드명에서 추론 시도
      const methodNameMatch = content.substring(Math.max(0, match.index - 200), match.index + match[0].length + 100)
        .match(/public\s+\S+\s+(\w+)\s*\(/);

      apis.push({
        name: methodNameMatch ? camelToTitle(methodNameMatch[1]) : `${method} ${fullPath}`,
        domain: domain,
        method: method,
        path: fullPath || '/'
      });
      existingPaths.add(key);
    }
  }

  return apis;
}

/**
 * camelCase를 Title Case로 변환
 */
function camelToTitle(str) {
  return str
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, s => s.toUpperCase())
    .trim();
}
