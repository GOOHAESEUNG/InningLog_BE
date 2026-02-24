package com.inninglog.inninglog.domain.scrap.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import com.inninglog.inninglog.domain.scrap.repository.ScrapRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    // N+1 최적화: 여러 targetId에 대해 스크랩 여부를 한 번에 조회
    @Transactional(readOnly = true)
    public Set<Long> findScrapedTargetIds(ContentType contentType, List<Long> targetIds, Member member){
        return scrapRepository.findScrapedTargetIds(contentType, targetIds, member);
    }

    //마이페이지: 내가 스크랩한 게시글 ID 조회
    @Transactional(readOnly = true)
    public Slice<Long> getScrappedPostIds(Member member, Pageable pageable) {
        return scrapRepository.findTargetIdsByMemberAndContentType(member, ContentType.POST, pageable);
    }
}
