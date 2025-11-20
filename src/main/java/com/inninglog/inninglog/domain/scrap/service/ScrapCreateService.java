package com.inninglog.inninglog.domain.scrap.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import com.inninglog.inninglog.domain.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapCreateService {

    private final ScrapRepository scrapRepository;

    //스크랩 생성
    @Transactional
    public void createScrap(ContentType contentType, Long targetId, Member member){
        Scrap scrap = Scrap.of(contentType, targetId, member);
        scrapRepository.save(scrap);
    }
}
