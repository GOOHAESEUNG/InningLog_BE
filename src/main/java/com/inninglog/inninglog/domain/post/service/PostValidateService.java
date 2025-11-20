package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.repository.PostRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostValidateService {

    private final PostRepository postRepository;

    //게시글 검증
    @Transactional(readOnly = true)
    public void validatePost(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    //게시글 조회
    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
