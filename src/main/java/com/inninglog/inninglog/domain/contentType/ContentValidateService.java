package com.inninglog.inninglog.domain.contentType;

import com.inninglog.inninglog.domain.comment.domain.CommentableContent;
import com.inninglog.inninglog.domain.comment.service.CommentValidateServcie;
import com.inninglog.inninglog.domain.journal.service.JournalValidateService;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.post.service.PostValidateService;
import com.inninglog.inninglog.domain.scrap.domain.ScrapableContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentValidateService {

    private final PostValidateService postValidateService;
    private final CommentValidateServcie commentValidateServcie;
    private final JournalValidateService journalValidateService;

    @Transactional(readOnly = true)
    public LikeableContent validateContentToLike(ContentType contentType, Long targetId){
        if(contentType==ContentType.POST){
            return postValidateService.getPostById(targetId);
        } else if (contentType==ContentType.JOURNAL) {
            return journalValidateService.getJournalById(targetId);
        } else if (contentType==ContentType.MARKET) {
            //이닝장터 반환
        }else if(contentType==ContentType.COMMENT){
            return commentValidateServcie.getCommentId(targetId);
        }
        throw new IllegalArgumentException("지원 안 함");
    }

    @Transactional(readOnly = true)
    public ScrapableContent validateContentToScrap(ContentType contentType, Long targetId){
        if(contentType==ContentType.POST){
            return postValidateService.getPostById(targetId);
        } else if (contentType==ContentType.JOURNAL) {
            return journalValidateService.getJournalById(targetId);
        } else if (contentType==ContentType.MARKET) {
            //이닝장터 반환
        }
        throw new IllegalArgumentException("지원 안 함");
    }

    @Transactional(readOnly = true)
    public CommentableContent validateContentToComment(ContentType contentType, Long targetId){
        if(contentType==ContentType.POST){
            return postValidateService.getPostById(targetId);
        } else if (contentType==ContentType.JOURNAL) {
            return journalValidateService.getJournalById(targetId);
        } else if (contentType==ContentType.MARKET) {
            //이닝장터 반환
        }
        throw new IllegalArgumentException("지원 안 함");
    }


}
