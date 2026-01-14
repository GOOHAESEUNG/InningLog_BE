package com.inninglog.inninglog.domain.post.dto.req;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageCreateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageRemainUpdateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 수정 요청 DTO")
public record PostUpdateReqDto(

        @Schema(
                description = "게시글 제목",
                example = "오늘 직관 후기 수정합니다"
        )
        String title,

        @Schema(
                description = "게시글 본문 내용",
                example = "경기 내용이 너무 좋아서 내용 수정했어요!"
        )
        String content,

        @Schema(
                description = "기존 이미지 중 유지할 이미지 목록 (삭제되지 않는 이미지)",
                example = """
                [
                  { "imageId": 1 },
                  { "imageId": 3 }
                ]
                """
        )
        List<ImageRemainUpdateReqDto> remainImages,

        @Schema(
                description = "새로 추가된 이미지 정보 목록",
                example = """
                [
                  { "s3Key": "post/2026/01/image1.jpg" },
                  { "s3Key": "post/2026/01/image2.jpg" }
                ]
                """
        )
        List<ImageCreateReqDto> newImages,

        @Schema(
                description = "게시글에 포함된 전체 이미지 개수 (기존 + 신규)",
                example = "4"
        )
        Long imageCount
) {}
