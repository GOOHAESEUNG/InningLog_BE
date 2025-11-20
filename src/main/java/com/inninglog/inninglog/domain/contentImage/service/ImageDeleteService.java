package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageDeleteService {

    private final ContentImageRepository contentImageRepository;

    @Transactional
    public void deleteByTargetId(ContentType contentType, Long targetId) {
        contentImageRepository.deleteAllByContent(contentType, targetId);
    }
}
