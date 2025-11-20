package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageCreateReqDto;
import com.inninglog.inninglog.domain.contentImage.dto.req.ImageUploadReqDto;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.s3.S3UrlProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageCreateService {

    private final ContentImageRepository contentImageRepository;
    private final S3UrlProperties s3UrlProperties;

    @Transactional
    public void createPostImageList(Long postId, List<ImageCreateReqDto> dtos){
        for (ImageCreateReqDto dto : dtos) {
            createPostImage(postId, dto);
        }
    }

    @Transactional
    public void createPostImage(Long postId, ImageCreateReqDto dto) {
        String originalUrl = getOriginalUrl(dto.key());
        ContentImage contentImage = ContentImage.of(ContentType.POST, postId, originalUrl, dto);

        contentImageRepository.save(contentImage);
    }

    private String getOriginalUrl(String Key){
        return s3UrlProperties.getBaseUrl() + "/" + Key;
    }
}
