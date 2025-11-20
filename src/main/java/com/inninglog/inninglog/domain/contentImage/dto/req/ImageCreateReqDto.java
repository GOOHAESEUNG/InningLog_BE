package com.inninglog.inninglog.domain.contentImage.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 생성 요청")
public record ImageCreateReqDto(
        @Schema(description = "이미지 업로드 순서", example = "2")
        Integer sequence,

        @Schema(description = "이미지 경로 키", example = "post/1/cat.jpeg")
        String key
) {
}
