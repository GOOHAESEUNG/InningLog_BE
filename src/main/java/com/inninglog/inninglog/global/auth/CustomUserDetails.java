package com.inninglog.inninglog.global.auth;

import com.inninglog.inninglog.domain.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final Long memberId;

    public CustomUserDetails(Member member, Long memberId) {
        this.member = member;
        this.memberId = memberId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getMemberType()));
    }

    @Override
    public String getUsername() {
        return member.getKakaoId().toString(); // 또는 nickname 등
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호 인증 안 하므로 null
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Member getMember() {
        return member;
    }

    public Long getMemberId() {return memberId;}
}
