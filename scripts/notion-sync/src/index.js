import 'dotenv/config';
import path from 'path';
import { fileURLToPath } from 'url';
import { parseControllers } from './parser.js';
import { NotionClient } from './notionClient.js';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const PROJECT_ROOT = path.resolve(__dirname, '../../..');

const NOTION_API_KEY = process.env.NOTION_API_KEY;
const NOTION_DATABASE_ID = process.env.NOTION_DATABASE_ID;

const isDryRun = process.argv.includes('--dry-run');

async function main() {
  console.log('=== API → Notion 동기화 시작 ===\n');

  if (!NOTION_API_KEY || !NOTION_DATABASE_ID) {
    console.error('오류: NOTION_API_KEY 또는 NOTION_DATABASE_ID가 설정되지 않았습니다.');
    console.error('.env 파일을 확인해주세요.');
    process.exit(1);
  }

  // 1. 컨트롤러 파싱
  console.log('1. 컨트롤러 파일 파싱 중...');
  const apis = await parseControllers(PROJECT_ROOT);
  console.log(`   → ${apis.length}개 API 발견\n`);

  if (isDryRun) {
    console.log('[DRY-RUN 모드] 발견된 API 목록:\n');
    apis.forEach((api, i) => {
      console.log(`${i + 1}. [${api.method}] ${api.path}`);
      console.log(`   도메인: ${api.domain}`);
      console.log(`   API명: ${api.name}\n`);
    });
    return;
  }

  // 2. Notion 클라이언트 초기화
  const notion = new NotionClient(NOTION_API_KEY, NOTION_DATABASE_ID);

  // 3. 기존 API 조회
  console.log('2. Notion 기존 데이터 조회 중...');
  const existing = await notion.getExistingApis();
  console.log(`   → ${existing.size}개 기존 API 확인\n`);

  // 4. 동기화
  console.log('3. 동기화 진행 중...');
  let created = 0;
  let updated = 0;
  let skipped = 0;

  for (const api of apis) {
    const key = `${api.method}:${api.path}`;
    const existingApi = existing.get(key);

    if (!existingApi) {
      // 신규 생성
      await notion.createApi(api);
      console.log(`   ✅ 생성: [${api.method}] ${api.path}`);
      created++;
    } else if (existingApi.name !== api.name || existingApi.domain !== api.domain) {
      // 변경 사항 있으면 업데이트
      await notion.updateApi(existingApi.pageId, api);
      console.log(`   🔄 업데이트: [${api.method}] ${api.path}`);
      updated++;
    } else {
      skipped++;
    }
  }

  console.log(`\n=== 동기화 완료 ===`);
  console.log(`생성: ${created}개`);
  console.log(`업데이트: ${updated}개`);
  console.log(`변경없음: ${skipped}개`);
}

main().catch(err => {
  console.error('오류 발생:', err.message);
  process.exit(1);
});
