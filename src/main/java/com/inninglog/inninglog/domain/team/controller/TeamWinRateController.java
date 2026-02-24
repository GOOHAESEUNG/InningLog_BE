package com.inninglog.inninglog.domain.team.controller;

import com.inninglog.inninglog.domain.team.dto.TeamWinRateDto;
import com.inninglog.inninglog.domain.team.service.TeamWinRateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kbo/team-rankings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "KBO 데이터 (내부용)", description = "크롤링 서버 통신용 API")
public class TeamWinRateController {

    private final TeamWinRateService teamWinRateService;

    /**
     * FastAPI에서 팀 승률 데이터 받아서 업데이트
     */
    @PostMapping("/win-rates")
    public ResponseEntity<TeamWinRateDto.WinRateUpdateResponse> updateTeamWinRates(
            @RequestBody TeamWinRateDto.WinRateUpdateRequest request) {

        log.info("팀 승률 업데이트 요청 수신: 날짜={}, 팀수={}", request.getDate(), request.getTotalTeams());

        try {
            TeamWinRateDto.WinRateUpdateResponse response = teamWinRateService.updateTeamWinRates(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("팀 승률 업데이트 실패: {}", e.getMessage(), e);

            TeamWinRateDto.WinRateUpdateResponse errorResponse = TeamWinRateDto.WinRateUpdateResponse.builder()
                    .message("팀 승률 업데이트 실패: " + e.getMessage())
                    .updatedTeams(0)
                    .totalTeams(request.getTotalTeams())
                    .skippedTeams(0)
                    .date(request.getDate())
                    .build();

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}