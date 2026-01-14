package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSummaryResDto;
import com.inninglog.inninglog.domain.post.service.PostUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.dto.SliceResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    @Operation(
            summary = "팀별 게시글 목록 조회",
            description = """
                특정 팀의 게시글 목록을 Slice 기반으로 조회합니다.

                ✔ 최신순(postAt DESC)으로 정렬되어 반환됩니다.  
                ✔ page는 0부터 시작합니다. (0=첫 페이지)  
                ✔ size는 한 페이지에서 가져올 게시글 수를 의미합니다.  
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.

                ※ 목록에서는 본문(content)은 전체 원문이 내려가며,  
                   실제 UI에서 최대 24글자로 잘라 사용하는 것을 권장합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팀별 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class)
                    )
            )
    })
    @GetMapping("/posts/team/{teamShortCode}")
    public ResponseEntity<SuccessResponse<SliceResponse<PostSummaryResDto>>> getPostsByTeam(
            @Parameter(description = "팀 숏 코드 (예: LG, OB, LT)", example = "LG")
            @PathVariable String teamShortCode,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam int page,

            @Parameter(description = "한 페이지당 게시글 개수", example = "10")
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<PostSummaryResDto> resdto = postUsecase.getPostList(teamShortCode, pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resdto));
    }
}
