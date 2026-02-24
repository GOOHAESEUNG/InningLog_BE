package com.inninglog.inninglog.domain.like.controller;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeUsecase;
import com.inninglog.inninglog.domain.member.domain.Member;
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
class LikeJournalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeUsecase likeUsecase;

    @Test
    @DisplayName("직관일지 좋아요 API 테스트")
    void likeJournal() throws Exception {
        // given
        Long journalId = 1L;

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(likeUsecase)
                .createLike(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));

        // when & then
        mockMvc.perform(post("/journals/{journalId}/likes", journalId)
                        .with(csrf())
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));

        verify(likeUsecase, times(1))
                .createLike(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));
    }

    @Test
    @DisplayName("직관일지 좋아요 취소 API 테스트")
    void unlikeJournal() throws Exception {
        // given
        Long journalId = 1L;

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(likeUsecase)
                .deleteLike(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));

        // when & then
        mockMvc.perform(delete("/journals/{journalId}/likes", journalId)
                        .with(csrf())
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));

        verify(likeUsecase, times(1))
                .deleteLike(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));
    }
}
