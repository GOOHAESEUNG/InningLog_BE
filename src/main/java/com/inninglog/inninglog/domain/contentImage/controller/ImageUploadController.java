package com.inninglog.inninglog.domain.contentImage.controller;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageListUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListUploadResDto;
import com.inninglog.inninglog.domain.contentImage.service.PostImageUploadService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "사진 리스트 업로드", description = "사진 리스트 업로드 관련 API")
public class ImageUploadController {

    private final PostImageUploadService postImageUploadService;

    @PostMapping("/upload/post")
    @Operation(
            summary = "게시글 이미지 Presigned URL 목록 발급",
            description = """
                게시글 작성 시 S3에 이미지를 업로드하기 위한 Presigned PUT URL 목록을 발급합니다.
                
                1) 프론트는 fileName / contentType / sequence 정보를 보내면  
                2) 서버는 각 이미지에 대한 Presigned PUT URL + 저장될 S3 Key 를 생성하여 반환합니다.  
                3) 프론트는 받은 Presigned URL 로 S3에 직접 업로드한 뒤  
                4) 게시글 생성 API 호출 시 해당 key 목록을 포함해 전송해야 합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공"),
            @ApiResponse(responseCode = "400", description = "파일명 검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
    })
    public ResponseEntity<SuccessResponse<ImageListUploadResDto>> uploadPostImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Parameter(description = "업로드할 이미지 목록") ImageListUploadReqDto reqDto
    ) {
        ImageListUploadResDto dto = postImageUploadService.getPostImagePreseignedUrlList(
                reqDto.imageUploadReqDto(),
                user.getMember().getId()
        );
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, dto));
    }
}
