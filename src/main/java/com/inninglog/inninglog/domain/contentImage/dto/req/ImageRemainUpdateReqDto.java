package com.inninglog.inninglog.domain.contentImage.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "기존 이미지 수정 요청")
public record ImageRemainUpdateReqDto(
        @Schema(description = "기존 이미지 Id", example = "1")
        Long remainImageId,

        @Schema(description = "수정 후 이미지 업로드 순서", example = "2")
        Integer sequence
) {
}
