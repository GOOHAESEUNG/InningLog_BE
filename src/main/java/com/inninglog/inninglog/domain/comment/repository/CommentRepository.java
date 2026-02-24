package com.inninglog.inninglog.domain.comment.repository;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByContentTypeAndTargetIdOrderByCommentAtDesc(ContentType contentType, Long targetId);

    // N+1 최적화: Member를 한 번에 조회
    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.contentType = :contentType AND c.targetId = :targetId ORDER BY c.commentAt DESC")
    List<Comment> findAllWithMemberByContentTypeAndTargetId(ContentType contentType, Long targetId);

    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.contentType = :contentType AND c.targetId = :targetId")
    void deleteAllByContent(ContentType contentType, Long targetId);

    // 마이페이지: 내가 댓글 단 게시글 ID 조회 (중복 제거, 최신순)
    @Query("SELECT DISTINCT c.targetId FROM Comment c WHERE c.member = :member AND c.contentType = :contentType ORDER BY c.targetId DESC")
    Slice<Long> findDistinctTargetIdsByMemberAndContentType(Member member, ContentType contentType, Pageable pageable);
}
