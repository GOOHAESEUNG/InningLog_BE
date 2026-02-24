package com.inninglog.inninglog.domain.seatView.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.seatView.dto.req.SeatCreateReqDto;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
@Setter
public class SeatView extends BaseTimeEntity { //직관 일지

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memebr_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private Journal journal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    private String section;

    private String seatRow;

    /**
     * @deprecated ContentImage 테이블로 이전됨. 추후 제거 예정.
     */
    @Deprecated
    private String view_media_url;


    public static SeatView from(SeatCreateReqDto dto, Member member, Journal journal, Stadium stadium, Zone zone) {
        return SeatView.builder()
                .member(member)
                .journal(journal)
                .stadium(stadium)
                .zone(zone)
                .section(dto.getSection())
                .seatRow(dto.getSeatRow())
                .build();
    }

}
