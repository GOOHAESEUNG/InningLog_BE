package com.inninglog.inninglog.domain.journal.dto.res;


import com.inninglog.inninglog.domain.journal.domain.EmotionTag;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourDetailResDto {

    @Schema(description = "직관일지 Id", example = "12")
    private Long journalId;

    @Schema(description = "경기 날짜", example = "2025-06-25 18:30")
    private String gameDate;

    @Schema(description = "우리팀 숏코드", example = "OB")
    private String supportTeamSC;

    @Schema(description = "상대팀 숏코드", example = "SS")
    private String opponentTeamSC;

    @Schema(description = "우리팀 점수", example = "3")
    private int ourScore;

    @Schema(description = "상대팀 점수", example = "1")
    private int theirScore;

    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumSC;

    @Schema(description = "감정 태그 (감동/짜릿함/답답함/아쉬움/분노 중 하나)", example = "감동")
    private EmotionTag emotion;

    @Schema(description = "업로드한 이미지 파일명 (확장자 포함)", example = "photo123.jpeg")
    private String media_url; // optional

    @Schema(description = "후기글", example = "오늘 경기는 정말 재미있었다!", nullable = true)
    private String review_text; // optional

    @Schema(description = "댓글 수", example = "5")
    private long commentCount;

    @Schema(description = "좋아요 수", example = "10")
    private long likeCount;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private boolean likedByMe;

    @Schema(description = "스크랩 수", example = "3")
    private long scrapCount;

    @Schema(description = "현재 사용자의 스크랩 여부", example = "false")
    private boolean scrapedByMe;

    public static JourDetailResDto from(Member member, Journal journal, String presignedUrl, boolean likedByMe, boolean scrapedByMe) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = journal.getDate().format(formatter);

        return JourDetailResDto.builder()
                .journalId(journal.getId())
                .gameDate(formattedDate)
                .supportTeamSC(member.getTeam().getShortCode())
                .opponentTeamSC(journal.getOpponentTeam().getShortCode())
                .ourScore(journal.getOurScore())
                .theirScore(journal.getTheirScore())
                .stadiumSC(journal.getStadium().getShortCode())
                .emotion(journal.getEmotion())
                .media_url(presignedUrl)
                .review_text(journal.getReview_text())
                .commentCount(journal.getCommentCount())
                .likeCount(journal.getLikeCount())
                .likedByMe(likedByMe)
                .scrapCount(journal.getScrapCount())
                .scrapedByMe(scrapedByMe)
                .build();
    }
}
