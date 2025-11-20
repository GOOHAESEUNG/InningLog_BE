package com.inninglog.inninglog.domain.contentImage.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "이미지 리스트 S3 업로드 요청")
public record ImageListUploadReqDto(
        @Schema(description = "이미지 리스트 S3 업로드 요청")
        List<ImageUploadReqDto> imageUploadReqDto
) {
}
