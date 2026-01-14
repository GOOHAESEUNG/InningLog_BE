package com.inninglog.inninglog.domain.comment.controller;

import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.service.CommentUsecase;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentGetController {
    private final CommentUsecase commentUsecase;

    @Operation(
            summary = "게시글 댓글 목록 조회",
            description = "특정 게시글에 작성된 댓글 목록을 조회합니다. 로그인한 사용자의 정보에 따라 댓글별 추가 정보가 함께 반환될 수 있습니다."
    )
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<SuccessResponse<CommentListResDto>> getPostComments(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        CommentListResDto resdto = commentUsecase.getComments(ContentType.POST, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }
}
