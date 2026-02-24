# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

InningLog is a Spring Boot 3 REST API for a baseball fan engagement platform (야구 직관 리포트 플랫폼). It enables fans to record game attendance experiences with emotions, seat reviews, and personalized statistics.

**Tech Stack:** Java 17, Spring Boot 3.4.5, Spring Data JPA, Spring Security, MySQL 8, AWS S3

## Build & Development Commands

```bash
# Build
./gradlew build              # Full build
./gradlew build -x test      # Build without tests

# Run
./gradlew bootRun            # Run application (uses local profile)

# Test
./gradlew test               # Run all tests (JUnit 5)

# Clean
./gradlew clean
```

## Environment Setup

환경변수 파일:
- `.env.local` - 로컬 개발용
- `.env` - 운영/배포용

주요 변수:
- Database: `LOCAL_DB_URL`, `LOCAL_DB_USERNAME`, `LOCAL_DB_PASSWORD`
- JWT: `JWT_SECRET_KEY`, `JWT_EXPIRATION`
- Kakao OAuth: `KAKAO_CLIENT_ID`, `KAKAO_REDIRECT_URI`
- AWS S3: `LOCAL_AWS_ACCESS_KEY`, `LOCAL_AWS_SECRET_KEY`, `AWS_REGION`, `LOCAL_AWS_S3_BUCKET`
- Swagger: `SWAGGER_NAME`, `SWAGGER_PW`

Profiles: `local` (default), `dev`, `prod`

## Architecture

### Layer Structure
```
Controller → Facade → Service → Repository
```
- **Controller**: HTTP request/response handling only (separated by operation: Get, Post, Delete, Update)
- **Facade**: Orchestrates multiple services for complex scenarios (optional)
- **Service**: Single Responsibility Principle, one domain per service
- **Repository**: Data persistence via JPA

### Domain Structure
Each domain follows this pattern:
```
domain/[feature]/
├── controller/     # HTTP endpoints
├── service/        # Business logic
├── repository/     # Data access (JPA)
├── domain/         # Entity models
├── dto/
│   ├── req/       # Request DTOs
│   └── res/       # Response DTOs
└── usecase/       # Complex orchestration (optional)
```

### Main Domains
- `journal` - 직관일지 (Game attendance journal)
- `seatView` - 좌석후기 (Seat reviews by zone/section/row)
- `post`, `comment` - 커뮤니티 (Community posts and comments)
- `member` - 회원관리 (User management)
- `like`, `scrap` - 좋아요/스크랩 (Likes and bookmarks)
- `kbo` - KBO 데이터 (Korean Baseball data)
- `kakao` - 카카오 로그인 (Kakao OAuth)
- `contentImage` - 이미지 관리 (S3 presigned URL handling)
- `team`, `stadium` - 팀/경기장 정보

### Global Infrastructure
Located in `src/main/java/com/inninglog/inninglog/global/`:
- `auth/` - JWT & Spring Security configuration
- `config/` - Spring configurations
- `exception/` - Global error handling via `GlobalExceptionHandler`
- `response/` - Unified API response wrapper
- `s3/` - AWS S3 presigned URL integration
- `entity/` - Base JPA entities
- `pageable/` - Pagination utilities

## Code Conventions

### PR/Commit Format
`type : 작업 내용 #이슈번호` 형식 사용:
- `feat` : New feature
- `fix` : Bug fix
- `chore` : Build/config changes
- `docs` : Documentation
- `style` : Formatting (no functional change)
- `refact` : Refactoring
- `test` : Test code

예시: `feat : Journal 댓글 API 추가 #88`

### Branch Naming
`{type}/#{이슈번호}/{설명}` 형식 사용:
```
feat/#88/journal-comment
fix/#42/login-bug
docs/#91/claude-md-update
chore/#93/git-workflow
```

### Git Workflow
```
1. Issue 생성     → gh issue create
2. Branch 생성    → git checkout -b {type}/#{번호}/{설명}
3. 작업 & Commit  → git commit -m "type : 내용 #번호"
4. Push & PR      → git push → gh pr create
5. Merge          → gh pr merge
```

### Key Patterns
- All authenticated endpoints require JWT tokens (via Kakao OAuth)
- Image uploads use S3 presigned URLs (no direct file handling in backend)
- Use Lombok for reducing boilerplate
- DTOs are strictly separated: request objects in `dto/req/`, responses in `dto/res/`

## API Documentation

Swagger UI: `/swagger-ui.html`
- username: `frontend`
- password: `inninglog0909`

API docs 확인:
```bash
curl -s -u "frontend:inninglog0909" "http://localhost:8080/v3/api-docs"
```

## Context Optimization (토큰 절약)

### 파일 읽기 최적화
- 파일 전체 대신 필요한 부분만 읽기: `Read(file, offset=70, limit=20)`
- 한 번 읽은 파일은 재사용, 중복 읽기 방지
- 코드 탐색 시 Explore Agent 활용 → 메인 컨텍스트 오염 방지

### Bash 출력 최적화
```bash
./gradlew test 2>&1 | tail -30      # 마지막 30줄만
./gradlew bootRun &                  # 백그라운드 실행
curl -s ... | head -10               # 응답 일부만
```

### 주요 파일 경로 (탐색 없이 바로 접근)
```
엔티티:        src/main/.../domain/{도메인}/domain/{Entity}.java
컨트롤러:      src/main/.../domain/{도메인}/controller/
서비스:        src/main/.../domain/{도메인}/service/
DTO:          src/main/.../domain/{도메인}/dto/req|res/
테스트:        src/test/.../domain/{도메인}/controller/
기획서:        docs/plan/feature-plan-{기능명}.md
스킬:          .claude/commands/{스킬명}.md
환경변수:      .env.local (로컬), .env (운영)
```

### 자주 사용하는 인터페이스 위치
- `CommentableContent`: domain/comment/domain/
- `LikeableContent`: domain/like/domain/
- `ScrapableContent`: domain/scrap/domain/
- `ContentValidateService`: domain/contentType/

### 서버 테스트 빠른 시작
```bash
# JWT 토큰 (memberId=1, 1시간 유효)
node -e "const c=require('crypto'),s='JWT_SECRET_KEY값',k=Buffer.from(s,'base64'),h=Buffer.from('{\"alg\":\"HS256\",\"typ\":\"JWT\"}').toString('base64url'),p=Buffer.from(JSON.stringify({sub:'1',iat:Math.floor(Date.now()/1000),exp:Math.floor(Date.now()/1000)+3600})).toString('base64url'),g=c.createHmac('sha256',k).update(h+'.'+p).digest('base64url');console.log(h+'.'+p+'.'+g)"
```

### 워크플로우 스킬
- `/plan-feature {기능명}` - 기획서 작성
- `/implement-feature {기능명}` - TDD 구현
- `/test-api {엔드포인트}` - API 테스트
- `/checkpoint` - 세션 종료 전 작업 상태 기록

## 작업 체크포인트

세션 간 작업 상태는 별도 파일에 기록:
→ **`docs/checkpoint.md`**
- `/commit {type} {설명}` - 이슈→브랜치→커밋→PR 자동화
