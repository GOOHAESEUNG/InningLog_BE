package com.inninglog.inninglog.domain.journal.dto.req;

import com.inninglog.inninglog.domain.journal.domain.EmotionTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourCreateReqDto {
    @Schema(description = "게임 Id", example = "20250622OBLG0")
    private String gameId;

    @Schema(description = "경기 날짜 (LocalDateTime 형식)", example = "2025-06-03 18:30")
    private String gameDate;

    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumSC;

    @Schema(description = "상대팀 숏코드", example = "OB")
    private String opponentTeamSC;

    @Schema(description = "우리팀 점수", example = "3")
    private int ourScore;

    @Schema(description = "상대팀 점수", example = "1")
    private int theirScore;

    @Schema(description = "업로드할 이미지 파일명 (확장자 포함)", example = "photo123.jpeg")
    private String fileName;

    @Schema(description = "감정 태그 (감동/짜릿함/답답함/아쉬움/분노 중 하나)", example = "감동")
    private EmotionTag emotion;

    @Schema(description = "후기글 작성", example = "오늘 경기는 정말 재미있었다!")
    private String review_text;

    @Schema(description = "공개 여부 (true: 공개, false: 비공개)", example = "false")
    @Builder.Default
    private boolean isPublic = false;
}