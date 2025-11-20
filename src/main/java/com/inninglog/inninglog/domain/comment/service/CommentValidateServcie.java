package com.inninglog.inninglog.domain.comment.service;

import static com.inninglog.inninglog.global.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.inninglog.inninglog.global.exception.ErrorCode.ROOT_COMMENT_NOT_FOUND;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentValidateServcie {

    private final CommentRepository commentRepository;

    //부모 댓글 검증
    @Transactional(readOnly = true)
    public void validateRootComment (Long commentId){
        commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ROOT_COMMENT_NOT_FOUND));
    }

    //댓글 아이디로 반환
    @Transactional(readOnly = true)
    public Comment getCommentId (Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }

    //삭제 되지 않은 뎃글 아이디로 반환
    @Transactional(readOnly = true)
    public Comment getCommentIdAndIsDeletedFalse(Long commentId){
        return commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }
}
