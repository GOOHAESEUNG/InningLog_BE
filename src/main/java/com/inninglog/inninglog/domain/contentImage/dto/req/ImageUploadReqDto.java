package com.inninglog.inninglog.domain.contentImage.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 S3 업로드 요청 DTO")
public record ImageUploadReqDto(
        @Schema(description = "이미지 업로드 순서", example = "2")
        Integer sequence,

        @Schema(description = "이미지 파일명(확장자 포함)", example = "cat.png")
        String fileName,

        @Schema(description = "이미지 파일 MIME 타입", example = "image/png")
        String contentType
) {
}
