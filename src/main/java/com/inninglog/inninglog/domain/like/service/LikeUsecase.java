package com.inninglog.inninglog.domain.like.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.contentType.ContentValidateService;
import com.inninglog.inninglog.domain.like.domain.Like;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.service.PostUpdateService;
import com.inninglog.inninglog.domain.post.service.PostValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeUsecase {

    private final LikeCreateService likeCreateService;
    private final LikeDeleteService likeDeleteService;
    private final ContentValidateService contentValidateService;
    private final LikeValidateService likeValidateService;

    //좋아요 생성
    @Transactional
    public void createLike(ContentType contentType, Long targetId, Member member) {
        LikeableContent content = contentValidateService.validateContentToLike(contentType, targetId);
        likeValidateService.existLikeByMember(contentType, targetId, member);
        likeCreateService.createLike(contentType, targetId, member);
        content.increaseLikeCount();
    }

    //좋아요 삭제
    @Transactional
    public void deleteLike(ContentType contentType, Long targetId, Member member) {
        LikeableContent content = contentValidateService.validateContentToLike(contentType, targetId);

        Like like = likeValidateService.getLike(contentType, targetId, member);
        likeDeleteService.deleteLike(like);

        content.decreaseLikeCount();
    }
}
