package com.inninglog.inninglog.domain.journal.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.domain.comment.domain.CommentableContent;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.scrap.domain.ScrapableContent;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.seatView.domain.SeatView;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
@Setter
public class Journal extends BaseTimeEntity implements CommentableContent, LikeableContent, ScrapableContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //경기 날짜
    private LocalDateTime date;

    //상대팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_team_id")
    private Team opponentTeam;

    //우리 팀 점수
    private int ourScore;

    //상대팀 점수
    private int theirScore;

    //경기 결과
    @Enumerated(EnumType.STRING)
    private ResultScore resultScore;

    //감정 태그
    @Enumerated(EnumType.STRING)
    private EmotionTag emotion;

    //후기 글
    private String review_text;

    //미디어 링크
    private String media_url;

    //경기장 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    //시야 정보
    @OneToOne(fetch = FetchType.LAZY)
    private SeatView seatView;

    //댓글 수
    @Builder.Default
    private long commentCount = 0;

    //좋아요 수
    @Builder.Default
    private long likeCount = 0;

    //스크랩 수
    @Builder.Default
    private long scrapCount = 0;

    //공개 여부
    @Builder.Default
    private boolean isPublic = false;

    @Override
    public void increaseCommentCount() {
        this.commentCount++;
    }

    @Override
    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    @Override
    public void increaseLikeCount() {
        this.likeCount++;
    }

    @Override
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    @Override
    public void increaseScrapCount() {
        this.scrapCount++;
    }

    @Override
    public void decreaseScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }

    public static Journal from(JourCreateReqDto dto, Member member, Team team, Stadium stadium) {
        ResultScore resultScore = ResultScore.of(dto.getOurScore(), dto.getTheirScore());

        // 날짜 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime parsedDate = LocalDateTime.parse(dto.getGameDate(), formatter);

        String filename = null;
        if (dto.getFileName() != null && !dto.getFileName().trim().isEmpty()) {
            filename = "journal/" + member.getId() + "/" + dto.getFileName();
        }

        return Journal.builder()
                .member(member)
                .date(parsedDate)
                .opponentTeam(team)
                .stadium(stadium)
                .resultScore(resultScore)
                .ourScore(dto.getOurScore())
                .theirScore(dto.getTheirScore())
                .emotion(dto.getEmotion())
                .review_text(dto.getReview_text())
                .media_url(filename)
                .isPublic(dto.isPublic())
                .build();
    }


    public void updateFrom(JourUpdateReqDto dto) {
        this.ourScore = dto.getOurScore();
        this.theirScore = dto.getTheirScore();
        this.resultScore = ResultScore.of(dto.getOurScore(), dto.getTheirScore());
        this.emotion = dto.getEmotion();
        this.review_text = dto.getReview_text();
        this.isPublic = dto.isPublic();

        // media_url 조건 분기 처리
        if (dto.getMedia_url() == null || dto.getMedia_url().trim().isEmpty()) {
            this.media_url = null;
        } else {
            this.media_url = dto.getMedia_url();
        }
    }

}
