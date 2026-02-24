package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 게시글", description = "게시글 관련 API")
public class PostCreateController {

    private final PostUsecase postUsecase;

    @PostMapping("/{teamSC}/posts/create")
    @Operation(
            summary = "게시글 생성",
            description = """
                게시글을 생성합니다.
                
                ⚾ 이미지 업로드 흐름:
                1) 먼저 Presigned URL 발급 API로 S3 업로드용 URL을 발급받고
                2) 프론트는 해당 Presigned URL로 S3에 직접 업로드한 뒤
                3) 아래 createPost API 호출 시 업로드된 이미지들의 s3Key(sequence 포함)를 함께 전달해야 합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 오류"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<SuccessResponse<Void>> createPost(
            @Parameter(description = "팀 숏 코드 (ex: LG)", example = "LG")
            @PathVariable("teamSC") String teamSC,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @RequestBody @Parameter(description = "게시글 생성 요청 DTO") PostCreateReqDto dto
    ) {
        postUsecase.uploadPost(dto, teamSC, user.getMember());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
