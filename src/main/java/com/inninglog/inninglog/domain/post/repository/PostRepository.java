package com.inninglog.inninglog.domain.post.repository;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.domain.Post;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Slice<Post> findByTeamShortCodeOrderByPostAtDesc(String teamShortCode, Pageable pageable);

    // N+1 최적화: Member를 한 번에 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.teamShortCode = :teamShortCode ORDER BY p.postAt DESC")
    Slice<Post> findWithMemberByTeamShortCode(String teamShortCode, Pageable pageable);

    // 인기 게시글 조회: 좋아요 수 기준 (N+1 최적화)
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.likeCount >= :minLikeCount ORDER BY p.postAt DESC")
    Slice<Post> findPopularPostsWithMember(long minLikeCount, Pageable pageable);

    // 마이페이지: 내가 쓴 글 조회 (N+1 최적화)
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.member = :member ORDER BY p.postAt DESC")
    Slice<Post> findByMemberWithMember(Member member, Pageable pageable);

    // 마이페이지: ID 목록으로 게시글 조회 (N+1 최적화)
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.id IN :ids")
    List<Post> findAllByIdInWithMember(List<Long> ids);
}