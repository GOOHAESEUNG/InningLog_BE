package com.inninglog.inninglog.domain.scrap.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import com.inninglog.inninglog.domain.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapDeleteService {
    private final ScrapRepository scrapRepository;

    //스크랩 삭제
    @Transactional
    public void deleteScrap(Scrap scrap) {
        scrapRepository.delete(scrap);
    }

    @Transactional
    public void deleteByTargetId(ContentType contentType, Long targetId) {
        scrapRepository.deleteAllByContent(contentType, targetId);
    }
}
