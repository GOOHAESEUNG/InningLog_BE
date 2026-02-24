package com.inninglog.inninglog.domain.journal.dto.res;

import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;

@Schema(description = "공개 피드용 일지 응답 DTO")
public record JournalFeedResDto(

        @Schema(description = "일지 ID", example = "123")
        Long journalId,

        @Schema(description = "썸네일 이미지 URL (presigned)", example = "https://s3.amazonaws.com/.../image.jpg")
        String thumbnailUrl,

        @Schema(description = "작성자 정보")
        MemberShortResDto member,

        @Schema(description = "내가 작성한 일지인지 여부", example = "false")
        boolean writedByMe,

        @Schema(description = "후기 미리보기 (최대 50자)", example = "오늘 경기 정말 재밌었다! 우리 팀이 역전승...")
        String reviewPreview,

        @Schema(description = "작성 시간", example = "2025-06-03 18:30")
        String createdAt,

        @Schema(description = "좋아요 수", example = "15")
        long likeCount,

        @Schema(description = "내가 좋아요 눌렀는지 여부", example = "true")
        boolean likedByMe,

        @Schema(description = "댓글 수", example = "3")
        long commentCount,

        @Schema(description = "스크랩 수", example = "2")
        long scrapCount,

        @Schema(description = "내가 스크랩했는지 여부", example = "false")
        boolean scrapedByMe
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int PREVIEW_MAX_LENGTH = 50;

    public static JournalFeedResDto from(Journal journal, String presignedUrl,
                                          boolean writedByMe, boolean likedByMe, boolean scrapedByMe) {
        String reviewText = journal.getReview_text();
        String preview = (reviewText != null && reviewText.length() > PREVIEW_MAX_LENGTH)
                ? reviewText.substring(0, PREVIEW_MAX_LENGTH) + "..."
                : reviewText;

        return new JournalFeedResDto(
                journal.getId(),
                presignedUrl,
                MemberShortResDto.from(journal.getMember()),
                writedByMe,
                preview,
                journal.getCreatedAt().format(FORMATTER),
                journal.getLikeCount(),
                likedByMe,
                journal.getCommentCount(),
                journal.getScrapCount(),
                scrapedByMe
        );
    }
}
