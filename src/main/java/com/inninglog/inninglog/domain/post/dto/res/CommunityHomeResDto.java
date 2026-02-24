package com.inninglog.inninglog.domain.post.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "커뮤니티 홈 응답 DTO")
public record CommunityHomeResDto(

        @Schema(description = "내 응원팀 숏 코드", example = "LG")
        String supportTeamShortCode,

        @Schema(description = "인기 게시글 목록 (최신 2개)")
        List<CommunityHomePostResDto> popularPosts
) {

    public static CommunityHomeResDto of(String teamShortCode, List<CommunityHomePostResDto> posts) {
        return new CommunityHomeResDto(teamShortCode, posts);
    }
}
