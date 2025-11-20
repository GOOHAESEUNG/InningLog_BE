package com.inninglog.inninglog.domain.scrap.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.contentType.ContentValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import com.inninglog.inninglog.domain.scrap.domain.ScrapableContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapUsecase {

    private final ScrapCreateService scrapCreateService;
    private final ScrapValidateService scrapValidateService;
    private final ContentValidateService contentValidateService;
    private final ScrapDeleteService scrapDeleteService;

    //스크랩 생성
    @Transactional
    public void createScrap(ContentType contentType, Long targetId, Member member){
        ScrapableContent content = contentValidateService.validateContentToScrap(contentType,targetId);
        scrapValidateService.existScrapByMember(contentType,targetId,member);
        scrapCreateService.createScrap(contentType, targetId, member);
        content.increaseScrapCount();
    }

    //스크랩 삭제
    @Transactional
    public void deleteScrap(ContentType contentType, Long targetId, Member member){
        ScrapableContent content = contentValidateService.validateContentToScrap(contentType,targetId);

        Scrap scrap = scrapValidateService.getScrap(contentType, targetId, member);
        scrapDeleteService.deleteScrap(scrap);

        content.decreaseScrapCount();
    }
}
