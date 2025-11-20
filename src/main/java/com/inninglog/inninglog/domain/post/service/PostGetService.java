package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostGetService {

    private final PostRepository postRepository;

    //게시글 단일 조회
    @Transactional(readOnly = true)
    public PostSingleResDto getSinglePost(
            Post post,
            MemberShortResDto memberShortResDto,
            ImageListResDto imageListResDto,
            boolean likedByMe,
            boolean scrapedByMe
    ) {
        return PostSingleResDto.of(post, memberShortResDto, imageListResDto, likedByMe, scrapedByMe);
    }
}
