package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.repository.JournalRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * N+1 쿼리 최적화 테스트
 *
 * 이 테스트는 댓글 조회 시 N+1 문제가 해결되었는지 확인합니다.
 * SQL 로깅을 활성화하고 콘솔에서 실행되는 쿼리 수를 직접 확인하세요.
 *
 * 최적화 전: 1(댓글 조회) + N(Member 조회) + N(Like 조회) = 2N+1 쿼리
 * 최적화 후: 1(댓글+Member Fetch Join) + 1(Like 배치 조회) = 2 쿼리
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentGetServiceN1Test {

    @Autowired
    private CommentGetService commentGetService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private EntityManager entityManager;

    private Member testMember;
    private Journal testJournal;

    @BeforeEach
    void setUp() {
        // 테스트 멤버 생성
        testMember = Member.builder()
                .kakaoId(12345L)
                .nickname("테스터")
                .profile_url("https://test.com/profile.jpg")
                .build();
        memberRepository.save(testMember);

        // 테스트 저널 생성
        testJournal = Journal.builder()
                .member(testMember)
                .review_text("테스트 저널")
                .date(LocalDateTime.now())
                .ourScore(3)
                .theirScore(2)
                .build();
        journalRepository.save(testJournal);

        // 여러 멤버와 댓글 생성 (N+1 테스트를 위해)
        for (int i = 0; i < 10; i++) {
            Member writer = Member.builder()
                    .kakaoId(10000L + i)
                    .nickname("작성자" + i)
                    .profile_url("https://test.com/profile" + i + ".jpg")
                    .build();
            memberRepository.save(writer);

            Comment comment = Comment.builder()
                    .content("댓글 내용 " + i)
                    .commentAt(LocalDateTime.now())
                    .rootCommentId(null)
                    .likeCount(i)
                    .isDeleted(false)
                    .contentType(ContentType.JOURNAL)
                    .targetId(testJournal.getId())
                    .member(writer)
                    .build();
            commentRepository.save(comment);
        }

        // 영속성 컨텍스트 초기화 (캐시 클리어)
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("N+1 최적화 테스트: 댓글 10개 조회 시 쿼리 수 확인")
    void testN1QueryOptimization() {
        System.out.println("\n========== N+1 최적화 테스트 시작 ==========");
        System.out.println("댓글 10개 조회 - 최적화 후 예상 쿼리 수: 2개");
        System.out.println("- 1개: 댓글 + Member Fetch Join");
        System.out.println("- 1개: Like 배치 조회 (IN 쿼리)");
        System.out.println("============================================\n");

        // when
        CommentListResDto result = commentGetService.getCommentList(
                ContentType.JOURNAL,
                testJournal.getId(),
                testMember
        );

        System.out.println("\n========== N+1 최적화 테스트 종료 ==========");
        System.out.println("조회된 댓글 수: " + result.comments().size());
        System.out.println("============================================\n");

        // then
        assertThat(result.comments()).hasSize(10);
    }
}
