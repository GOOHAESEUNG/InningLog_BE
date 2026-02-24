package com.inninglog.inninglog.domain.journal.repository;

import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByMember(Member member);

    List<Journal> findAllByMemberAndResultScore(Member member, ResultScore resultScore);

    Page<Journal> findAllByMember(Member member, Pageable pageable);

    Page<Journal> findAllByMemberAndResultScore(Member member, ResultScore resultScore, Pageable pageable);

    @Query("SELECT j FROM Journal j JOIN FETCH j.member WHERE j.isPublic = true ORDER BY j.date DESC")
    Slice<Journal> findPublicJournals(Pageable pageable);

    @Query("SELECT j FROM Journal j JOIN FETCH j.member m JOIN FETCH m.team t " +
           "WHERE j.isPublic = true AND t.shortCode = :teamShortCode ORDER BY j.date DESC")
    Slice<Journal> findPublicJournalsByTeam(@Param("teamShortCode") String teamShortCode, Pageable pageable);

    // 마이페이지: 내가 쓴 직관 일지 (Slice 기반)
    @Query("SELECT j FROM Journal j WHERE j.member = :member ORDER BY j.date DESC")
    Slice<Journal> findByMemberOrderByDateDesc(@Param("member") Member member, Pageable pageable);
}
