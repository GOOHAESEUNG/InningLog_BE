package com.inninglog.inninglog.domain.like.repository;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.domain.Like;
import com.inninglog.inninglog.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    //해당 콘텐츠에 좋아요 누른 여부
    boolean existsByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);

    Optional<Like> findByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);

    // N+1 최적화: 여러 targetId에 대해 좋아요 여부를 한 번에 조회
    @Query("SELECT l.targetId FROM Like l WHERE l.contentType = :contentType AND l.targetId IN :targetIds AND l.member = :member")
    Set<Long> findLikedTargetIds(ContentType contentType, List<Long> targetIds, Member member);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.contentType = :contentType AND l.targetId = :targetId")
    void deleteAllByContent(ContentType contentType, Long targetId);
}
