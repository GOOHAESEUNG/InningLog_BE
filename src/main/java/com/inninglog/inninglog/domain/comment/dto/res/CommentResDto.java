package com.inninglog.inninglog.domain.comment.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "댓글 단일 응답 DTO (대댓글 포함)")
public record CommentResDto (

        @Schema(description = "댓글 ID", example = "12")
        long commentId,

        @Schema(description = "작성자 정보")
        MemberShortResDto memberShortResDto,

        @Schema(description = "내가 작성한 댓글인지 여부", example = "false")
        boolean writedByMe,

        @Schema(description = "댓글 내용", example = "와 오늘 경기 미쳤다 ㄷㄷ")
        String content,

        @Schema(description = "작성 시각", example = "2025-01-31 13:25")
        String commentAt,

        @Schema(description = "좋아요 수", example = "3")
        long likeCount,

        @Schema(description = "내가 좋아요 누른 여부", example = "false")
        boolean likedByMe,

        @Schema(description = "삭제 여부(soft delete)", example = "false")
        boolean isDeleted,

        @Schema(description = "대댓글 리스트 (재귀 구조)", nullable = true)
        List<CommentResDto> replies
){
    private static final String DELETED_MESSAGE = "삭제된 댓글입니다.";

    public static CommentResDto of (Comment comment,
                                    boolean writedByMe,
                                    boolean likedByMe,
                                    MemberShortResDto memberShortResDto,
                                    List<CommentResDto> replies) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (comment.isDeleted()) {
            return new CommentResDto(
                    comment.getId(),
                    null,
                    false,
                    DELETED_MESSAGE,
                    comment.getCommentAt().format(formatter),
                    0L,
                    false,
                    true,
                    replies
            );
        }

        return new CommentResDto(
                comment.getId(),
                memberShortResDto,
                writedByMe,
                comment.getContent(),
                comment.getCommentAt().format(formatter),
                comment.getLikeCount(),
                likedByMe,
                comment.isDeleted(),
                replies
        );
    }

    public static CommentResDto of(Comment comment,
                                   boolean writedByMe,
                                   boolean likedByMe,
                                   MemberShortResDto memberShortResDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (comment.isDeleted()) {
            return new CommentResDto(
                    comment.getId(),
                    null,
                    false,
                    DELETED_MESSAGE,
                    comment.getCommentAt().format(formatter),
                    0L,
                    false,
                    true,
                    new ArrayList<>()
            );
        }

        return new CommentResDto(
                comment.getId(),
                memberShortResDto,
                writedByMe,
                comment.getContent(),
                comment.getCommentAt().format(formatter),
                comment.getLikeCount(),
                likedByMe,
                comment.isDeleted(),
                new ArrayList<>()
        );
    }
}
