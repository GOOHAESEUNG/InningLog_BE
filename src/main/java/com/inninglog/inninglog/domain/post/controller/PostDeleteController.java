package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.post.service.PostUpdateService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 게시글", description = "게시글 관련 API")
public class PostDeleteController {
    private final PostUsecase postUsecase;

    @Operation(
            summary = "게시글 삭제",
            description = """
                특정 게시글을 영구 삭제합니다.
                
                게시글 삭제 시 아래와 같은 모든 연관된 데이터가 함께 **하드 딜리트(Hard Delete)** 됩니다:
                - 게시글에 달린 모든 댓글
                - 게시글의 좋아요(Like)
                - 게시글의 스크랩(Scrap)
                - 게시글과 연결된 이미지 파일(S3 등 저장된 사진)
                
                이미 삭제된 게시글이거나 존재하지 않는 게시글 ID가 전달될 경우 에러가 발생합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (또는 이미 삭제된 게시글)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<SuccessResponse<Void>> deletePost(
            @Parameter(description = "삭제할 게시글의 ID", example = "42")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ){
        postUsecase.deletePost(user.getMember().getId(), postId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
