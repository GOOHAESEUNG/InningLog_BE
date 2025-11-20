package com.inninglog.inninglog.domain.like.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessApiResponses;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "좋아요", description = "좋아요 관련 API")
public class LikeDeleteController {

    private final LikeUsecase likeUsecase;

    @Operation(
            summary = "게시글 좋아요 취소",
            description = """
                특정 게시글에 현재 로그인한 사용자가 누른 '좋아요'를 취소합니다.

                - 로그인한 사용자가 해당 게시글에 좋아요를 누른 상태일 때만 삭제됩니다.
                - 좋아요가 존재하지 않는 상태에서 요청 시 예외 발생
                - 성공 시 좋아요 수 감소
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 취소 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "좋아요 기록 또는 게시글을 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<SuccessResponse<Void>> deleteLikeAtPost(
            @Parameter(description = "좋아요를 취소할 게시글 ID", example = "12")
            @PathVariable Long postId,

            @AuthenticationPrincipal CustomUserDetails user
    ){
        likeUsecase.deleteLike(ContentType.POST, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }

    @Operation(
            summary = "댓글 좋아요 취소",
            description = "특정 댓글에 등록된 좋아요를 취소합니다. 아직 좋아요를 누르지 않은 상태에서 취소 요청 시 에러가 발생합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 좋아요 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (좋아요를 누르지 않은 상태에서 취소 요청 등)"),
            @ApiResponse(responseCode = "404", description = "댓글 또는 좋아요 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/comments/{commentId}/likes")
    public ResponseEntity<SuccessResponse<Void>> deleteLikeAtComment(
            @Parameter(description = "좋아요를 취소할 댓글 ID", example = "3")
            @PathVariable Long commentId,

            @AuthenticationPrincipal CustomUserDetails user
    ){
        likeUsecase.deleteLike(ContentType.COMMENT, commentId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}