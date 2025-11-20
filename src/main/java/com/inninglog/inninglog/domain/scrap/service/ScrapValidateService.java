package com.inninglog.inninglog.domain.scrap.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import com.inninglog.inninglog.domain.scrap.repository.ScrapRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapValidateService {
    private final ScrapRepository scrapRepository;

    @Transactional(readOnly = true)
    public void existScrapByMember(ContentType contentType, Long targetId, Member member){
        if(scrapRepository.existsByContentTypeAndTargetIdAndMember(contentType, targetId, member)){
            throw new CustomException(ErrorCode.SCRAP_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public boolean scrapedByMe(ContentType contentType, Long targetId, Member member){
        return scrapRepository.existsByContentTypeAndTargetIdAndMember(contentType, targetId, member);
    }

    @Transactional(readOnly = true)
    public Scrap getScrap(ContentType contentType, Long targetId, Member member){
        return scrapRepository.findByContentTypeAndTargetIdAndMember(contentType, targetId, member)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
    }
}
