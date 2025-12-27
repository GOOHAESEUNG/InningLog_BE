package com.inninglog.inninglog.domain.post.dto.req;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageCreateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageRemainUpdateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import java.util.List;

public record PostUpdateReqDto(
        String title,
        String content,

        // 클라이언트에서 유지할 기존 이미지들의 id 리스트
        List<ImageRemainUpdateReqDto> remainImages,

        // 새로 추가된 이미지 S3 key 리스트
        List<ImageCreateReqDto> newImages
) {}
