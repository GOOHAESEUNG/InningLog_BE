package com.inninglog.inninglog.domain.member.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.domain.kakao.dto.KakaoUserInfoResDTO;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.req.MemberWithFlagReqDto;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public MemberWithFlagReqDto saveOrUpdateMember(KakaoUserInfoResDTO userInfo) {
        Optional<Member> existing = memberRepository.findByKakaoId(userInfo.getId());

        if (existing.isPresent()) {
            Member member = existing.get();
            member.updateInfo(userInfo);
            log.info("📌 [saveOrUpdateMember] kakaoId={} 기존 회원 정보 업데이트: isNew=false",
                    userInfo.getId());
            return new MemberWithFlagReqDto(member, false);
        } else {
            Member newMember = Member.fromKakaoDto(userInfo);
            memberRepository.save(newMember);
            log.info("📌 [saveOrUpdateMember] kakaoId={} 새 회원 가입: isNew=true",
                    userInfo.getId());
            return new MemberWithFlagReqDto(newMember, true);
        }
    }

    //닉네임 업데이트
    @Transactional
    public void updateNickname(Long memberId, String nickname) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.info("📌 [updateNickname] memberId={} 존재하지 않는 회원", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        if (memberRepository.existsByNickname(nickname)) {
            log.info("📌 [updateNickname] nickname='{}' 중복 닉네임 시도", nickname);
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        member.setNickname(nickname);
        log.info("📌 [updateNickname] memberId={} 닉네임 변경 완료: nickname='{}'",
                memberId, nickname);
    }


    //유저 타입 & 응원 팀 설정
    @Transactional
    public void updateMemberType(Long memberId, String teamShortCode) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.info("📌 [updateMemberType] memberId={} 존재하지 않는 회원", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        if (member.getTeam() != null) {
            log.info("📌 [updateMemberType] memberId={} 이미 팀 설정된 유저", memberId);
            throw new CustomException(ErrorCode.ALREADY_SET);
        }

        Team team = teamRepository.findByShortCode(teamShortCode)
                .orElseThrow(() -> {
                    log.info("📌 [updateMemberType] teamShortCode='{}' 존재하지 않는 팀 코드", teamShortCode);
                    return new CustomException(ErrorCode.TEAM_NOT_FOUND);
                });

        member.setTeam(team);
        log.info("📌 [updateMemberType] memberId={} 응원팀 설정 완료: teamShortCode='{}'",
                memberId, teamShortCode);
    }

    @Transactional
    public void setupMemberInfo(Long memberId, String nickname, String teamShortCode) {
        updateNickname(memberId, nickname);
        updateMemberType(memberId, teamShortCode);
    }
}