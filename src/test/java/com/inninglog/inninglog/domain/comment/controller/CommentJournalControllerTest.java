package com.inninglog.inninglog.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentResDto;
import com.inninglog.inninglog.domain.comment.service.CommentUsecase;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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
class CommentJournalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentUsecase commentUsecase;

    @Test
    @DisplayName("직관일지 댓글 생성 API 테스트")
    @WithMockUser
    void createJournalComment() throws Exception {
        // given
        Long journalId = 1L;
        CommentCreateReqDto request = new CommentCreateReqDto(null, "테스트 댓글입니다");

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(commentUsecase)
                .createComment(eq(ContentType.JOURNAL), any(CommentCreateReqDto.class), eq(journalId), any(Member.class));

        // when & then
        mockMvc.perform(post("/journals/{journalId}/comments", journalId)
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));

        verify(commentUsecase, times(1))
                .createComment(eq(ContentType.JOURNAL), any(CommentCreateReqDto.class), eq(journalId), any(Member.class));
    }

    @Test
    @DisplayName("직관일지 대댓글 생성 API 테스트")
    @WithMockUser
    void createJournalReplyComment() throws Exception {
        // given
        Long journalId = 1L;
        Long rootCommentId = 5L;
        CommentCreateReqDto request = new CommentCreateReqDto(rootCommentId, "대댓글입니다");

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        doNothing().when(commentUsecase)
                .createComment(eq(ContentType.JOURNAL), any(CommentCreateReqDto.class), eq(journalId), any(Member.class));

        // when & then
        mockMvc.perform(post("/journals/{journalId}/comments", journalId)
                        .with(csrf())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }

    @Test
    @DisplayName("직관일지 댓글 목록 조회 API 테스트")
    @WithMockUser
    void getJournalComments() throws Exception {
        // given
        Long journalId = 1L;

        Member mockMember = Member.builder()
                .id(1L)
                .nickname("테스터")
                .kakaoId(12345L)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockMember, mockMember.getId());

        MemberShortResDto memberDto = new MemberShortResDto("작성자", "https://profile.url");

        CommentResDto comment1 = new CommentResDto(
                1L,
                memberDto,
                false,
                "첫 번째 댓글",
                "2025-01-21 14:30",
                5L,
                true,
                false,
                new ArrayList<>()
        );

        CommentResDto reply = new CommentResDto(
                2L,
                memberDto,
                true,
                "대댓글입니다",
                "2025-01-21 14:35",
                2L,
                false,
                false,
                new ArrayList<>()
        );

        comment1.replies().add(reply);

        CommentListResDto response = new CommentListResDto(List.of(comment1));

        when(commentUsecase.getComments(eq(ContentType.JOURNAL), eq(journalId), any(Member.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/journals/{journalId}/comments", journalId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.comments").isArray())
                .andExpect(jsonPath("$.data.comments[0].commentId").value(1))
                .andExpect(jsonPath("$.data.comments[0].content").value("첫 번째 댓글"))
                .andExpect(jsonPath("$.data.comments[0].likeCount").value(5))
                .andExpect(jsonPath("$.data.comments[0].likedByMe").value(true))
                .andExpect(jsonPath("$.data.comments[0].writedByMe").value(false))
                .andExpect(jsonPath("$.data.comments[0].replies").isArray())
                .andExpect(jsonPath("$.data.comments[0].replies[0].commentId").value(2))
                .andExpect(jsonPath("$.data.comments[0].replies[0].content").value("대댓글입니다"))
                .andExpect(jsonPath("$.data.comments[0].replies[0].writedByMe").value(true));

        verify(commentUsecase, times(1))
                .getComments(eq(ContentType.JOURNAL), eq(journalId), any(Member.class));
    }
}
