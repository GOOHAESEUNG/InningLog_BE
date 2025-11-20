package com.inninglog.inninglog.domain.contentImage.dto.res;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 이미지 단일 응답 DTO")
public record ImageResDto(

        @Schema(description = "이미지 PK", example = "21")
        Long imageId,

        @Schema(description = "정렬 순서", example = "1")
        Integer sequence,

        @Schema(description = "이미지 실제 URL", example = "https://inninglog.s3.ap-northeast-3.amazonaws.com/post/1/uuid.jpeg")
        String url
) {
    public static ImageResDto from(ContentImage contentImage) {
        return new ImageResDto(
                contentImage.getId(),
                contentImage.getSequence(),
                contentImage.getOriginalUrl()
        );
    }
}
