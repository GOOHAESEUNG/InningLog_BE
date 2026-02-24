package com.inninglog.inninglog.domain.scrap.repository;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    boolean existsByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);

    Optional<Scrap> findByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);

    // N+1 최적화: 여러 targetId에 대해 스크랩 여부를 한 번에 조회
    @Query("SELECT s.targetId FROM Scrap s WHERE s.contentType = :contentType AND s.targetId IN :targetIds AND s.member = :member")
    Set<Long> findScrapedTargetIds(ContentType contentType, List<Long> targetIds, Member member);

    @Modifying
    @Query("DELETE FROM Scrap s WHERE s.contentType = :contentType AND s.targetId = :targetId")
    void deleteAllByContent(ContentType contentType, Long targetId);

    // 마이페이지: 내가 스크랩한 게시글 ID 조회 (최신순)
    @Query("SELECT s.targetId FROM Scrap s WHERE s.member = :member AND s.contentType = :contentType ORDER BY s.createdAt DESC")
    Slice<Long> findTargetIdsByMemberAndContentType(Member member, ContentType contentType, Pageable pageable);
}
