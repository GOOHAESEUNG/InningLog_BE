package com.inninglog.inninglog.domain.post.repository;

import com.inninglog.inninglog.domain.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Slice<Post> findByTeamShortCodeOrderByPostAtDesc(String teamShortCode, Pageable pageable);
}