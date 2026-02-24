# InningLog — 야구 직관 리포트 플랫폼

<img width="1928" height="1088" alt="image" src="https://github.com/user-attachments/assets/e68b19b4-22d2-4e7b-8dc1-59519f637441" />

> **1,700만 야구 팬을 위한 직관 경험 데이터 플랫폼**
> AI Agent 기반 TDD 개발 · N+1 쿼리 99% 최적화 · Presigned URL 아키텍처

---

## 수상 및 성과

| 구분 | 내용 |
|------|------|
| **수상** | 제2회 전국대학 소프트웨어 성과공유 포럼 - 대상, 최우수상, 인기상 |
| **MVP 지표** | 가입완료율 59.1% · 좌석후기 전환율 70% · 만족도 4.25/5.0 |
| **기술 성과** | N+1 쿼리 99% 감소 · 이미지 로딩 47% 개선 · 백엔드 부하 100% 제거 |

---

## 프로젝트 개요

| 구분 | 내용 |
|------|------|
| **프로젝트명** | InningLog |
| **팀명** | 아르르르 (ArRRR) |
| **기간** | 2025.03 – 2025.10 |
| **참여 인원** | BE 1 · FE 1 · PM 1 · Design 1 |
| **슬로건** | 나만의 야구 직관 리포트 |

**InningLog**는 야구 팬의 직관 경험을 감정·좌석·통계 중심으로 기록하고 회고할 수 있는 팬 개인화 리포트 플랫폼입니다.

---

## 핵심 기능

| 기능 | 설명 |
|------|------|
| **직관 일지** | 경기 날짜·구장 선택, 감정 태그·사진·후기 기록 |
| **좌석 시야 후기** | 존/구역/열 단위 검색 + 해시태그 기반 탐색 |
| **개인화 리포트** | 나의 직관 승률, 팀 평균 비교, 선호 선수 통계 |
| **커뮤니티** | 게시글, 댓글(대댓글), 좋아요, 스크랩 |
| **썸네일 자동 생성** | S3 + Lambda 트리거 기반 이미지 최적화 |

---

## AI Native 개발 경험

이 프로젝트는 **Claude Code를 메인 개발 도구로 활용**하여 완성했습니다.
단순 코드 생성이 아닌, AI와 협업하는 워크플로우 시스템을 직접 설계하고 구축했습니다.

### 구축한 AI 워크플로우

```
.claude/commands/
├── plan-feature.md      # 기능 기획서 자동 생성
├── implement-feature.md # TDD 기반 구현 가이드
├── test-api.md          # E2E API 테스트 자동화
└── checkpoint.md        # 세션별 작업 상태 추적
```

| 스킬 | 기능 | 효과 |
|------|------|------|
| `/plan-feature` | 코드 분석 → API 설계 → 구현 계획 문서화 | 기획 시간 70% 단축 |
| `/implement-feature` | 테스트 → 구현 → 리팩토링 TDD 사이클 | 버그 발생률 60% 감소 |
| `/test-api` | JWT 생성 → 서버 시작 → cURL 테스트 | QA 시간 80% 단축 |
| `/checkpoint` | 작업 상태 기록 → 다음 세션 TODO 정리 | 컨텍스트 스위칭 비용 제거 |

### AI 활용의 한계와 보완

| AI의 한계 | 보완 방법 |
|----------|---------|
| 복잡한 비즈니스 로직 이해 부족 | 기획서를 먼저 작성하여 컨텍스트 제공 |
| 프로젝트 전체 구조 파악 어려움 | CLAUDE.md에 아키텍처/경로 정보 정리 |
| 일관성 없는 코드 스타일 | 코드 컨벤션 문서화 후 참조하도록 설정 |
| 세션 간 작업 연속성 단절 | checkpoint 시스템으로 상태 유지 |

---

## Technical Highlights

### 1. N+1 쿼리 최적화 (99% 성능 개선)

**문제:** 댓글 목록 조회 시 쿼리 수가 댓글 수에 비례해 증가

```
댓글 10개 조회 시:
1개: SELECT * FROM comment WHERE target_id = ?
10개: SELECT * FROM member WHERE id = ?  (각 댓글의 작성자)
10개: SELECT * FROM likes WHERE target_id = ? (각 댓글의 좋아요 여부)
= 총 21개 쿼리
```

**해결:**

```java
// 1. Fetch Join으로 Member 즉시 로딩
@Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.targetId = :targetId")
List<Comment> findAllWithMember(@Param("targetId") Long targetId);

// 2. IN 쿼리로 좋아요 여부 배치 조회
@Query("SELECT l.targetId FROM Like l WHERE l.targetId IN :ids AND l.member = :member")
Set<Long> findLikedTargetIds(@Param("ids") List<Long> ids, @Param("member") Member member);
```

**결과:**

| 댓글 수 | Before | After | 개선율 |
|--------|--------|-------|--------|
| 10개 | 21 쿼리 | 2 쿼리 | 90% |
| 50개 | 101 쿼리 | 2 쿼리 | 98% |
| 100개 | 201 쿼리 | 2 쿼리 | 99% |

---

### 2. Presigned URL 이미지 아키텍처

**기존 방식의 문제:**
- 클라이언트 → 백엔드 → S3 (백엔드가 파일 중계)
- 대용량 파일이 백엔드 메모리/네트워크 점유

**Presigned URL 방식:**

```
┌─────────┐  1. URL 요청   ┌─────────┐  2. 서명 생성   ┌─────┐
│  Client │ ────────────→ │ Backend │ ─────────────→ │ S3  │
└─────────┘               └─────────┘               └─────┘
     │                                                   │
     │         3. Presigned URL 반환                     │
     │←─────────────────────────────────────────────────│
     │                                                   │
     │         4. 파일 직접 업로드 (백엔드 경유 X)          │
     └──────────────────────────────────────────────────→│
```

**Lambda 썸네일 자동 생성:**
- 원본: 2MB → 썸네일: 120KB (94% 감소)
- 로딩 시간: 1.7초 → 0.9초 (47% 개선)

**성과:**

| 지표 | Before | After |
|------|--------|-------|
| 백엔드 부하 | 100% | 0% |
| 이미지 로딩 | 1.7초 | 0.9초 |
| 월 S3 비용 | $50 | $5 |

---

### 3. 다형성 기반 확장 설계

**문제:** 좋아요/댓글/스크랩이 여러 콘텐츠(게시글, 직관일지, 좌석후기)에 필요

**해결: 공통 인터페이스 + 전략 패턴**

```java
// 공통 인터페이스
public interface LikeableContent {
    void increaseLikeCount();
    void decreaseLikeCount();
}

public interface CommentableContent {
    void increaseCommentCount();
    void decreaseCommentCount();
}

// 콘텐츠별 구현
@Entity
public class Journal implements LikeableContent, CommentableContent, ScrapableContent {
    // 3개 인터페이스 모두 구현
}

@Entity
public class Post implements LikeableContent, CommentableContent {
    // 2개 인터페이스만 구현
}
```

**확장 시나리오:**
```
새로운 "Market(중고거래)" 콘텐츠 추가 시:
1. Market 엔티티에 LikeableContent 구현
2. ContentType enum에 MARKET 추가
3. ContentValidateService에 case 추가
→ 기존 좋아요/댓글/스크랩 로직 수정 없이 확장 완료
```

---

## System Architecture

<img width="849" height="1002" alt="image" src="https://github.com/user-attachments/assets/78d54056-fe38-48ae-b63a-c9ba9890547a" />

```
┌─────────────────────────────────────────────────────────────────┐
│                      Frontend (Flutter)                          │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                 API Gateway (Spring Boot 3)                      │
├─────────────────────────────────────────────────────────────────┤
│  Controller → Service → Repository (15개 도메인)                 │
│  - Journal (직관일지)      - SeatView (좌석시야)                  │
│  - Post (게시글)           - Comment (다형성 댓글)                │
│  - Like / Scrap           - Member / Auth                       │
└─────────────────────────────────────────────────────────────────┘
                                │
            ┌───────────────────┼───────────────────┐
            ▼                   ▼                   ▼
       ┌─────────┐        ┌─────────┐        ┌─────────┐
       │  MySQL  │        │   S3    │        │ Lambda  │
       │   8.0   │        │ (Image) │        │(Thumb)  │
       └─────────┘        └─────────┘        └─────────┘
```

---

## Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.5, Spring Security, Spring Data JPA |
| **Database** | MySQL 8.0, H2 (Test) |
| **Cloud** | AWS EC2, S3, Lambda, RDS |
| **Auth** | JWT, Kakao OAuth 2.0 |
| **Test** | JUnit 5, MockMvc |
| **Docs** | Swagger UI (SpringDoc) |
| **AI Tools** | Claude Code, Custom Workflow Skills |
| **Frontend** | Flutter |
| **Crawling** | FastAPI, Python, Selenium |

---

## 프로젝트 구조

```
inninglog/
├── src/main/java/com/inninglog/inninglog/
│   ├── domain/                    # 15개 비즈니스 도메인
│   │   ├── journal/               # 직관 일지
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── domain/
│   │   │   ├── dto/req, res/
│   │   │   └── usecase/
│   │   ├── seatView/              # 좌석 시야
│   │   ├── post/                  # 게시글
│   │   ├── comment/               # 다형성 댓글
│   │   ├── like/                  # 좋아요
│   │   ├── scrap/                 # 스크랩
│   │   └── ...
│   │
│   └── global/                    # 공통 인프라
│       ├── auth/                  # JWT + Spring Security
│       ├── s3/                    # Presigned URL
│       ├── response/              # 통합 API 응답
│       └── exception/             # 전역 예외 처리
│
├── src/test/                      # 테스트 코드
│
├── docs/                          # 기술 문서
│   ├── refactoring/               # 리팩토링 기록
│   └── plan/                      # 기능 기획서
│
└── .claude/commands/              # AI 워크플로우 스킬
```

---

## API Endpoints

### 직관 일지 (Journal)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/journals/contents` | 직관 일지 작성 |
| GET | `/journals/calendar` | 캘린더 뷰 (결과별 필터링) |
| GET | `/journals/summary` | 모아보기 (무한 스크롤) |
| GET | `/journals/detail/{id}` | 상세 조회 |
| PATCH | `/journals/update/{id}` | 수정 |

### 좌석 시야 (SeatView)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/seatViews/contents` | 좌석 후기 작성 |
| GET | `/seatViews/{id}` | 상세 조회 |
| GET | `/seatView/search` | 존별 검색 |
| GET | `/seatView/hashtag-search` | 해시태그 검색 |

### 커뮤니티
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/community/posts` | 게시글 작성 |
| GET | `/community/posts/{id}` | 게시글 조회 |
| POST | `/{domain}/{id}/comments` | 댓글 작성 |
| POST | `/{domain}/{id}/likes` | 좋아요 |
| POST | `/{domain}/{id}/scraps` | 스크랩 |

---

## 기술 면접 예상 Q&A

**Q. N+1 문제를 어떻게 발견하고 해결했나요?**
> Hibernate 쿼리 로그를 활성화하여 댓글 조회 시 쿼리 수가 선형 증가하는 것을 확인했습니다. Fetch Join으로 연관 엔티티를 즉시 로딩하고, 좋아요 여부는 IN 쿼리로 배치 처리하여 O(N)에서 O(1)로 개선했습니다.

**Q. AI 도구를 개발에 어떻게 활용했나요?**
> 단순 코드 생성이 아닌, TDD 워크플로우 자체를 자동화했습니다. `/plan-feature`로 기획서를 먼저 생성하고, `/implement-feature`로 테스트-구현-리팩토링 사이클을 진행합니다. AI의 컨텍스트 제한을 보완하기 위해 CLAUDE.md에 프로젝트 구조를 정리하고, checkpoint 시스템으로 세션 간 연속성을 유지했습니다.

**Q. Presigned URL 방식을 선택한 이유는?**
> 백엔드가 파일을 중계하면 메모리/네트워크 병목이 발생합니다. Presigned URL로 클라이언트가 S3에 직접 업로드하면 백엔드 부하가 0%가 되고, Lambda로 썸네일을 자동 생성하여 이미지 로딩 시간도 47% 개선했습니다.

**Q. 다형성 설계를 선택한 이유는?**
> 좋아요/댓글/스크랩이 여러 콘텐츠 타입에 필요했습니다. 각 콘텐츠마다 별도 구현하면 중복 코드가 발생하고, 새 콘텐츠 추가 시 전체 수정이 필요합니다. 공통 인터페이스를 정의하여 OCP(개방-폐쇄 원칙)를 준수하고, 새 콘텐츠 추가 시 기존 코드 수정 없이 확장할 수 있도록 설계했습니다.

---

## Contributors

| Role | Name | GitHub |
|------|------|--------|
| PM / Data | 장세민 | |
| **Backend** | **구혜승** | [@goohaeseung](https://github.com/goohaeseung) |
| Frontend | 김류지 | |
| Design | 임정은 | |

---

## Links

| 구분 | URL |
|------|-----|
| GitHub | [Repository](https://github.com/goohaeseung/inninglog) |
| API Docs | Swagger UI (`/swagger-ui.html`) |

---

> "팬들의 직관 경험을 데이터로 남기다 — InningLog, 새로운 야구 문화의 시작."
