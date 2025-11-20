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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "스크랩", description = "스크랩 관련 API")
public class ScrapCreateController {

    private final ScrapUsecase scrapUsecase;

    @PostMapping("/posts/{postsId}/scraps")
    @Operation(
            summary = "게시글 스크랩 생성",
            description = """
            특정 게시글을 현재 로그인한 사용자가 스크랩합니다.

            - 이미 스크랩한 상태에서 다시 요청하면 예외가 발생합니다.
            - 성공 시 스크랩 수 증가.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스크랩 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 스크랩한 상태에서 재요청한 경우"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    public ResponseEntity<SuccessResponse<Void>> createScrapAtPost(
            @Parameter(description = "스크랩을 생성할 게시글 ID", example = "12")
            @PathVariable Long postsId,

            @AuthenticationPrincipal CustomUserDetails user
    ){
        scrapUsecase.createScrap(ContentType.POST, postsId, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
