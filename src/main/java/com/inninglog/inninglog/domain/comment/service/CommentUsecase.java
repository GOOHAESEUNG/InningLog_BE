package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.domain.CommentableContent;
import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.contentType.ContentValidateService;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.domain.post.domain.Post;
import com.inninglog.inninglog.domain.post.service.PostCreateService;
import com.inninglog.inninglog.domain.post.service.PostUpdateService;
import com.inninglog.inninglog.domain.post.service.PostValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentUsecase {

    private final PostValidateService postValidateService;
    private final CommentCreateService commentCreateService;
    private final CommentValidateServcie commentValidateServcie;
    private final CommentDeleteService commentDeleteService;
    private final CommentGetService commentGetService;
    private final ContentValidateService contentValidateService;

    //댓글 생성
    @Transactional
    public void createComment(ContentType contentType, CommentCreateReqDto dto, Long postId, Member member){
        CommentableContent content = contentValidateService.validateContentToComment(contentType, postId);
        if(dto.rootCommentId() != null) {
            commentValidateServcie.validateRootComment(dto.rootCommentId());
        }
        commentCreateService.createComment(contentType, dto, postId, member);
        content.increaseCommentCount();
    }

    //댓글 목록 조회
    @Transactional(readOnly = true)
    public CommentListResDto getComments(ContentType contentType, Long postId){
        postValidateService.getPostById(postId);
        return commentGetService.getCommentList(contentType,postId);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId){
        Comment comment = commentValidateServcie.getCommentIdAndIsDeletedFalse(commentId);
        commentDeleteService.delete(comment);
    }
}
