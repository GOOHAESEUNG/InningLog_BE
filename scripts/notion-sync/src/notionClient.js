import { Client } from '@notionhq/client';

export class NotionClient {
  constructor(apiKey, databaseId) {
    this.notion = new Client({ auth: apiKey });
    this.databaseId = databaseId;
  }

  /**
   * 기존 API 목록 조회 (중복 체크용)
   */
  async getExistingApis() {
    const existing = new Map();
    let hasMore = true;
    let startCursor = undefined;

    while (hasMore) {
      const response = await this.notion.databases.query({
        database_id: this.databaseId,
        start_cursor: startCursor,
        page_size: 100
      });

      for (const page of response.results) {
        const props = page.properties;
        const method = props['HTTP Method']?.select?.name || '';
        const path = props['API 주소']?.rich_text?.[0]?.plain_text || '';
        const key = `${method}:${path}`;

        existing.set(key, {
          pageId: page.id,
          name: props['API명']?.title?.[0]?.plain_text || '',
          domain: props['도메인']?.select?.name || '',
          method,
          path
        });
      }

      hasMore = response.has_more;
      startCursor = response.next_cursor;
    }

    return existing;
  }

  /**
   * 새 API 페이지 생성
   */
  async createApi(api) {
    return await this.notion.pages.create({
      parent: { database_id: this.databaseId },
      properties: this.buildProperties(api)
    });
  }

  /**
   * 기존 API 페이지 업데이트
   */
  async updateApi(pageId, api) {
    return await this.notion.pages.update({
      page_id: pageId,
      properties: this.buildProperties(api)
    });
  }

  /**
   * Notion 속성 객체 생성
   */
  buildProperties(api) {
    return {
      'API명': {
        title: [{ text: { content: api.name } }]
      },
      '도메인': {
        select: { name: api.domain }
      },
      'HTTP Method': {
        select: { name: api.method }
      },
      'API 주소': {
        rich_text: [{ text: { content: api.path } }]
      }
    };
  }
}
