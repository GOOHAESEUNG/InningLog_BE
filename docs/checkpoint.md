# 작업 체크포인트

> 마지막 업데이트: 2025-01-21

## 현재 진행 중인 작업

### N+1 쿼리 최적화
- **이슈**: #97
- **PR**: #98
- **브랜치**: `refact/#97/n+1-query-optimization` → `feat/#88/댓글응답수정`
- **상태**: PR 생성됨, 리뷰 대기

**완료된 작업:**
- [x] CommentRepository Fetch Join 추가
- [x] LikeRepository 배치 조회 추가
- [x] PostRepository Fetch Join 추가
- [x] 통합 테스트 작성 (`CommentGetServiceN1Test`)
- [x] 쿼리 수 90% 감소 확인 (21개 → 2개)
- [x] 리팩토링 문서 작성 (`docs/refactoring/001-n+1-query-optimization.md`)

---

## 다음 세션 TODO

- [ ] PR #98 머지 (feat/#88으로)
- [ ] feat/#88 브랜치 main으로 머지
- [ ] MemberCredential N+1 최적화 검토
- [ ] 로컬 AWS 설정 또는 조건부 로딩 검토

---

## 발견된 이슈

### 1. MemberCredential N+1
- **원인**: `Member` ↔ `MemberCredential` 양방향 OneToOne 관계
- **증상**: Member 조회 시 각각 Credential 추가 쿼리 발생
- **해결**: 향후 별도 리팩토링

### 2. 로컬 서버 실행 불가
- **원인**: `.env.local`에 `LOCAL_AWS_ACCESS_KEY`, `LOCAL_AWS_SECRET_KEY` 없음
- **증상**: S3 PreSignedPut 서비스 빈 생성 실패
- **우회**: 테스트는 H2 + test 프로파일로 가능

---

## 관련 문서
- 리팩토링 기록: `docs/refactoring/`
- 기능 기획서: `docs/plan/`
