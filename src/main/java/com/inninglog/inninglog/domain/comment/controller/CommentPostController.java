package com.inninglog.inninglog.domain.comment.controller;

import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 소셜", description = "게시글 댓글/좋아요/스크랩 API")
public class CommentPostController {

    private final CommentUsecase commentUsecase;

    @PostMapping("/posts/{postId}/comments")
    @Operation(
            summary = "게시글 댓글 생성",
            description = """
                특정 게시글에 댓글을 생성합니다.
                
                - `postId`는 PathVariable로 전달됩니다.
                - `rootCommentId`가 null이면 일반 댓글입니다.
                - `rootCommentId`에 값이 있으면 해당 댓글의 대댓글(답글)로 생성됩니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 부모 댓글을 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<Void>> createPostComment(
            @Parameter(description = "댓글을 작성할 게시글 ID", example = "35")
            @PathVariable Long postId,

            @RequestBody
            @Parameter(description = "댓글 생성 요청 DTO")
            CommentCreateReqDto dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ){
        commentUsecase.createComment(ContentType.POST, dto, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
