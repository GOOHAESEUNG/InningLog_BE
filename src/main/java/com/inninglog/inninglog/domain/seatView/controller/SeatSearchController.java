package com.inninglog.inninglog.domain.seatView.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.pageable.SimplePageResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatSearchRes;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.domain.seatView.service.SeatSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatViews/normal")
@RequiredArgsConstructor
@Tag(name = "좌석시야", description = "좌석 시야 후기 관련 API")
public class SeatSearchController {

    private final SeatSearchService seatSearchService;

    @Operation(
            summary = "일반 좌석 검색 (게시물 형태)",
            description = """
                    구장, 존, 구역, 열 정보를 통해 좌석 시야 후기를 검색합니다.  
                    모든 조건은 선택사항이지만, 열 정보만으로는 검색할 수 없습니다 (최소 존 정보 필요).
                    
                    ※ 결과는 **최신순으로 정렬**됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좌석 시야 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SeatSearchRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "검색 결과 있음",
                                            value = """
                                                    {
                                                      "code": "SEATVIEW_LIST_FETCHED",
                                                      "message": "시야 사진 조회 성공",
                                                      "data": {
                                                        "content": [
                                                          {
                                                            "seatViewId": 3,
                                                            "viewMediaUrl": "https://your-s3-bucket-url/image.jpg"
                                                          }
                                                        ],
                                                        "pageNumber": 0,
                                                        "pageSize": 10,
                                                        "totalElements": 3,
                                                        "totalPages": 1,
                                                        "last": false
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "검색 결과 없음",
                                            value = """
                                                    {
                                                      "code": "SEAT_VIEW_EMPTY",
                                                      "message": "해당 조건에 해당하는 시야 사진이 없습니다.",
                                                      "data": {
                                                        "content": [],
                                                        "pageNumber": 0,
                                                        "pageSize": 10,
                                                        "totalElements": 0,
                                                        "totalPages": 0,
                                                        "last": true
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (열 정보만 입력한 경우)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "에러 응답 예시",
                                    value = """
                                            {
                                              "code": "BAD_REQUEST",
                                              "message": "열 정보만으로는 검색할 수 없습니다. 최소 존 정보가 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    @GetMapping("/gallery")
    public ResponseEntity<SuccessResponse<SimplePageResponse<SeatViewDetailResult>>> searchSeats(

            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(
                    description = "구장 단축코드",
                    required = true,
                    example = "JAM",
                    schema = @Schema(type = "string", allowableValues = {
                            "JAM", "GOC", "ICN", "SUW", "DJN", "DAE", "BUS", "GWJ", "CHW"
                    })
            )
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = "존 단축코드 (선택사항)",
                    required = false,
                    example = "JAM_BLUE"
            )
            @RequestParam(required = false) String zoneShortCode,

            @Parameter(
                    description = "구역 정보 (선택사항)",
                    required = false,
                    example = "13"
            )
            @RequestParam(required = false) String section,

            @Parameter(
                    description = "열 정보 (선택사항, 단독 사용 불가 - 최소 존 정보 필요)",
                    required = false,
                    example = "3"
            )
            @RequestParam(required = false) String seatRow,

            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "페이지 크기 (한 페이지당 항목 수)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SeatViewDetailResult> response = seatSearchService.searchSeats(
                user.getMember().getId(), stadiumShortCode, zoneShortCode, section, seatRow, pageable
        );

        SuccessCode code = response.isEmpty() ? SuccessCode.SEATVIEW_EMPTY : SuccessCode.SEATVIEW_LIST_FETCHED;

        SimplePageResponse<SeatViewDetailResult> simplePage = SimplePageResponse.<SeatViewDetailResult>builder()
                .content(response.getContent())
                .pageNumber(response.getNumber())
                .pageSize(response.getSize())
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .isLast(response.isLast())
                .build();

        return ResponseEntity.ok(SuccessResponse.success(code, simplePage));
    }
}