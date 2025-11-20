package com.inninglog.inninglog.domain.scrap.repository;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.domain.Scrap;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    boolean existsByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);

    Optional<Scrap> findByContentTypeAndTargetIdAndMember(ContentType contentType, Long targetId, Member member);

    @Modifying
    @Query("DELETE FROM Scrap s WHERE s.contentType = :contentType AND s.targetId = :targetId")
    void deleteAllByContent(ContentType contentType, Long targetId);
}
