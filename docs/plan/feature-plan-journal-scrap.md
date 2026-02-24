# 기능 기획서: Journal 스크랩 API

> 작성일: 2025-01-21
> 관련 이슈: #88
> 상태: 구현 완료

## 1. 개요

직관 일지(Journal)에 스크랩(북마크) 기능을 추가하여 사용자가 관심 있는 직관일지를 저장할 수 있게 한다.

## 2. 현재 구조 분석

### 스크랩 시스템 핵심 설계
```
Scrap 엔티티:
- contentType: POST | JOURNAL | MARKET (다형성 지원)
- targetId: 대상 콘텐츠 ID
- member: 스크랩한 사용자
```

### 현재 기능 지원 현황

| 기능 | Post | Journal |
|------|------|---------|
| 스크랩 | O | X |
| ScrapableContent 인터페이스 | 구현됨 | 미구현 |
| scrapCount 필드 | O | X |

### 기존 스크랩 API (Post)

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/community/posts/{postId}/scraps` | 게시글 스크랩 |
| `DELETE` | `/community/posts/{postId}/scraps` | 게시글 스크랩 취소 |

### 재사용 가능 요소
- `ScrapUsecase`, `ScrapCreateService`, `ScrapDeleteService` - 그대로 사용
- `ScrapRepository` - 그대로 사용
- `ScrapValidateService.scrapedByMe()` - 그대로 사용

## 3. API 설계

### 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/journals/{journalId}/scraps` | 직관일지 스크랩 |
| `DELETE` | `/journals/{journalId}/scraps` | 직관일지 스크랩 취소 |

### Response

**스크랩 성공 Response:**
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
| `Journal.java` | ScrapableContent 인터페이스 구현, scrapCount 필드 추가 |
| `ContentValidateService.java` | validateContentToScrap()에 JOURNAL 케이스 추가 |
| `ScrapJournalController.java` | 신규 생성 (POST, DELETE 엔드포인트) |
| `JourDetailResDto.java` | scrapCount, scrapedByMe 필드 추가 |
| `JournalUsecase.java` | scrapedByMe 조회 로직 추가 |

### Journal 엔티티 변경사항 (71-100줄 부근)

```java
@Entity
public class Journal extends BaseTimeEntity
    implements CommentableContent, LikeableContent, ScrapableContent {

    // 기존 필드들...

    @Builder.Default
    private long scrapCount = 0;

    @Override
    public void increaseScrapCount() {
        this.scrapCount++;
    }

    @Override
    public void decreaseScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }
}
```

### ContentValidateService 변경사항 (36-45줄)

```java
public ScrapableContent validateContentToScrap(ContentType contentType, Long targetId){
    if(contentType==ContentType.POST){
        return postValidateService.getPostById(targetId);
    } else if (contentType==ContentType.JOURNAL) {
        return journalValidateService.getJournalById(targetId);  // 추가
    } else if (contentType==ContentType.MARKET) {
        //이닝장터 반환
    }
    throw new IllegalArgumentException("지원 안 함");
}
```

### 새 Controller 구조

```java
@RestController
@RequestMapping("/journals/{journalId}/scraps")
@RequiredArgsConstructor
@Tag(name = "직관일지 스크랩", description = "직관일지 스크랩 관련 API")
public class ScrapJournalController {

    private final ScrapUsecase scrapUsecase;

    @PostMapping
    @Operation(summary = "직관일지 스크랩", description = "직관일지를 스크랩합니다.")
    public ResponseEntity<SuccessResponse<Void>> scrapJournal(
        @PathVariable Long journalId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        scrapUsecase.createScrap(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }

    @DeleteMapping
    @Operation(summary = "직관일지 스크랩 취소", description = "직관일지 스크랩을 취소합니다.")
    public ResponseEntity<SuccessResponse<Void>> unscrapJournal(
        @PathVariable Long journalId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        scrapUsecase.deleteScrap(ContentType.JOURNAL, journalId, user.getMember());
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
    long likeCount,
    boolean likedByMe,
    long scrapCount,      // 추가
    boolean scrapedByMe   // 추가
) {}
```

## 6. 체크리스트

- [x] Journal 엔티티에 ScrapableContent 구현
- [x] Journal 엔티티에 scrapCount 필드 추가
- [x] ContentValidateService에 JOURNAL 케이스 추가
- [x] ScrapJournalController 생성
- [x] JourDetailResDto에 scrapCount, scrapedByMe 추가
- [x] JournalUsecase에서 scrapedByMe 조회 로직 추가
- [x] API 테스트
- [x] Swagger 문서 확인
