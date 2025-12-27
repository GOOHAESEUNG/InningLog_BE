package com.inninglog.inninglog.domain.contentImage.domain;

import com.inninglog.inninglog.domain.contentImage.dto.req.ImageCreateReqDto;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contentImage_id")
    private Long id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private String originalUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(nullable = false)
    private Long targetId;

    public static ContentImage of (ContentType contentType, Long targetId, String originalUrl, ImageCreateReqDto dto){
        return ContentImage.builder()
                .sequence(dto.sequence())
                .originalUrl(originalUrl)
                .contentType(contentType)
                .targetId(targetId)
                .build();
    }

    public void updateSequence(Integer seq) {
        this.sequence = seq;
    }
}
