package com.inninglog.inninglog.domain.journal.usecase;

import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.domain.journal.dto.res.*;
import com.inninglog.inninglog.global.dto.SliceResponse;
import com.inninglog.inninglog.domain.journal.service.JournalGetService;
import com.inninglog.inninglog.domain.journal.service.JournalService;
import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.dto.gameSchdule.GameSchResDto;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.kbo.service.GameGetService;
import com.inninglog.inninglog.domain.like.service.LikeValidateService;
import com.inninglog.inninglog.domain.scrap.service.ScrapValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.stadium.service.StadiumValidateService;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.service.TeamGetService;
import com.inninglog.inninglog.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalUsecase {

    private final JournalService journalService;
    private final JournalGetService journalGetService;
    private final MemberValidateService memberValidateService;
    private final TeamGetService teamGetService;
    private final StadiumValidateService stadiumValidateService;
    private final GameReportService gameReportService;
    private final GameGetService gameGetService;
    private final S3Uploader s3Uploader;
    private final LikeValidateService likeValidateService;
    private final ScrapValidateService scrapValidateService;


    //직관 일지 생성
    @Transactional
    public JourCreateResDto createJournal(Long memberId, JourCreateReqDto dto) {
       Member member = memberValidateService.findById(memberId);
       Team opponentTeam = teamGetService.validateTeam(dto.getOpponentTeamSC());
       Stadium stadium = stadiumValidateService.validateStadium(dto.getStadiumSC());
       Journal journal = journalService.createJournal(dto, member, opponentTeam, stadium);
       gameReportService.createVisitedGame(member.getId(), dto.getGameId(),journal.getId());

       return JourCreateResDto.from(journal);
    }

    //직관 일지 조회 - 캘린더
    @Transactional(readOnly = true)
    public List<JournalCalListResDto> getJournalsByMemberCal(Long memberId, ResultScore resultScore) {
        Member member = memberValidateService.findById(memberId);
        List<Journal> journals = journalService.getJournalsByMemberCal(member, resultScore);

        return journals.stream()
                .map(JournalCalListResDto::from)
                .collect(Collectors.toList());
    }

    //직관 일지 조회 - 모아보기
    @Transactional(readOnly = true)
    public Page<JournalSumListResDto> getJournalsByMemberSum(
            Long memberId, Pageable pageable, ResultScore resultScore) {
        Member member = memberValidateService.findById(memberId);
        Page<Journal> journals = journalService.getJournalsByMemberSum(member, pageable, resultScore);

        // N+1 최적화: 좋아요/스크랩 여부를 한 번에 조회
        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();
        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        return journals.map(
                journal -> JournalSumListResDto.from(
                        journal,
                        s3Uploader.generatePresignedGetUrl(journal.getMedia_url()),
                        member.getTeam().getShortCode(),
                        likedIds.contains(journal.getId()),
                        scrapedIds.contains(journal.getId())
                )
        );
    }

    //일지 기본 정보 제공
    @Transactional(readOnly = true)
    public JourGameResDto infoPreJournal(Long memberId, String gameId){
        Member member = memberValidateService.findById(memberId);
        Game game = gameGetService.findById(gameId);
        String opponentTeamSC = teamGetService.getOpponentTeamSC(member, game);

        return JourGameResDto.fromGame(member.getTeam().getShortCode(), opponentTeamSC, game );
    }

    //해당 일자의 경기 가져오기
    @Transactional(readOnly = true)
    public GameSchResDto getSingleGameSch(Long memberId, LocalDate gameDate) {
        Member member = memberValidateService.findById(memberId);
        Long supportTeamId = teamGetService.getSupportTeamId(member);
        Game game = gameGetService.findByDateAndTeamId(gameDate,supportTeamId);

        return GameSchResDto.from(game, supportTeamId);
    }

    //특정 직관 일지 조회
    @Transactional(readOnly = true)
    public JourUpdateResDto getDetailJournal(Long memberId, Long journalId){
        Member member = memberValidateService.findById(memberId);
        Journal journal = journalGetService.getJournalById(journalId);
        String presignedUrl = s3Uploader.generatePresignedGetUrl(journal.getMedia_url());
        boolean likedByMe = likeValidateService.likedByMe(ContentType.JOURNAL, journalId, member);
        boolean scrapedByMe = scrapValidateService.scrapedByMe(ContentType.JOURNAL, journalId, member);
        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal, presignedUrl, likedByMe, scrapedByMe);
        if(journal.getSeatView() == null){
            return JourUpdateResDto.from(jourDetailResDto, null);
        }

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());
    }

    //특정 직관 일지 수정
    @Transactional
    public JourUpdateResDto updateJournal(Long memberId, Long journalId, JourUpdateReqDto dto) {
        Member member = memberValidateService.findById(memberId);
        Journal journal = journalGetService.getJournalById(journalId);
        journalService.accessToJournal(memberId, journal.getMember().getId());
        journalService.updateJournal(journal, dto);
        String presignedUrl = s3Uploader.generatePresignedGetUrl(journal.getMedia_url());
        boolean likedByMe = likeValidateService.likedByMe(ContentType.JOURNAL, journalId, member);
        boolean scrapedByMe = scrapValidateService.scrapedByMe(ContentType.JOURNAL, journalId, member);

        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal, presignedUrl, likedByMe, scrapedByMe);

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());
    }

    //공개 일지 피드 조회
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> getPublicJournalFeed(Long memberId, String teamShortCode, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Journal> journals;

        // teamShortCode가 "ALL"이면 전체 조회, 그 외에는 팀별 조회
        if ("ALL".equalsIgnoreCase(teamShortCode)) {
            journals = journalGetService.getPublicJournals(pageable);
        } else {
            journals = journalGetService.getPublicJournalsByTeam(teamShortCode, pageable);
        }

        // N+1 최적화: 좋아요/스크랩 여부를 한 번에 조회
        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();

        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        Slice<JournalFeedResDto> dtoSlice = journals.map(journal -> {
            boolean writedByMe = journal.getMember().getId().equals(memberId);
            boolean likedByMe = likedIds.contains(journal.getId());
            boolean scrapedByMe = scrapedIds.contains(journal.getId());

            return JournalFeedResDto.from(
                    journal,
                    s3Uploader.generatePresignedGetUrl(journal.getMedia_url()),
                    writedByMe,
                    likedByMe,
                    scrapedByMe
            );
        });

        return SliceResponse.of(dtoSlice);
    }

    // 마이페이지: 내가 쓴 직관 일지 목록
    @Transactional(readOnly = true)
    public SliceResponse<JournalSumListResDto> getMyJournals(Member member, Pageable pageable) {
        Slice<Journal> journals = journalGetService.getMyJournals(member, pageable);

        // N+1 최적화: 좋아요/스크랩 여부를 한 번에 조회
        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();
        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        Slice<JournalSumListResDto> dtoSlice = journals.map(
                journal -> JournalSumListResDto.from(
                        journal,
                        s3Uploader.generatePresignedGetUrl(journal.getMedia_url()),
                        member.getTeam().getShortCode(),
                        likedIds.contains(journal.getId()),
                        scrapedIds.contains(journal.getId())
                )
        );

        return SliceResponse.of(dtoSlice);
    }
}
