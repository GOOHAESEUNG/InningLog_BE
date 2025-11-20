package com.inninglog.inninglog.domain.comment.domain;

import com.inninglog.inninglog.domain.comment.dto.req.CommentCreateReqDto;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity implements LikeableContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commment_id")
    private Long id;

    private String content;

    private LocalDateTime commentAt;

    @Nullable
    private Long rootCommentId;

    private long likeCount;

    @Column(nullable = false)
    private boolean isDeleted=false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(nullable = false)
    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Comment of(CommentCreateReqDto dto, ContentType contentType, Long postId, Member member) {
        return Comment.builder()
                .content(dto.content())
                .commentAt(LocalDateTime.now())
                .rootCommentId(dto.rootCommentId())
                .likeCount(0L)
                .isDeleted(false)
                .contentType(contentType)
                .targetId(postId)
                .member(member)
                .build();
    }

    @Override
    public void increaseLikeCount(){
        this.likeCount++;
    }

    @Override
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void deleteComment() {
        this.content = null;
        this.isDeleted=true;
    }
}
