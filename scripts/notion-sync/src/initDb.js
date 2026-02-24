import 'dotenv/config';
import { Client } from '@notionhq/client';

const NOTION_API_KEY = process.env.NOTION_API_KEY;
const NOTION_DATABASE_ID = process.env.NOTION_DATABASE_ID;

async function initDatabase() {
  console.log('=== Notion 데이터베이스 속성 초기화 ===\n');

  if (!NOTION_API_KEY || !NOTION_DATABASE_ID) {
    console.error('오류: NOTION_API_KEY 또는 NOTION_DATABASE_ID가 설정되지 않았습니다.');
    process.exit(1);
  }

  const notion = new Client({ auth: NOTION_API_KEY });

  // 1. 현재 데이터베이스 속성 조회
  console.log('1. 현재 속성 조회 중...');
  const db = await notion.databases.retrieve({ database_id: NOTION_DATABASE_ID });

  // 기존 제목 속성 이름 찾기
  let titlePropertyName = null;
  for (const [name, prop] of Object.entries(db.properties)) {
    if (prop.type === 'title') {
      titlePropertyName = name;
      break;
    }
  }
  console.log(`   기존 제목 속성: "${titlePropertyName}"\n`);

  // 2. 데이터베이스 속성 업데이트
  console.log('2. 속성 업데이트 중...');

  const properties = {
    '도메인': {
      select: {
        options: [
          { name: 'journal', color: 'blue' },
          { name: 'post', color: 'green' },
          { name: 'comment', color: 'yellow' },
          { name: 'member', color: 'orange' },
          { name: 'like', color: 'red' },
          { name: 'scrap', color: 'pink' },
          { name: 'seatView', color: 'purple' },
          { name: 'kbo', color: 'gray' },
          { name: 'kakao', color: 'brown' },
          { name: 'team', color: 'default' },
          { name: 'contentImage', color: 'blue' },
          { name: 'home', color: 'green' },
          { name: 'unknown', color: 'gray' }
        ]
      }
    },
    'HTTP Method': {
      select: {
        options: [
          { name: 'GET', color: 'blue' },
          { name: 'POST', color: 'green' },
          { name: 'PATCH', color: 'yellow' },
          { name: 'PUT', color: 'orange' },
          { name: 'DELETE', color: 'red' }
        ]
      }
    },
    'API 주소': {
      rich_text: {}
    },
    '프론트 담당자': {
      people: {}
    },
    '백엔드 담당자': {
      people: {}
    },
    '상태': {
      status: {}
    }
  };

  // 기존 제목 속성 이름을 'API명'으로 변경
  if (titlePropertyName && titlePropertyName !== 'API명') {
    properties[titlePropertyName] = {
      name: 'API명',
      title: {}
    };
  }

  await notion.databases.update({
    database_id: NOTION_DATABASE_ID,
    properties
  });

  console.log('\n✅ 데이터베이스 속성 설정 완료!\n');
  console.log('생성된 속성:');
  console.log('  - API명 (제목)');
  console.log('  - 도메인 (선택)');
  console.log('  - HTTP Method (선택)');
  console.log('  - API 주소 (텍스트)');
  console.log('  - 프론트 담당자 (사람)');
  console.log('  - 백엔드 담당자 (사람)');
  console.log('  - 상태 (상태)');
}

initDatabase().catch(err => {
  console.error('오류 발생:', err.message);
  process.exit(1);
});
