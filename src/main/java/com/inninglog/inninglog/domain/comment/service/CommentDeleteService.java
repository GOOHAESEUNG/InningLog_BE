package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentRepository commentRepository;

    @Transactional
    public void delete(Comment comment) {
        comment.deleteComment();
    }

    @Transactional
    public void deleteByTargetId(ContentType contentType, Long targetId) {
        commentRepository.deleteAllByContent(contentType, targetId);
    }
}
