package com.inninglog.inninglog.domain.contentImage.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "이미지 S3 업로드 프리사인드 url 응답 DTO")
public record ImageListUploadResDto(
        List<ImageUploadResDto> imageUploadResDtos
) {
    public static ImageListUploadResDto of(List<ImageUploadResDto> dtos) {
        return new ImageListUploadResDto(dtos);
    }
}