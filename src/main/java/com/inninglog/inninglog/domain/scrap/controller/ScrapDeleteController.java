package com.inninglog.inninglog.domain.scrap.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.scrap.service.ScrapUsecase;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 소셜", description = "게시글 댓글/좋아요/스크랩 API")
public class ScrapDeleteController {

    private final ScrapUsecase scrapUsecase;

    @DeleteMapping("/posts/{postId}/scraps")
    @Operation(
            summary = "게시글 스크랩 취소",
            description = """
            특정 게시글에 대해 로그인한 사용자가 해둔 스크랩을 취소합니다.

            - 사용자가 해당 게시글을 스크랩한 상태일 때만 취소가 가능합니다.
            - 스크랩이 존재하지 않는 상태에서 요청하면 예외가 발생합니다.
            - 성공 시 스크랩 수가 감소합니다.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스크랩 취소 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "스크랩 정보 또는 게시글을 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    public ResponseEntity<SuccessResponse<Void>> deleteScrapAtPost(
            @Parameter(description = "스크랩을 취소할 게시글 ID", example = "12")
            @PathVariable Long postId,

            @AuthenticationPrincipal CustomUserDetails user
    ){
        scrapUsecase.deleteScrap(ContentType.POST, postId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
