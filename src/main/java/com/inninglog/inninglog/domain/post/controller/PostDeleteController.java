package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.post.service.PostUpdateService;
import com.inninglog.inninglog.domain.post.service.PostUsecase;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostDeleteController {
    private final PostUsecase postUsecase;

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<SuccessResponse<Void>> deletePost(
            @PathVariable Long postId
    ){
        postUsecase.deletePost(postId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
