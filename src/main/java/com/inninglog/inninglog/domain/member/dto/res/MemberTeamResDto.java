package com.inninglog.inninglog.domain.member.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberTeamResDto(
        @Schema(description = "응원팀 shortCode", example = "LG")
        String teamShortCode
) {
    public static MemberTeamResDto from(String teamShortCode){
        return new MemberTeamResDto(teamShortCode);
    }
}
