package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCreateService {

    private final PostRepository postRepository;

    //게시글 생성
    public Long createPost(PostCreateReqDto dto,String team_shortcode, Member member){
        Post post = Post.of(dto, team_shortcode, member);
       return postRepository.save(post).getId();
    }
}
