package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageResDto;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageGetService {

    private final ContentImageRepository contentImageRepository;

    @Transactional(readOnly = true)
    public ImageListResDto getImageList(ContentType contentType, Long targetId) {
        List<ContentImage> contentImages = contentImageRepository.findAllByContentTypeAndTargetIdOrderBySequenceAsc(contentType, targetId);

        List<ImageResDto> imageResDtos = new ArrayList<>();

        for(ContentImage contentImage : contentImages) {
           ImageResDto result =  getImage(contentImage);

            imageResDtos.add(result);
        }

        return ImageListResDto.of(imageResDtos);
    }

    @Transactional(readOnly = true)
    protected ImageResDto getImage(ContentImage contentImage) {
        return ImageResDto.from(contentImage);
    }
}
