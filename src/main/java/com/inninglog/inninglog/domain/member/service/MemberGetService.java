package com.inninglog.inninglog.domain.member.service;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.member.dto.res.MemberTeamResDto;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberGetService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberShortResDto toMemberShortResDto(Member member) {
        return MemberShortResDto.from(member);
    }

    @Transactional(readOnly = true)
    public MemberTeamResDto getMemberTeam(Long memberId) {
        Member member = memberRepository.findByIdWithTeam(memberId)
                .orElseThrow();

        return MemberTeamResDto.from(
                member.getTeam().getShortCode()
        );
    }
}
