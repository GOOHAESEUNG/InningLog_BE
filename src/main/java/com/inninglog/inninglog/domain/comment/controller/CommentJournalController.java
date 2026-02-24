package com.inninglog.inninglog.domain.comment.controller;

import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.service.CommentUsecase;
import com.inninglog.inninglog.domain.contentType.ContentType;
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
@RequestMapping("/journals/{journalId}/comments")
@Tag(name = "직관일지 - 소셜", description = "직관일지 댓글/좋아요/스크랩 API")
public class CommentJournalController {

    private final CommentUsecase commentUsecase;

    @PostMapping
    @Operation(
            summary = "직관일지 댓글 생성",
            description = """
                특정 직관일지에 댓글을 생성합니다.

                - `journalId`는 PathVariable로 전달됩니다.
                - `rootCommentId`가 null이면 일반 댓글입니다.
                - `rootCommentId`에 값이 있으면 해당 댓글의 대댓글(답글)로 생성됩니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "직관일지 또는 부모 댓글을 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<Void>> createJournalComment(
            @Parameter(description = "댓글을 작성할 직관일지 ID", example = "1")
            @PathVariable Long journalId,

            @RequestBody
            @Parameter(description = "댓글 생성 요청 DTO")
            CommentCreateReqDto dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        commentUsecase.createComment(ContentType.JOURNAL, dto, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }

    @GetMapping
    @Operation(
            summary = "직관일지 댓글 목록 조회",
            description = "특정 직관일지에 작성된 댓글 목록을 조회합니다. 로그인한 사용자의 정보에 따라 댓글별 추가 정보가 함께 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "직관일지를 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<CommentListResDto>> getJournalComments(
            @Parameter(description = "직관일지 ID", example = "1")
            @PathVariable Long journalId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommentListResDto result = commentUsecase.getComments(ContentType.JOURNAL, journalId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }
}
