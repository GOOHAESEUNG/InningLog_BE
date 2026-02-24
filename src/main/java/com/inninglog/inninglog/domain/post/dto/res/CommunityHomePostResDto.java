package com.inninglog.inninglog.domain.post.dto.res;

import com.inninglog.inninglog.domain.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;

@Schema(description = "커뮤니티 홈 인기 게시글 DTO")
public record CommunityHomePostResDto(

        @Schema(description = "게시글 ID", example = "45")
        Long postId,

        @Schema(description = "팀 숏 코드 (어느 팀 게시판인지)", example = "LG")
        String teamShortCode,

        @Schema(description = "게시글 제목", example = "역전승 후기")
        String title,

        @Schema(description = "게시글 본문")
        String content,

        @Schema(description = "좋아요 수", example = "24")
        long likeCount,

        @Schema(description = "스크랩 수", example = "5")
        long scrapCount,

        @Schema(description = "댓글 수", example = "18")
        long commentCount,

        @Schema(description = "대표 이미지 URL (없으면 null)")
        String thumbImageUrl,

        @Schema(description = "게시글에 포함된 전체 이미지 개수", example = "3")
        Long imageCount,

        @Schema(description = "작성일", example = "2025-06-03 18:30")
        String postAt,

        @Schema(description = "내가 좋아요 눌렀는지", example = "true")
        boolean likedByMe,

        @Schema(description = "내가 스크랩 했는지", example = "false")
        boolean scrapedByMe
) {

    public static CommunityHomePostResDto of(Post post, boolean likedByMe, boolean scrapedByMe) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new CommunityHomePostResDto(
                post.getId(),
                post.getTeamShortCode(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                post.getScrapCount(),
                post.getCommentCount(),
                post.getThumbnailUrl(),
                post.getImageCount(),
                post.getPostAt().format(formatter),
                likedByMe,
                scrapedByMe
        );
    }
}
