package com.inninglog.inninglog.domain.kbo.controller;

import com.inninglog.inninglog.domain.kbo.dto.playerstat.PlayerStatsSaveResult;
import com.inninglog.inninglog.domain.kbo.dto.playerstat.ReviewStatsDto;
import com.inninglog.inninglog.domain.kbo.service.PlayerStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kbo/player-stats")
@Tag(name = "KBO 데이터 (내부용)", description = "크롤링 서버 통신용 API")
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;

    @Operation(
            summary = "선수 기록 저장",
            description = "Python 크롤러에서 수집한 선수들의 투타 기록을 데이터베이스에 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "선수 기록 저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> savePlayerStats(
            @Parameter(description = "경기 ID (예: 20250601HHNC0)", required = true, example = "20250601HHNC0")
            @RequestParam String gameId,

            @Parameter(description = "투수 및 타자 기록 데이터", required = true)
            @RequestBody ReviewStatsDto reviewStatsDto) {

        try {
            log.info("선수 기록 저장 요청 - gameId: {}, 투수: {}명, 타자: {}명",
                    gameId,
                    reviewStatsDto.getPitchers().size(),
                    reviewStatsDto.getHitters().size());

            PlayerStatsSaveResult result = playerStatsService.savePlayerStats(gameId, reviewStatsDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "선수 기록 저장 완료");
            response.put("data", Map.of(
                    "newPlayersCount", result.getNewPlayersCount(),
                    "pitcherStatsCount", result.getPitcherStatsCount(),
                    "hitterStatsCount", result.getHitterStatsCount()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("선수 기록 저장 중 오류 발생", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "선수 기록 저장 실패: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(
            summary = "게임별 선수 기록 조회",
            description = "특정 게임의 모든 선수 기록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게임을 찾을 수 없음")
    })
    @GetMapping("/{gameId}")
    public ResponseEntity<Map<String, Object>> getPlayerStatsByGame(
            @Parameter(description = "경기 ID", required = true, example = "20250601HHNC0")
            @PathVariable String gameId) {

        try {
            // 여기서는 조회 로직 구현 (필요한 경우)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "조회 기능은 추후 구현 예정");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("선수 기록 조회 중 오류 발생", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "조회 실패: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}