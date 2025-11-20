package com.inninglog.inninglog.domain.member.dto.req;

import com.inninglog.inninglog.domain.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberWithFlagReqDto {
    private final Member member;
    private final boolean isNew;
}