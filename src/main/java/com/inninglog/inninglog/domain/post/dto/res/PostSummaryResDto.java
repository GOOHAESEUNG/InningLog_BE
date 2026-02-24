package com.inninglog.inninglog.domain.post.dto.res;

import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;

@Schema(description = "게시글 목록용 요약 DTO")
public record PostSummaryResDto(

        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(description = "팀 숏 코드", example = "LG")
        String teamShortCode,

        @Schema(description = "게시글 제목", example = "오늘 두산 왜이럼")
        String title,

        @Schema(description = "게시글 본문 (전체 원문)")
        String content,

        @Schema(description = "작성자 요약 정보")
        MemberShortResDto member,

        @Schema(description = "좋아요 수", example = "12")
        long likeCount,

        @Schema(description = "게시글 스크랩 수", example = "3")
        long scrapCount,

        @Schema(description = "댓글 수", example = "5")
        long commentCount,

        @Schema(description = "대표 이미지 URL (없으면 null)")
        String thumbImageUrl,

        @Schema(description = "게시글에 포함된 전체 이미지 개수", example = "3")
        Long imageCount,

        @Schema(description = "작성일", example = "2025-01-31 14:22")
        String postAt,

        @Schema(description = "내가 좋아요 눌렀는지 여부 (마이페이지에서만 표시)", example = "true")
        Boolean likedByMe,

        @Schema(description = "내가 스크랩했는지 여부 (마이페이지에서만 표시)", example = "false")
        Boolean scrapedByMe
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static PostSummaryResDto of(Post post, MemberShortResDto member) {
        return new PostSummaryResDto(
                post.getId(),
                post.getTeamShortCode(),
                post.getTitle(),
                post.getContent(),
                member,
                post.getLikeCount(),
                post.getScrapCount(),
                post.getCommentCount(),
                post.getThumbnailUrl(),
                post.getImageCount(),
                post.getPostAt().format(FORMATTER),
                null,
                null
        );
    }

    public static PostSummaryResDto of(Post post, MemberShortResDto member, boolean likedByMe, boolean scrapedByMe) {
        return new PostSummaryResDto(
                post.getId(),
                post.getTeamShortCode(),
                post.getTitle(),
                post.getContent(),
                member,
                post.getLikeCount(),
                post.getScrapCount(),
                post.getCommentCount(),
                post.getThumbnailUrl(),
                post.getImageCount(),
                post.getPostAt().format(FORMATTER),
                likedByMe,
                scrapedByMe
        );
    }
}