package com.inninglog.inninglog.domain.like.service;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.domain.Like;
import com.inninglog.inninglog.domain.like.repository.LikeRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeCreateService {

    private final LikeRepository likeRepository;

    @Transactional
    public void createLike(ContentType contentType, Long target, Member member) {
        Like like = Like.of(contentType, target, member);
        likeRepository.save(like);
    }
}
