package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDeleteService {

    private final PostRepository postRepository;

    @Transactional
    public void postDelete(Post post) {
        postRepository.delete(post);
    }
}
