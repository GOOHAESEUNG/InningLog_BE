package com.inninglog.inninglog.domain.post.domain;

import com.inninglog.inninglog.domain.comment.domain.CommentableContent;
import com.inninglog.inninglog.domain.like.domain.LikeableContent;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.post.dto.req.PostCreateReqDto;
import com.inninglog.inninglog.domain.scrap.domain.ScrapableContent;
import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity implements LikeableContent, ScrapableContent, CommentableContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    private long likeCount;

    private long scrapCount;

    private long commentCount;

    private String thumbnailUrl;

    private LocalDateTime postAt;

    private boolean isEdit=false;

    private String team_shortCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public static Post of(PostCreateReqDto dto, String team_shortCode, Member member) {
        return Post.builder()
                .title(dto.title())
                .content(dto.content())
                .team_shortCode(team_shortCode)
                .likeCount(0L)
                .scrapCount(0L)
                .commentCount(0L)
                .postAt(LocalDateTime.now())
                .isEdit(false)
                .member(member)
                .build();
    }

    @Override
    public void increaseCommentCount(){
        this.commentCount++;
    }

    @Override
    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
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

    @Override
    public void increaseScrapCount(){
        this.scrapCount++;
    }

    @Override
    public void decreaseScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }
}
