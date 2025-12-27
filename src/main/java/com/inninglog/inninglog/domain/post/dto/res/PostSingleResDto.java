package com.inninglog.inninglog.domain.post.dto.res;

import com.inninglog.inninglog.domain.contentImage.dto.res.ImageListResDto;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;

@Schema(description = "게시글 단일 조회 응답 DTO")
public record PostSingleResDto(

        @Schema(description = "작성자 요약 정보")
        MemberShortResDto member,

        @Schema(description = "내가 쓴 글인지 여부")
        boolean writedByMe,

        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(description = "팀 숏 코드", example = "LG")
        String teamShortCode,

        @Schema(description = "게시글 제목", example = "오늘 두산 왜이럼")
        String title,

        @Schema(description = "게시글 본문", example = "아 진짜 이 팀... ㅠㅠ")
        String content,

        @Schema(description = "게시글 좋아요 수", example = "12")
        long likeCount,

        @Schema(description = "내가 좋아요 누른 여부", example = "false")
        boolean likedByMe,

        @Schema(description = "게시글 스크랩 수", example = "3")
        long scrapCount,

        @Schema(description = "내가 스크랩한 여부", example = "false")
        boolean scrapedByMe,

        @Schema(description = "댓글 수", example = "5")
        long commentCount,

        @Schema(description = "작성일", example = "2025-01-31 14:22")
        String postAt,

        @Schema(description = "수정 여부", example = "false")
        boolean isEdit,

        @Schema(description = "이미지 리스트")
        ImageListResDto imageListResDto
) {

    public static PostSingleResDto of(
            Post post,
            MemberShortResDto memberShortResDto,
            ImageListResDto imageListResDto,
            boolean writedByMe,
            boolean likedByMe,
            boolean scrapedByMe){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return new PostSingleResDto(
                memberShortResDto,
                writedByMe,
                post.getId(),
                post.getTeamShortCode(),
                post.getTitle(),
                post.getContent(),
                post.getLikeCount(),
                likedByMe,
                post.getScrapCount(),
                scrapedByMe,
                post.getCommentCount(),
                post.getPostAt().format(formatter),
                post.isEdit(),
                imageListResDto
        );
    }
}