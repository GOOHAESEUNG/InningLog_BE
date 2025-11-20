package com.inninglog.inninglog.domain.contentImage.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드 url DTO")
public record ImageUploadResDto(

        @Schema(description = "이미지 순서", example = "1")
        Integer sequence,

        @Schema(description = "이미지 프리사인드 url", example = "https://inninglog.s3.ap-northeast-2.amazonaws.com/seatView/1/seatview001.png?X-Amz-Algorithm=...")
        String presignedUrl,

        @Schema(description = "이미지 경로 키", example = "post/1/cat.jpeg")
        String key
) {
}
