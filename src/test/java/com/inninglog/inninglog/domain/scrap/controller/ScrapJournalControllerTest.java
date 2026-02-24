package com.inninglog.inninglog.domain.scrap.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.scrap.service.ScrapUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScrapJournalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScrapUsecase scrapUsecase;

    @Test
    @DisplayName("직관일지 스크랩 API 테스트")
    void scrapJournal() throws Exception {
        // given
        Long journalId = 1L;

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(scrapUsecase)
                .createScrap(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));

        // when & then
        mockMvc.perform(post("/journals/{journalId}/scraps", journalId)
                        .with(csrf())
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));

        verify(scrapUsecase, times(1))
                .createScrap(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));
    }

    @Test
    @DisplayName("직관일지 스크랩 취소 API 테스트")
    void unscrapJournal() throws Exception {
        // given
        Long journalId = 1L;

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(scrapUsecase)
                .deleteScrap(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));

        // when & then
        mockMvc.perform(delete("/journals/{journalId}/scraps", journalId)
                        .with(csrf())
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));

        verify(scrapUsecase, times(1))
                .deleteScrap(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));
    }
}
