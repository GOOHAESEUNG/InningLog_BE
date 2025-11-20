package com.inninglog.inninglog.domain.comment.controller;

import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.service.CommentUsecase;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<SuccessResponse<CommentListResDto>> getPostComments(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable("postId") Long postId
    ){
        CommentListResDto resdto = commentUsecase.getComments(ContentType.POST, postId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }
}
