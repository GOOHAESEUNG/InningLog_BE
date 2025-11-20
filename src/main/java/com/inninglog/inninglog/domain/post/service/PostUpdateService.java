package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUpdateService {
    //댓글 추가시 댓글 필드 수 증가 로직
    @Transactional
    public void increaseCommentCount(Post post) {
        post.increaseCommentCount();
    }

    //좋아요 수 증가
    @Transactional
    public void increaseLikeCount(Post post) {
        post.increaseLikeCount();
    }
}
