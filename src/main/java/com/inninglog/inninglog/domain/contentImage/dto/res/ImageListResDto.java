package com.inninglog.inninglog.domain.contentImage.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 이미지 목록 응답 DTO")
public record ImageListResDto(

        @Schema(description = "이미지 응답 리스트")
        List<ImageResDto> imageResDtos
) {
    public static ImageListResDto of(List<ImageResDto> imageResDtos)  {
        return new ImageListResDto(imageResDtos);
    }
}
