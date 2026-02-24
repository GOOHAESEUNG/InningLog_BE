# 기능 기획서: Journal 댓글 API

> 작성일: 2025-01-21
> 관련 이슈: #88
> 상태: 구현 완료

## 1. 개요

직관 일지(Journal)에 댓글 기능을 추가하여 사용자 간 소통을 활성화한다.

## 2. 현재 구조 분석

### 댓글 시스템 핵심 설계
```
Comment 엔티티:
- contentType: POST | JOURNAL | MARKET | COMMENT (다형성 지원)
- targetId: 대상 콘텐츠 ID
- rootCommentId: 대댓글 지원 (null이면 최상위 댓글)
```

### 현재 기능 지원 현황

| 기능 | Post | Journal |
|------|------|---------|
| 댓글 | O | X |
| 좋아요 | O | X |
| 스크랩 | O | X |
| CommentableContent 인터페이스 | 구현됨 | 미구현 |

### 재사용 가능 요소
- `ContentType.JOURNAL` - 이미 enum에 존재
- `CommentUsecase`, `CommentCreateService`, `CommentGetService`, `CommentDeleteService` - 그대로 사용
- `CommentRepository` - 그대로 사용
- 모든 Comment DTO - 그대로 사용

## 3. API 설계

### 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/journals/{journalId}/comments` | 댓글/대댓글 생성 |
| `GET` | `/journals/{journalId}/comments` | 댓글 목록 조회 (트리 구조) |
| `DELETE` | `/journals/{journalId}/comments/{commentId}` | 댓글 삭제 (soft delete) |

### Request/Response

**댓글 생성 Request:**
```json
{
  "rootCommentId": null,
  "content": "댓글 내용"
}
```
- `rootCommentId`: null이면 최상위 댓글, 값이 있으면 대댓글

**댓글 목록 Response:**
```json
{
  "comments": [
    {
      "commentId": 1,
      "memberShortResDto": {
        "memberId": 1,
        "nickname": "닉네임",
        "profileUrl": "https://..."
      },
      "writedByMe": false,
      "content": "댓글 내용",
      "commentAt": "2025-01-21 14:30",
      "likeCount": 5,
      "likedByMe": true,
      "isDeleted": false,
      "replies": []
    }
  ]
}
```

## 4. 구현 계획

### 수정 필요 파일

| 파일 | 작업 내용 |
|------|----------|
| `Journal.java` | CommentableContent 인터페이스 구현, commentCount 필드 추가 |
| `ContentValidateService.java` | JOURNAL 케이스 처리 추가 |
| `JournalValidateService.java` | 신규 생성 또는 기존 서비스에 검증 메서드 추가 |
| `CommentJournalController.java` | 신규 생성 (POST, GET, DELETE 엔드포인트) |
| `JourDetailResDto.java` | commentCount 필드 추가 |
| `JournalSumListResDto.java` | commentCount 필드 추가 (선택) |

### Journal 엔티티 변경사항

```java
@Entity
public class Journal extends BaseTimeEntity implements CommentableContent {
    // 기존 필드들...

    // 추가
    private long commentCount;

    @Override
    public void increaseCommentCount() {
        this.commentCount++;
    }

    @Override
    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}
```

### ContentValidateService 변경사항

```java
public CommentableContent validateContentToComment(ContentType contentType, Long targetId) {
    if (contentType == ContentType.POST) {
        return postValidateService.getPostById(targetId);
    } else if (contentType == ContentType.JOURNAL) {
        return journalValidateService.getJournalById(targetId);  // 추가
    }
    throw new IllegalArgumentException("지원하지 않는 콘텐츠 타입입니다.");
}
```

### 새 Controller 구조

```java
@RestController
@RequestMapping("/journals/{journalId}/comments")
@RequiredArgsConstructor
public class CommentJournalController {

    private final CommentUsecase commentUsecase;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createComment(
        @PathVariable Long journalId,
        @RequestBody CommentCreateReqDto dto,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        commentUsecase.createComment(ContentType.JOURNAL, dto, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.of(null));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<CommentListResDto>> getComments(
        @PathVariable Long journalId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommentListResDto result = commentUsecase.getComments(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.of(result));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<SuccessResponse<Void>> deleteComment(
        @PathVariable Long journalId,
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        commentUsecase.deleteComment(ContentType.JOURNAL, journalId, commentId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.of(null));
    }
}
```

## 5. 체크리스트

- [x] Journal 엔티티에 CommentableContent 구현
- [x] Journal 엔티티에 commentCount 필드 추가
- [x] JournalValidateService 생성 및 검증 메서드 구현
- [x] ContentValidateService에 JOURNAL 케이스 추가
- [x] CommentJournalController 생성
- [x] JourDetailResDto에 commentCount 추가
- [x] API 테스트
- [x] Swagger 문서 확인
