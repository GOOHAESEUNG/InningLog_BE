package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCreateService {

    private final CommentRepository commentRepository;

    //댓글 생성
    public void createComment(ContentType contentType, CommentCreateReqDto dto, Long postId, Member member){
        Comment comment = Comment.of(dto, contentType, postId, member);
        commentRepository.save(comment);
    }
}
