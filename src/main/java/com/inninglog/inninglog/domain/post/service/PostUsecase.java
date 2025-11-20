package com.inninglog.inninglog.domain.post.service;

import com.inninglog.inninglog.domain.comment.service.CommentDeleteService;
import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentImage.service.ImageGetService;
import com.inninglog.inninglog.domain.contentImage.service.PostImageCreateService;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeDeleteService;
import com.inninglog.inninglog.domain.like.service.LikeValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.post.dto.res.PostSingleResDto;
import com.inninglog.inninglog.domain.scrap.service.ScrapDeleteService;
import com.inninglog.inninglog.domain.scrap.service.ScrapValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostUsecase {

    private final PostCreateService postCreateService;
    private final PostImageCreateService postImageCreateService;
    private final PostGetService postGetService;
    private final PostValidateService postValidateService;
    private final PostDeleteService postDeleteService;

    private final MemberGetService memberGetService;
    private final ImageGetService imageGetService;

    private final LikeValidateService likeValidateService;
    private final LikeDeleteService likeDeleteService;

    private final ScrapValidateService scrapValidateService;
    private final ScrapDeleteService scrapDeleteService;

    private final CommentDeleteService commentDeleteService;

    private final ContentImageRepository contentImageRepository;

    //게시글 업로드 + 이미지 저장
    @Transactional
    public void uploadPost (PostCreateReqDto dto,String team_Shortcode, Member member){
        Long postId = postCreateService.createPost(dto,team_Shortcode,member);
        postImageCreateService.createPostImageList(postId, dto.imageCreateReqDto());
    }

    //게시글 단일 조회
    @Transactional(readOnly = true)
    public PostSingleResDto getSinglePost(ContentType contentType, Long postId, Member me){
        Post post = postValidateService.getPostById(postId);
        MemberShortResDto memberShortResDto = memberGetService.toMemberShortResDto(post.getMember());
        ImageListResDto imageListResDto = imageGetService.getImageList(contentType, postId);

        boolean likedByMe = likeValidateService.likedByMe(contentType, postId, me);
        boolean scrapedByMe = scrapValidateService.scrapedByMe(contentType, postId, me);

        return postGetService.getSinglePost(post, memberShortResDto, imageListResDto, likedByMe, scrapedByMe);
    }

    //게시글 삭제
    @Transactional
    public void deletePost(Long postId){
        likeDeleteService.deleteByTargetId(ContentType.POST, postId);
        commentDeleteService.deleteByTargetId(ContentType.POST, postId);
        scrapDeleteService.deleteByTargetId(ContentType.POST, postId);
        contentImageRepository.deleteAllByContent(ContentType.POST, postId);
        Post post = postValidateService.getPostById(postId);
        postDeleteService.postDelete(post);
    }
}
