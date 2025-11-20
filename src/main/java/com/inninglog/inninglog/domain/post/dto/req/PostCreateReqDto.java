package com.inninglog.inninglog.domain.post.dto.req;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageCreateReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 생성 요청 DTO")
public record PostCreateReqDto (

        @Schema(description = "게시글 제목", example = "오늘 두산 경기 왜이럼?")
        String title,

        @Schema(description = "게시글 본문", example = "아 진짜 화나네 진짜")
        String content,

        @Schema(description = "첨부 이미지 리스트")
        List<ImageCreateReqDto> imageCreateReqDto
) {
}
