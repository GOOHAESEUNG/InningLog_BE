package com.inninglog.inninglog.domain.post.controller;

import com.inninglog.inninglog.domain.post.dto.req.PostUpdateReqDto;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "커뮤니티 - 게시글", description = "게시글 관련 API")
public class PostUpdateController {

    private final PostUsecase postUsecase;

    @Operation(
            summary = "게시글 수정",
            description = """
                게시글의 제목·내용 수정뿐 아니라 **기존 이미지 유지/삭제/순서 변경**,  
                **새로운 이미지 추가**까지 모두 처리하는 API입니다.

                ### 🔧 요청 규칙

                #### 1) 기존 이미지 수정 (`remainImages`)
                - 기존 ContentImage의 `id` 와 수정된 `sequence`즉 순서를 전달합니다.
                - 전달된 이미지들만 유지되며, 전달되지 않은 기존 이미지는 삭제됩니다.

                #### 2) 신규 이미지 추가 (`newImages`)
                - S3 Presigned URL을 통해 업로드된 후의 이미지 key와,
                  최종 반영할 `sequence` 를 전달합니다.

                #### 3) sequence 정책
                - **프론트에서 보내준 sequence 값이 최종 순서입니다.**
                - 백엔드는 별도의 재정렬을 하지 않고, 전달받은 순서 그대로 저장합니다.

                ---
                ### 📌 예시 요청
                ```json
                {
                  "title": "수정된 제목",
                  "content": "변경된 내용입니다.",
                  "remainImages": [
                    { "remainImageId": 12, "sequence": 1 },
                    { "remainImageId": 14, "sequence": 3 }
                  ],
                  "newImages": [
                    { "sequence": 2, "key": "post/1/new-image.jpeg" }
                  ]
                }
                ```
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "작성자만 수정 가능"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
    })
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<SuccessResponse<Void>> updatePost(
            @Parameter(description = "수정할 게시글 ID", example = "123")
            @PathVariable Long postId,

            @Parameter(description = "게시글 수정 요청 DTO")
            @RequestBody PostUpdateReqDto dto,

            @AuthenticationPrincipal CustomUserDetails user
    ) {
        postUsecase.updatePost(user.getMember().getId(), postId, dto);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
