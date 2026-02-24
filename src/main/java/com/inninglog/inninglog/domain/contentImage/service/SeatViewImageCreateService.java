package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.s3.S3UrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatViewImageCreateService {

    private final ContentImageRepository contentImageRepository;
    private final S3UrlProperties s3UrlProperties;

    @Transactional
    public void createSeatViewImage(Long seatViewId, String fileName, Long memberId) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }

        String key = "seatView/" + memberId + "/" + fileName;
        String originalUrl = getOriginalUrl(key);

        ContentImage contentImage = ContentImage.builder()
                .contentType(ContentType.SEATVIEW)
                .targetId(seatViewId)
                .originalUrl(originalUrl)
                .sequence(1)
                .build();

        contentImageRepository.save(contentImage);
    }

    private String getOriginalUrl(String key) {
        return s3UrlProperties.getBaseUrl() + "/" + key;
    }
}
