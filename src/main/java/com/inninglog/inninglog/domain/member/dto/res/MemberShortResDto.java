package com.inninglog.inninglog.domain.member.dto.res;

import com.inninglog.inninglog.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "작성자 요약 정보 DTO")
public record MemberShortResDto(

        @Schema(description = "닉네임", example = "볼빨간스트라스버그")
        String nickName,

        @Schema(description = "프로필 이미지 URL(카카오)", example = "https://k.kakaocdn.net/.../img_640x640.jpg")
        String profile_url
) {
    public static MemberShortResDto from(Member member){
        return new MemberShortResDto(
                member.getNickname(),
                member.getKakao_profile_url()
        );
    }
}
