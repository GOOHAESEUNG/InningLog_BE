package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            boolean writedByMe,
            boolean likedByMe,
            boolean scrapedByMe
    ) {
        return PostSingleResDto.of(post, memberShortResDto, imageListResDto, writedByMe, likedByMe, scrapedByMe);
    }

    //게시글 목록 조회 - 팀별 (N+1 최적화: Member Fetch Join 적용)
    @Transactional(readOnly = true)
    public Slice<Post> getPostsByTeam(String teamShortCode, Pageable pageable) {
        return postRepository.findWithMemberByTeamShortCode(teamShortCode, pageable);
    }

    //게시글 작성자 Id 가져오기
    @Transactional(readOnly = true)
    public Member getPostWriterId(Post post){
        return post.getMember();
    }

    //인기 게시글 목록 조회 (좋아요 수 기준)
    @Transactional(readOnly = true)
    public Slice<Post> getPopularPosts(long minLikeCount, Pageable pageable) {
        return postRepository.findPopularPostsWithMember(minLikeCount, pageable);
    }

    //마이페이지: 내가 쓴 글 조회
    @Transactional(readOnly = true)
    public Slice<Post> getMyPosts(Member member, Pageable pageable) {
        return postRepository.findByMemberWithMember(member, pageable);
    }

    //마이페이지: ID 목록으로 게시글 조회 (순서 유지)
    @Transactional(readOnly = true)
    public List<Post> findAllByIds(List<Long> ids) {
        return postRepository.findAllByIdInWithMember(ids);
    }
}

