# 리팩토링 001: N+1 쿼리 최적화

> 작성일: 2025-01-21
> 상태: 완료

## 1. 문제 상황

### 현재 코드 (CommentGetService.java:44-58)
```java
for (Comment c : comments) {
    // 1. 각 댓글마다 Member 조회 → N번의 추가 쿼리
    MemberShortResDto memberDto = memberGetService.toMemberShortResDto(c.getMember());

    // 2. 각 댓글마다 Like 여부 조회 → N번의 추가 쿼리
    boolean likedByMe = likeValidateService.likedByMe(ContentType.COMMENT, c.getId(), me);
}
```

### 실제 발생하는 쿼리
댓글 10개 조회 시:
```sql
-- 1번: 댓글 목록 조회
SELECT * FROM comment WHERE content_type = 'JOURNAL' AND target_id = 1;

-- 2~11번: 각 댓글의 Member 조회 (N번)
SELECT * FROM member WHERE id = 1;
SELECT * FROM member WHERE id = 2;
SELECT * FROM member WHERE id = 3;
... (10번 반복)

-- 12~21번: 각 댓글의 Like 여부 조회 (N번)
SELECT COUNT(*) FROM like WHERE content_type = 'COMMENT' AND target_id = 1 AND member_id = ?;
SELECT COUNT(*) FROM like WHERE content_type = 'COMMENT' AND target_id = 2 AND member_id = ?;
... (10번 반복)
```

**총 쿼리 수: 1 + 10 + 10 = 21개** (댓글 10개 기준)

---

## 2. 원인 분석

### N+1 문제란?
```
N+1 = 1(목록 조회) + N(각 항목별 추가 조회)
```

JPA에서 연관 엔티티를 **LAZY 로딩**으로 설정하면, 실제로 해당 필드에 접근할 때 쿼리가 실행됩니다.

```java
@Entity
public class Comment {
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY = 접근할 때 조회
    private Member member;
}
```

### 왜 LAZY를 쓰는가?
```java
// EAGER (즉시 로딩) - 항상 JOIN
Comment comment = commentRepository.findById(1L);
// → SELECT c.*, m.* FROM comment c JOIN member m ...
// → Member가 필요 없어도 항상 조회됨 (낭비)

// LAZY (지연 로딩) - 필요할 때만 조회
Comment comment = commentRepository.findById(1L);
// → SELECT * FROM comment WHERE id = 1
comment.getMember().getNickname();  // 이 시점에 Member 조회
// → SELECT * FROM member WHERE id = ?
```

LAZY가 기본적으로 좋지만, **목록 조회 + 반복문**에서 N+1 발생.

### 왜 문제인가?
| 댓글 수 | 쿼리 수 | DB 왕복 | 응답 시간 |
|--------|--------|--------|----------|
| 10개 | 21개 | 21회 | ~200ms |
| 50개 | 101개 | 101회 | ~1000ms |
| 100개 | 201개 | 201회 | ~2000ms |

**DB 왕복 비용이 선형으로 증가** → 성능 저하의 주요 원인

---

## 3. 해결 방안

### 방안 1: Fetch Join (권장)
```java
// Before
@Query("SELECT c FROM Comment c WHERE c.targetId = :targetId")
List<Comment> findByTargetId(Long targetId);

// After - Member를 한 번에 조회
@Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.targetId = :targetId")
List<Comment> findByTargetIdWithMember(Long targetId);
```

**결과:**
```sql
-- 단 1번의 쿼리로 Comment + Member 조회
SELECT c.*, m.*
FROM comment c
JOIN member m ON c.member_id = m.id
WHERE c.target_id = 1;
```

### 방안 2: Batch Size (대안)
```java
// application.yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100
```

**결과:**
```sql
-- 1번: 댓글 조회
SELECT * FROM comment WHERE target_id = 1;

-- 2번: Member IN 쿼리로 한 번에 조회
SELECT * FROM member WHERE id IN (1, 2, 3, 4, 5, ...);
```

### 방안 3: Like 배치 조회
```java
// Before - 각각 조회
for (Comment c : comments) {
    boolean likedByMe = likeRepository.existsBy...(c.getId(), member);
}

// After - 한 번에 조회
List<Long> commentIds = comments.stream().map(Comment::getId).toList();
Set<Long> likedIds = likeRepository.findLikedTargetIds(ContentType.COMMENT, commentIds, member);

for (Comment c : comments) {
    boolean likedByMe = likedIds.contains(c.getId());
}
```

```java
// Repository 추가
@Query("SELECT l.targetId FROM Like l WHERE l.contentType = :type AND l.targetId IN :ids AND l.member = :member")
Set<Long> findLikedTargetIds(ContentType type, List<Long> ids, Member member);
```

---

## 4. 성능 비교 (실제 테스트 결과)

### 테스트 환경
- H2 In-Memory Database
- 댓글 10개, 각각 다른 Member 작성
- `CommentGetServiceN1Test.java` 통합 테스트

### 실제 실행된 쿼리

**Before (최적화 전)**
```
댓글 10개 조회 시:
1. 댓글 목록 조회: 1개
2. 각 댓글의 Member 조회: 10개
3. 각 댓글의 Like 여부: 10개
총: 21개 쿼리
```

**After (최적화 후)**
```sql
-- 1. Comment + Member Fetch Join (1개)
SELECT c.*, m.* FROM comment c
JOIN member m ON m.id = c.member_id
WHERE c.content_type = ? AND c.target_id = ?

-- 2. Like 배치 조회 IN 쿼리 (1개)
SELECT l.target_id FROM content_like l
WHERE l.content_type = ? AND l.target_id IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) AND l.member_id = ?

총: 2개 쿼리 ✅
```

### 개선 효과

| 항목 | Before | After | 개선율 |
|------|--------|-------|--------|
| 댓글 10개 | 21개 쿼리 | **2개** | 90% 감소 |
| 댓글 50개 | 101개 쿼리 | **2개** | 98% 감소 |
| 댓글 100개 | 201개 쿼리 | **2개** | 99% 감소 |

### 추가 발견된 문제

`Member` → `MemberCredential` N+1 문제가 추가로 발견됨:
```java
// Member.java
@OneToOne(mappedBy = "member")
private MemberCredential credential;
```
이 문제는 MemberCredential이 양방향 OneToOne 관계이기 때문에 발생.
→ 향후 별도 리팩토링에서 해결 예정

---

## 5. 수정된 파일

| 파일 | 수정 내용 |
|------|----------|
| `CommentRepository.java` | `findAllWithMemberByContentTypeAndTargetId()` Fetch Join 쿼리 추가 |
| `LikeRepository.java` | `findLikedTargetIds()` 배치 조회 쿼리 추가 |
| `LikeValidateService.java` | `findLikedTargetIds()` 배치 조회 메서드 추가 |
| `CommentGetService.java` | Fetch Join + 배치 조회 로직으로 변경 |
| `PostRepository.java` | `findWithMemberByTeamShortCode()` Fetch Join 쿼리 추가 |
| `PostGetService.java` | Fetch Join 쿼리 사용으로 변경 |

---

## 6. 체크리스트

- [x] CommentRepository Fetch Join 추가
- [x] LikeRepository 배치 조회 추가
- [x] LikeValidateService 배치 조회 메서드 추가
- [x] CommentGetService 수정
- [x] PostRepository Fetch Join 추가
- [x] PostGetService 수정
- [x] 빌드 통과
- [x] 통합 테스트 작성 (`CommentGetServiceN1Test.java`)
- [x] 성능 테스트 실행 및 쿼리 수 확인

---

## 7. 참고 자료

### JPA Fetch 전략
```
EAGER: 항상 즉시 로딩 (거의 사용 안 함)
LAZY: 접근 시 로딩 (기본값, N+1 주의)
Fetch Join: 명시적으로 한 번에 로딩 (권장)
Batch Size: IN 쿼리로 묶어서 로딩
```

### 언제 어떤 방법을 쓰는가?
```
단건 조회 → LAZY 그대로 OK
목록 조회 + 연관 엔티티 필요 → Fetch Join
목록 조회 + 여러 연관 엔티티 → Batch Size + Fetch Join 조합
존재 여부만 확인 (Like/Scrap) → 배치 IN 쿼리
```
