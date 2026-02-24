/**
 * enriched API 데이터를 Notion 블록 배열로 변환
 */

// === 헬퍼 함수 ===

function richText(content, annotations = {}) {
  return {
    type: 'text',
    text: { content },
    annotations: { bold: false, italic: false, code: false, ...annotations }
  };
}

function heading2(text) {
  return {
    object: 'block',
    type: 'heading_2',
    heading_2: {
      rich_text: [richText(text)]
    }
  };
}

function paragraph(text) {
  return {
    object: 'block',
    type: 'paragraph',
    paragraph: {
      rich_text: [richText(text)]
    }
  };
}

function divider() {
  return {
    object: 'block',
    type: 'divider',
    divider: {}
  };
}

function callout(text, emoji) {
  return {
    object: 'block',
    type: 'callout',
    callout: {
      rich_text: [richText(text)],
      icon: { type: 'emoji', emoji }
    }
  };
}

function bulletItem(text) {
  return {
    object: 'block',
    type: 'bulleted_list_item',
    bulleted_list_item: {
      rich_text: [richText(text)]
    }
  };
}

function bulletItemRich(richTextArray) {
  return {
    object: 'block',
    type: 'bulleted_list_item',
    bulleted_list_item: {
      rich_text: richTextArray
    }
  };
}

/**
 * Notion 테이블 블록 생성
 * @param {string[]} headers - 헤더 행
 * @param {string[][]} rows - 데이터 행들
 */
function table(headers, rows) {
  const tableWidth = headers.length;
  const allRows = [headers, ...rows];

  return {
    object: 'block',
    type: 'table',
    table: {
      table_width: tableWidth,
      has_column_header: true,
      has_row_header: false,
      children: allRows.map(row => ({
        object: 'block',
        type: 'table_row',
        table_row: {
          cells: row.map(cell => [richText(String(cell || ''))])
        }
      }))
    }
  };
}

// === 메인 빌더 ===

/**
 * enriched API 데이터를 Notion 블록 배열로 변환
 */
export function buildApiSpecBlocks(api) {
  const blocks = [];

  // 개요
  blocks.push(heading2('개요'));
  blocks.push(paragraph(api.description || `${api.method} ${api.path}`));
  blocks.push(divider());

  // 인증
  blocks.push(heading2('인증'));
  if (api.requiresAuth) {
    blocks.push(callout('JWT 인증 필요 (Authorization: Bearer 토큰)', '🔒'));
  } else {
    blocks.push(callout('인증 불필요', '🔓'));
  }
  blocks.push(divider());

  // 경로 매개변수 (있을 때만)
  if (api.pathParams && api.pathParams.length > 0) {
    blocks.push(heading2('경로 매개변수'));
    blocks.push(table(
      ['이름', '타입', '설명', '예시'],
      api.pathParams.map(p => [p.name, p.type, p.description, p.example])
    ));
    blocks.push(divider());
  }

  // 쿼리 매개변수 (있을 때만)
  if (api.queryParams && api.queryParams.length > 0) {
    blocks.push(heading2('쿼리 매개변수'));
    blocks.push(table(
      ['이름', '타입', '필수', '기본값', '설명'],
      api.queryParams.map(p => [
        p.name,
        p.type,
        p.required ? 'Y' : 'N',
        p.defaultValue || '-',
        p.description
      ])
    ));
    blocks.push(divider());
  }

  // 요청 본문 (있을 때만)
  if (api.requestBodyFields && api.requestBodyFields.length > 0) {
    blocks.push(heading2('요청 본문'));
    blocks.push(paragraph(`DTO: ${api.requestBodyDto}`));

    const bodyRows = flattenFields(api.requestBodyFields);
    blocks.push(table(
      ['필드명', '타입', '설명', '예시'],
      bodyRows
    ));
    blocks.push(divider());
  }

  // 응답 코드
  if (api.responses && api.responses.length > 0) {
    blocks.push(heading2('응답 코드'));
    for (const resp of api.responses) {
      const codeNum = parseInt(resp.code, 10);
      const icon = codeNum >= 200 && codeNum < 300 ? '✅' : '❌';
      blocks.push(bulletItem(`${icon} ${resp.code} - ${resp.description}`));
    }
    blocks.push(divider());
  }

  // 응답 본문 (Response DTO가 있을 때만)
  if (api.responseFields && api.responseFields.length > 0) {
    blocks.push(heading2('응답 본문'));
    blocks.push(paragraph(`DTO: ${api.responseDtoName}`));

    const resRows = flattenFields(api.responseFields);
    blocks.push(table(
      ['필드명', '타입', '설명', '예시'],
      resRows
    ));
  }

  return blocks;
}

/**
 * 중첩 필드를 플랫 배열로 변환 (prefix 부여)
 */
function flattenFields(fields, prefix = '') {
  const rows = [];

  for (const field of fields) {
    const fieldName = prefix ? `${prefix}.${field.name}` : field.name;
    rows.push([fieldName, field.type, field.description, field.example]);

    if (field.nestedFields && field.nestedFields.length > 0) {
      rows.push(...flattenFields(field.nestedFields, fieldName));
    }
  }

  return rows;
}

/**
 * dry-run용 블록 프리뷰 텍스트 생성
 */
export function previewBlocks(api, blocks) {
  const lines = [];
  lines.push(`\n--- [${api.method}] ${api.path} ---`);
  lines.push(`API명: ${api.name}`);
  lines.push(`블록 수: ${blocks.length}개`);

  for (const block of blocks) {
    const type = block.type;

    if (type === 'heading_2') {
      lines.push(`  [H2] ${block.heading_2.rich_text[0].text.content}`);
    } else if (type === 'paragraph') {
      const text = block.paragraph.rich_text[0].text.content;
      lines.push(`  [P]  ${text.substring(0, 80)}${text.length > 80 ? '...' : ''}`);
    } else if (type === 'callout') {
      const emoji = block.callout.icon.emoji;
      const text = block.callout.rich_text[0].text.content;
      lines.push(`  [${emoji}] ${text}`);
    } else if (type === 'table') {
      const rowCount = block.table.children.length - 1; // 헤더 제외
      lines.push(`  [TABLE] ${rowCount}행`);
    } else if (type === 'bulleted_list_item') {
      const text = block.bulleted_list_item.rich_text[0].text.content;
      lines.push(`  [•] ${text}`);
    } else if (type === 'divider') {
      lines.push(`  [---]`);
    }
  }

  return lines.join('\n');
}
