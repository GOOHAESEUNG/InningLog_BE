package com.inninglog.inninglog.domain.kakao.usecase;

import com.inninglog.inninglog.domain.kakao.dto.KakaoLoginResDTO;
import com.inninglog.inninglog.domain.kakao.dto.KakaoUserInfoResDTO;
import com.inninglog.inninglog.domain.kakao.service.KakaoService;
import com.inninglog.inninglog.global.auth.service.JwtProvider;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.req.MemberWithFlagReqDto;
import com.inninglog.inninglog.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAuthUseCase {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public KakaoLoginResDTO loginWithKakao(String code) {
        String kakaoAccessToken = kakaoService.getAccessToken(code);
        KakaoUserInfoResDTO userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        MemberWithFlagReqDto result = memberService.saveOrUpdateMember(userInfo);
        Member member = result.getMember();
        boolean isNewUser = result.isNew();

        String jwtAccessToken = jwtProvider.createToken(member.getId());
        String jwtRefreshToken = jwtProvider.createRefreshToken(member.getId());


        return new KakaoLoginResDTO(member.getNickname(), isNewUser, jwtAccessToken, jwtRefreshToken);
    }
}