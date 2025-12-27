package com.inninglog.inninglog.domain.contentImage.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostImageDeleteService {
    private final ContentImageRepository contentImageRepository;

    @Transactional
    public void delete(ContentImage contentImage) {
        contentImageRepository.delete(contentImage);
    }
}
