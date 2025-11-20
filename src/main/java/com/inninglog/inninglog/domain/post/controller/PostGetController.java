package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.service.PostUsecase;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "게시글", description = "게시글 관련 API")
public class PostGetController {
    private final PostUsecase postUsecase;

    @GetMapping("/posts/{postId}")
    @Operation(
            summary = "게시글 단일 조회",
            description = """
                특정 팀에 속한 게시글을 단건 조회합니다.
                
                - 이미지 목록 포함
                - 작성자 정보 포함
                - 포맷팅된 작성일(postAt) 포함 (yyyy-MM-dd HH:mm)
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
    })
    public ResponseEntity<SuccessResponse<PostSingleResDto>> getSinglePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable("postId") Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        PostSingleResDto resdto = postUsecase.getSinglePost(ContentType.POST, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }
}
