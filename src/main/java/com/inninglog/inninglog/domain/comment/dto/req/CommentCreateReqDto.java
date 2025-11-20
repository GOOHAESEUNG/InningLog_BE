package com.inninglog.inninglog.domain.comment.dto.req;

import jakarta.annotation.Nullable;

public record CommentCreateReqDto(
        @Nullable
        Long rootCommentId,

        String content
) {
}
