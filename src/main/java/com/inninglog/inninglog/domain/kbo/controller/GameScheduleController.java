package com.inninglog.inninglog.domain.kbo.controller;

import com.inninglog.inninglog.domain.kbo.dto.MonthlyGameStatsDto;
import com.inninglog.inninglog.domain.kbo.dto.gameResult.GameResultRequestDto;
import com.inninglog.inninglog.domain.kbo.dto.gameSchdule.GameScheduleRequestDto;
import com.inninglog.inninglog.domain.kbo.dto.gameSchdule.GameScheduleResponseDto;
import com.inninglog.inninglog.domain.kbo.dto.playerstat.GameWithBoxscoreDto;
import com.inninglog.inninglog.domain.kbo.service.GameScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kbo/games")
@RequiredArgsConstructor
@Tag(name = "KBO 데이터 (내부용)", description = "크롤링 서버 통신용 API")
public class GameScheduleController {

    private final GameScheduleService gameScheduleService;

    /**
     * 월별 경기 일정 저장
     * POST /api/kbo/games/schedule
     */
    @PostMapping("/schedule")
    public ResponseEntity<GameScheduleResponseDto> saveMonthlySchedule(
            @RequestBody GameScheduleRequestDto requestDto) {

        log.info("월별 경기 일정 저장 요청: 연월={}, 경기 수={}",
                requestDto.getYearMonth(), requestDto.getGames().size());

        try {
            GameScheduleResponseDto response = gameScheduleService.saveMonthlySchedule(requestDto);

            log.info("월별 경기 일정 저장 성공: 저장={}, 중복={}, 오류={}",
                    response.getSavedCount(), response.getDuplicateCount(), response.getErrorCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("월별 경기 일정 저장 실패", e);
            return ResponseEntity.badRequest()
                    .body(GameScheduleResponseDto.error("월별 일정 저장 실패: " + e.getMessage()));
        }
    }

    /**
     * 경기 결과 업데이트
     * POST /api/kbo/games/results
     */
    @PostMapping("/results")
    public ResponseEntity<GameScheduleResponseDto> updateGameResults(
            @RequestBody GameResultRequestDto requestDto) {

        log.info("경기 결과 업데이트 요청: 날짜={}, 경기 수={}",
                requestDto.getGameDate(), requestDto.getGames().size());

        try {
            GameScheduleResponseDto response = gameScheduleService.updateGameResults(requestDto);

            log.info("경기 결과 업데이트 성공: 업데이트={}, 신규={}, 오류={}",
                    response.getUpdatedCount(), response.getSavedCount(), response.getErrorCount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("경기 결과 업데이트 실패", e);
            return ResponseEntity.badRequest()
                    .body(GameScheduleResponseDto.error("경기 결과 업데이트 실패: " + e.getMessage()));
        }
    }

    /**
     * 박스스코어 URL이 있는 경기 조회
     * GET /api/kbo/games/with-boxscore?gameDate=2025-06-01
     */
    @GetMapping("/with-boxscore")
    public ResponseEntity<List<GameWithBoxscoreDto>> getGamesWithBoxscore(
            @RequestParam String gameDate) {

        log.info("박스스코어 URL 있는 경기 조회: 날짜={}", gameDate);

        try {
            List<GameWithBoxscoreDto> games = gameScheduleService.getGamesWithBoxscore(gameDate);

            log.info("박스스코어 URL 있는 경기 조회 성공: {}경기", games.size());

            return ResponseEntity.ok(games);

        } catch (Exception e) {
            log.error("박스스코어 URL 있는 경기 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 특정 월의 경기 통계 조회
     * GET /api/kbo/games/stats?yearMonth=2025-06
     */
    @GetMapping("/stats")
    public ResponseEntity<MonthlyGameStatsDto> getMonthlyGameStats(
            @RequestParam String yearMonth) {

        log.info("월별 경기 통계 조회: 연월={}", yearMonth);

        try {
            MonthlyGameStatsDto stats = gameScheduleService.getMonthlyGameStats(yearMonth);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("월별 경기 통계 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }
}