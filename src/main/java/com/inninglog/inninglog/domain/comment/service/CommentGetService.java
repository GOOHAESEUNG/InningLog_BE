package com.inninglog.inninglog.domain.comment.service;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.dto.res.CommentListResDto;
import com.inninglog.inninglog.domain.comment.dto.res.CommentResDto;
import com.inninglog.inninglog.domain.comment.repository.CommentRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.service.LikeValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.res.MemberShortResDto;
import com.inninglog.inninglog.domain.member.service.MemberGetService;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentGetService {
    private final CommentRepository commentRepository;
    private final MemberGetService memberGetService;
    private final LikeValidateService likeValidateService;
    private final MemberValidateService memberValidateService;

    @Transactional(readOnly = true)
    public CommentListResDto getCommentList(ContentType contentType, Long targetId, Member me){
       // N+1 최적화: Fetch Join으로 Member를 한 번에 조회
       List<Comment> comments = commentRepository.findAllWithMemberByContentTypeAndTargetId(contentType, targetId);
       List<CommentResDto> results = buildCommentTree(comments, me);

       return CommentListResDto.from(results);
    }

    //대댓글 매핑 (N+1 최적화 적용)
    @Transactional(readOnly = true)
    protected List<CommentResDto> buildCommentTree(List<Comment> comments, Member me) {

        Map<Long, CommentResDto> map = new HashMap<>();
        List<CommentResDto> roots = new ArrayList<>();

        // N+1 최적화: 모든 댓글 ID에 대해 좋아요 여부를 한 번에 조회
        List<Long> commentIds = comments.stream().map(Comment::getId).toList();
        Set<Long> likedCommentIds = likeValidateService.findLikedTargetIds(ContentType.COMMENT, commentIds, me);

        // 1) DTO 변환 후 map에 넣기
        for (Comment c : comments) {
            // Member는 이미 Fetch Join으로 로딩됨
            MemberShortResDto memberShortResDto = memberGetService.toMemberShortResDto(c.getMember());

            // 배치 조회 결과에서 좋아요 여부 확인
            boolean likedByMe = likedCommentIds.contains(c.getId());
            boolean writedByMe = memberValidateService.checkPostWriter(c.getMember().getId(), me.getId());
            CommentResDto dto = CommentResDto.of(c, writedByMe, likedByMe, memberShortResDto);
            map.put(c.getId(), dto);
        }

        // 2) 부모/자식 연결
        for (Comment c : comments) {
            if (c.getRootCommentId() == null) {
                roots.add(map.get(c.getId()));     // 루트 댓글
            } else {
                CommentResDto parent = map.get(c.getRootCommentId());
                parent.replies().add(map.get(c.getId()));     // 대댓글 붙이기
            }
        }

        return roots;
    }

    //마이페이지: 내가 댓글 단 게시글 ID 조회
    @Transactional(readOnly = true)
    public Slice<Long> getCommentedPostIds(Member member, Pageable pageable) {
        return commentRepository.findDistinctTargetIdsByMemberAndContentType(member, ContentType.POST, pageable);
    }
}
