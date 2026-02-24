package com.inninglog.inninglog.domain.like.service;

import static com.inninglog.inninglog.global.exception.ErrorCode.LIKE_ALREADY_EXISTS;
import static com.inninglog.inninglog.global.exception.ErrorCode.LIKE_NOT_FOUND;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.domain.Like;
import com.inninglog.inninglog.domain.like.repository.LikeRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.global.exception.CustomException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeValidateService {
    private final LikeRepository likeRepository;

    //이미 좋아요 누른건지 확인
    @Transactional
    public void existLikeByMember(ContentType contentType, Long targetId, Member member){
        if(likeRepository.existsByContentTypeAndTargetIdAndMember(contentType, targetId, member)){
            throw new CustomException(LIKE_ALREADY_EXISTS);
        }
    }

    //내가 좋아요 누른건지에 대한 볼린 형태 반환
    @Transactional(readOnly = true)
    public boolean likedByMe(ContentType contentType, Long targetId, Member member){
        return likeRepository.existsByContentTypeAndTargetIdAndMember(contentType, targetId, member);
    }

    // N+1 최적화: 여러 targetId에 대해 좋아요 여부를 한 번에 조회
    @Transactional(readOnly = true)
    public Set<Long> findLikedTargetIds(ContentType contentType, List<Long> targetIds, Member member){
        return likeRepository.findLikedTargetIds(contentType, targetIds, member);
    }

    //이미 좋아요 누른건지 확인
    @Transactional
    public Like getLike(ContentType contentType, Long targetId, Member member){
        return likeRepository
                .findByContentTypeAndTargetIdAndMember(contentType, targetId, member)
                .orElseThrow(() -> new CustomException(LIKE_NOT_FOUND));
    }
}
