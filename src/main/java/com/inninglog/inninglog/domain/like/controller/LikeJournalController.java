package com.inninglog.inninglog.domain.like.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/journals/{journalId}/likes")
@Tag(name = "직관일지 - 소셜", description = "직관일지 댓글/좋아요/스크랩 API")
public class LikeJournalController {

    private final LikeUsecase likeUsecase;

    @Operation(
            summary = "직관일지 좋아요",
            description = "직관일지에 좋아요를 추가합니다. 이미 좋아요를 누른 상태에서 다시 요청 시 예외 발생"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 성공"),
            @ApiResponse(responseCode = "400", description = "이미 좋아요를 누른 경우"),
            @ApiResponse(responseCode = "404", description = "직관일지를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> likeJournal(
            @Parameter(description = "좋아요를 누를 직관일지 ID", example = "1")
            @PathVariable Long journalId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        likeUsecase.createLike(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }

    @Operation(
            summary = "직관일지 좋아요 취소",
            description = "직관일지의 좋아요를 취소합니다. 좋아요가 존재하지 않는 상태에서 요청 시 예외 발생"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "404", description = "좋아요 기록 또는 직관일지를 찾을 수 없음")
    })
    @DeleteMapping
    public ResponseEntity<SuccessResponse<Void>> unlikeJournal(
            @Parameter(description = "좋아요를 취소할 직관일지 ID", example = "1")
            @PathVariable Long journalId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        likeUsecase.deleteLike(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
