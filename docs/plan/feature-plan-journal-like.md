# 기능 기획서: Journal 좋아요 API

> 작성일: 2025-01-21
> 관련 이슈: #88
> 상태: 구현 완료

## 1. 개요

직관 일지(Journal)에 좋아요 기능을 추가하여 사용자 간 상호작용을 활성화한다.

## 2. 현재 구조 분석

### 좋아요 시스템 핵심 설계
```
Like 엔티티:
- contentType: POST | COMMENT (현재 지원)
- targetId: 대상 콘텐츠 ID
- member: 좋아요를 누른 사용자
```

### 현재 기능 지원 현황

| 기능 | Post | Comment | Journal |
|------|------|---------|---------|
| 좋아요 | O | O | X |
| LikeableContent 인터페이스 | 구현됨 | 구현됨 | 미구현 |

### 기존 좋아요 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/community/posts/{postId}/likes` | 게시글 좋아요 |
| `DELETE` | `/community/posts/{postId}/likes` | 게시글 좋아요 취소 |
| `POST` | `/community/comments/{commentId}/likes` | 댓글 좋아요 |
| `DELETE` | `/community/comments/{commentId}/likes` | 댓글 좋아요 취소 |

### 재사용 가능 요소
- `LikeCreateService`, `LikeDeleteService` - 그대로 사용
- `LikeRepository` - 그대로 사용
- `ContentValidateToLikeService` - JOURNAL 케이스 추가 필요

## 3. API 설계

### 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/journals/{journalId}/likes` | 직관일지 좋아요 |
| `DELETE` | `/journals/{journalId}/likes` | 직관일지 좋아요 취소 |

### Response

**좋아요 성공 Response:**
```json
{
  "code": "SUCCESS",
  "message": "요청이 정상적으로 처리되었습니다.",
  "data": null
}
```

## 4. 구현 계획

### 수정 필요 파일

| 파일 | 작업 내용 |
|------|----------|
| `Journal.java` | LikeableContent 인터페이스 구현, likeCount 필드 추가 |
| `ContentType.java` | JOURNAL이 이미 존재하는지 확인 (존재함) |
| `ContentValidateToLikeService.java` | JOURNAL 케이스 처리 추가 |
| `LikeJournalController.java` | 신규 생성 (POST, DELETE 엔드포인트) |
| `JourDetailResDto.java` | likeCount, likedByMe 필드 추가 |
| `JournalSumListResDto.java` | likeCount 필드 추가 (선택) |

### Journal 엔티티 변경사항

```java
@Entity
public class Journal extends BaseTimeEntity implements CommentableContent, LikeableContent {
    // 기존 필드들...

    // 추가
    @Builder.Default
    private long likeCount = 0;

    @Override
    public void increaseLikeCount() {
        this.likeCount++;
    }

    @Override
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
```

### ContentValidateToLikeService 변경사항

```java
public LikeableContent validateContentToLike(ContentType contentType, Long targetId) {
    if (contentType == ContentType.POST) {
        return postValidateService.getPostById(targetId);
    } else if (contentType == ContentType.COMMENT) {
        return commentValidateService.getCommentById(targetId);
    } else if (contentType == ContentType.JOURNAL) {
        return journalValidateService.getJournalById(targetId);  // 추가
    }
    throw new IllegalArgumentException("지원하지 않는 콘텐츠 타입입니다.");
}
```

### 새 Controller 구조

```java
@RestController
@RequestMapping("/journals/{journalId}/likes")
@RequiredArgsConstructor
@Tag(name = "직관일지 좋아요", description = "직관일지 좋아요 관련 API")
public class LikeJournalController {

    private final LikeCreateService likeCreateService;
    private final LikeDeleteService likeDeleteService;

    @PostMapping
    @Operation(summary = "직관일지 좋아요", description = "직관일지에 좋아요를 추가합니다.")
    public ResponseEntity<SuccessResponse<Void>> likeJournal(
        @PathVariable Long journalId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        likeCreateService.createLike(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }

    @DeleteMapping
    @Operation(summary = "직관일지 좋아요 취소", description = "직관일지의 좋아요를 취소합니다.")
    public ResponseEntity<SuccessResponse<Void>> unlikeJournal(
        @PathVariable Long journalId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        likeDeleteService.deleteLike(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
```

## 5. DTO 변경사항

### JourDetailResDto 변경
```java
public record JourDetailResDto(
    // 기존 필드들...
    long commentCount,
    long likeCount,      // 추가
    boolean likedByMe    // 추가
) {}
```

## 6. 체크리스트

- [x] Journal 엔티티에 LikeableContent 구현
- [x] Journal 엔티티에 likeCount 필드 추가
- [x] ContentValidateService에 JOURNAL 케이스 추가
- [x] LikeJournalController 생성
- [x] JourDetailResDto에 likeCount, likedByMe 추가
- [x] JournalUsecase에서 likedByMe 조회 로직 추가
- [x] API 테스트
- [x] Swagger 문서 확인

## 7. 참고: 댓글 좋아요는 이미 구현됨

Journal 댓글에 대한 좋아요 기능은 이미 구현되어 있습니다:
- `POST /community/comments/{commentId}/likes`
- `DELETE /community/comments/{commentId}/likes`

CommentResDto에 `likedByMe`와 `likeCount` 필드가 이미 포함되어 있어,
Journal 댓글 조회 시 좋아요 정보가 함께 반환됩니다.
