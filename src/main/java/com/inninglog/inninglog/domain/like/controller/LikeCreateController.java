package com.inninglog.inninglog.domain.like.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 소셜", description = "게시글 댓글/좋아요/스크랩 API")
public class LikeCreateController {

    private final LikeUsecase likeUsecase;

    @Operation(
            summary = "게시글 좋아요 생성",
            description = """
            특정 게시글에 현재 로그인한 사용자가 좋아요를 누릅니다.

            - 이미 좋아요를 누른 상태에서 다시 요청 시 예외 발생
            - 성공 시 좋아요 수 증가
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글 없음 / 사용자 없음 등의 예외"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 좋아요를 눌렀을 경우"
            )
    })
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<SuccessResponse<Void>> createLikeAtPost(
            @Parameter(description = "좋아요를 누를 게시글 ID", example = "12")
            @PathVariable Long postId,

            @AuthenticationPrincipal CustomUserDetails user
    ){
        likeUsecase.createLike(ContentType.POST, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }

    @Operation(
            summary = "댓글 좋아요 생성",
            description = "특정 댓글에 좋아요를 등록합니다. 이미 좋아요를 누른 상태에서 다시 요청할 경우 에러가 발생합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 좋아요 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 좋아요를 누른 경우 등)"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<SuccessResponse<Void>> createLikeAtComment(
            @Parameter(description = "좋아요를 등록할 댓글 ID", example = "15")
            @PathVariable Long commentId,

            @AuthenticationPrincipal CustomUserDetails user
    ){
        likeUsecase.createLike(ContentType.COMMENT, commentId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
