package com.inninglog.inninglog.domain.comment.dto.res;

import java.util.List;

public record CommentListResDto(
        List<CommentResDto> comments
) {
    public static CommentListResDto from(List<CommentResDto> commentResDtos) {
        return new CommentListResDto(commentResDtos);
    }
}
