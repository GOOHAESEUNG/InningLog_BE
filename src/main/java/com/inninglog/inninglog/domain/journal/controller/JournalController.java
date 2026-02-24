package com.inninglog.inninglog.domain.journal.controller;

import com.inninglog.inninglog.domain.journal.dto.res.*;
import com.inninglog.inninglog.global.dto.SliceResponse;
import com.inninglog.inninglog.domain.journal.usecase.JournalUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.pageable.SimplePageResponse;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;

import com.inninglog.inninglog.domain.journal.dto.req.JourUpdateReqDto;

import com.inninglog.inninglog.domain.journal.service.JournalService;
import com.inninglog.inninglog.domain.kbo.dto.gameSchdule.GameSchResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/journals")
@Tag(name = "직관일지", description = "직관 일지 관련 API")
public class JournalController {

    private final JournalService journalService;
    private final JournalUsecase journalUsecase;

    //직관 일지 콘텐츠 업로드
    @Operation(
            summary = "직관 일지 작성 페이지 - 직관 일지 콘텐츠 업로드",
            description = """
    직관 일지 본문 데이터를 업로드하는 API입니다. 

    사용자는 사전에 Presigned URL 발급 API(`/s3/journal/presigned`)를 통해
    S3에 이미지를 직접 업로드한 뒤, 업로드 경로에 해당하는 `fileName`을 포함하여
    본 API를 호출해야 합니다.

    이 API는 전달받은 정보를 바탕으로 새로운 Journal 객체를 생성합니다.

    ✅ 필수 필드:
    - `gameId`: 경기 고유 ID (예: 20250622OBLG0)
    - `fileName`: 업로드한 이미지 파일명 (확장자 포함, ex. photo123.jpeg)
    - `ourScore`, `theirScore`: 점수 정보
    - `opponentTeamShortCode`, `stadiumShortCode`: 상대팀 및 경기장 숏코드
    - `gameDateTime`: 경기 일시 (`yyyy-MM-dd HH:mm` 형식)
    - `emotion`: 감정 태그 (감동, 짜릿함, 답답함, 아쉬움, 분노, 흡족 중 하나)
    - `review_text`: 후기 내용
    """
    )
    @ErrorApiResponses.Common
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "일지 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JourCreateResDto.class),
                            examples = @ExampleObject(
                                    name = "일지 생성",
                                    value = """
                                    {
                                      "code": "JOURNAL_CREATED",
                                      "message": "직관 일지가 등록되었습니다.",
                                      "data": {
                                        "journalId": 123
                                      }
                                    }
                                    """)
                    ))
    })
    @PostMapping("/contents")
    public ResponseEntity<SuccessResponse<JourCreateResDto>> createContents(
            @RequestParam Long memberId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = JourCreateReqDto.class))
            )
            @RequestBody JourCreateReqDto request)
    {
        JourCreateResDto resDto = journalUsecase.createJournal(memberId, request);

        return ResponseEntity.ok(
                SuccessResponse.success(SuccessCode.JOURNAL_CREATED, resDto)
        );
    }




    //본인 직관일지 목록 조회(캘린더)
    @Operation(
            summary = "본인 직관 일지 목록 페이지 - 캘린더",
            description = """
                JWT 토큰에서 유저 정보를 추출하여 본인의 직관 일지를 조회합니다.

                ✅ 선택적으로 `resultScore` 파라미터를 통해 경기 결과에 따른 필터링이 가능합니다.

                📌 필터링 예시:
                - `/journals/calendar?resultScore=승`
                - `/journals/calendar?resultScore=패`
                - `/journals/calendar?resultScore=무승부`

                🔁 필터링 가능한 값:
                - 승 (WIN)
                - 패 (LOSE)
                - 무승부 (DRAW)
                """
    )
    @ErrorApiResponses.Common
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JournalCalListResDto.class),
                    examples = {
                            @ExampleObject(name = "일지 목록", value = """
                                    {
                                      "code": "JOURNAL_LIST_FETCHED",
                                      "message": "직관 일지 리스트 조회 성공",
                                      "data": [
                                        {
                                          "journalId": 5,
                                          "ourScore": 3,
                                          "theirScore": 1,
                                          "resultScore": "승",
                                          "gameDate": "2025-06-03 18:30",
                                          "supportTeamSC": "OB",
                                          "opponentTeamSC": "SS",
                                          "stadiumSC": "JAM"
                                        }
                                      ]
                                    }
                                    """),
                            @ExampleObject(name = "일지 없음", value = """
                                            {
                                              "code": "JOURNAL_EMPTY",
                                              "message": "해당 조건에 해당하는 직관 일지가 없습니다.",
                                              "data": []
                                            }
                                            """)
                    }))
    @GetMapping("/calendar")
    public ResponseEntity<SuccessResponse<List<JournalCalListResDto>>> getCalendarJournals(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) ResultScore resultScore
    ) {
        List<JournalCalListResDto> result = journalUsecase.getJournalsByMemberCal(user.getMember().getId(), resultScore);

        SuccessCode code = result.isEmpty()
                ? SuccessCode.JOURNAL_EMPTY
                : SuccessCode.JOURNAL_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, result));
    }





    //본인 직관일지 목록 조회(모아보기)
    @Operation(
            summary = "본인 직관 일지 목록 페이지 - 모아보기",
            description = """
        로그인한 유저의 직관 일지를 목록 형식으로 조회합니다.

        📌 *무한 스크롤 방식 지원*  
        🔍 *`resultScore` 파라미터를 통해 경기 결과(WIN, LOSE, DRAW)로 필터링 가능*  
        🧭 *`page`, `size` 파라미터로 페이지네이션 처리 (기본: 1페이지당 10개)*  

        ✅ 예시 요청:
        - 전체 조회: `/journals/summary?page=0&size=10`
        - 승리 경기만: `/journals/summary?page=1&size=10&resultScore=WIN`
        """
    )
    @ErrorApiResponses.Common
    @ApiResponse(responseCode = "200", description = "일지 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JournalSumListResDto.class),
                    examples = {
                            @ExampleObject(name = "일지 목록 있음", value = """
{
  "code": "JOURNAL_LIST_FETCHED",
  "message": "직관 일지 리스트 조회 성공",
  "data": {
    "content": [
      {
        "journalId": 7,
        "media_url": "https://inninglog-bucket.s3.ap-northeast-2.amazonaws.com/journal/1/photo123.jpeg?X-Amz-Expires=600&X-Amz-Signature=...",
        "resultScore": "승",
        "emotion": "감동",
        "gameDate": "2025-06-03 18:30",
        "supportTeamSC": "OB",
        "opponentTeamSC": "SS",
        "stadiumSC": "JAM",
        "likeCount": 15,
        "commentCount": 8,
        "scrapCount": 3,
        "likedByMe": true,
        "scrapedByMe": false
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 6,
    "totalPages": 1,
    "last": true
  }
}
"""),
                            @ExampleObject(name = "일지 목록 없음", value = """
{
  "code": "JOURNAL_EMPTY",
  "message": "해당 조건에 해당하는 직관 일지가 없습니다.",
  "data": {
    "content": [],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 0,
    "totalPages": 0,
    "last": true
  }
}
""")
                    }))    @GetMapping("/summary")
    public ResponseEntity
            <SuccessResponse<SimplePageResponse<JournalSumListResDto>>> getMyJournalsSum(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "경기 결과 필터 (WIN, LOSE, DRAW)", example = "WIN")
            @RequestParam(required = false) ResultScore resultScore,

            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0",
                    schema = @Schema(type = "integer", minimum = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "페이지 크기 (한 페이지당 항목 수)",
                    example = "10",
                    schema = @Schema(type = "integer", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<JournalSumListResDto> result = journalUsecase.getJournalsByMemberSum(user.getMember().getId(), pageable, resultScore);

        SuccessCode code = result.isEmpty() ? SuccessCode.JOURNAL_EMPTY : SuccessCode.JOURNAL_LIST_FETCHED;

        SimplePageResponse<JournalSumListResDto> simplePage = SimplePageResponse.<JournalSumListResDto>builder()
                .content(result.getContent())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .isLast(result.isLast())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();

        return ResponseEntity.ok(SuccessResponse.success(code, simplePage));
    }


    @Operation(
            summary = "직관 일지 작성 페이지 - 직관 일지 콘텐츠 사전 정보 조회",
            description = """
    해당 경기 ID(gameId)를 기반으로, 현재 로그인한 사용자의 응원 팀과 상대 팀 정보를 조회합니다.
      
    - 이 API는 직관 일지 작성을 시작하기 전, 작성 페이지에 필요한 정보를 제공합니다.  
    - 반환되는 데이터는 사용자의 응원 팀, 상대 팀, 경기장 정보, 경기 일시 등을 포함합니다.  
    - 유저의 응원 팀은 미리 설정되어 있어야 하며, gameId는 유효한 경기여야 합니다.
    """
    )
    @ErrorApiResponses.Common
    @ApiResponse(
            responseCode = "200",
            description = "요청이 정상적으로 처리되었습니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JourGameResDto.class),
                    examples = @ExampleObject(
                            name = "직관 콘텐츠 사전 정보 응답 예시",
                            summary = "성공 응답",
                            value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": {
                                    "gameId": "20250625OBLG0",
                                    "gameDate": "2025-06-03 18:30",
                                    "supportTeamSC": "LG",
                                    "opponentTeamSC": "OB",
                                    "stadiumSC": "JAM"
                                  }
                                }
                                """
                    )
            )
    )    @GetMapping("/contents")
    public ResponseEntity<SuccessResponse<JourGameResDto>> getGameInfo(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "경기 Id (gameId)", required = true)
            @RequestParam String gameId
    ){
        JourGameResDto resDto = journalUsecase.infoPreJournal(user.getMember().getId(), gameId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resDto));
    }


    //특정 날짜 경기 일정 조회 - 유저의 응원팀 기준
    @Operation(
            summary = "본인 직관 일지 캘린더 페이지 - 유저 응원팀의 특정 날짜 경기 일정 조회[팝업 형태]",
            description = """
            로그인한 유저의 **응원팀 기준으로**, 특정 날짜의 경기 일정을 조회합니다.  
            
            반환된 `gameId`는 이후 **직관 일지 콘텐츠 업로드 API (`/journals/contents`)**에 사용됩니다.

            🗓️ 요청 날짜는 `YYYY-MM-DD` 형식으로 전달해야 합니다.

            ✅ 예시:
            `/journals/schedule?gameDate=2025-07-01`
        """
    )
    @ErrorApiResponses.Common
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경기 일정 조회 성공 (또는 해당일에 경기 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameSchResDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "경기 있음 예시",
                                            value = """
                    {
                      "code": "SUCCESS",
                      "status": 200,
                      "message": "요청이 정상적으로 처리되었습니다.",
                      "data": {
                        "gameId": "20250701OBLT0",
                        "gameDate": "2025-06-03 18:30",
                        "supportTeamSC": "OB",
                        "opponentSC": "LT",
                        "stadiumSC": "JAM"
                      }
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "경기 없음 예시",
                                            value = """
                    {
                      "code": "SUCCESS",
                      "status": 200,
                      "message": "요청이 정상적으로 처리되었습니다.",
                      "data": null
                    }
                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/schedule")
    public ResponseEntity<SuccessResponse<GameSchResDto>> getSchedule(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "경기 일정 날짜 (예: 2025-07-01)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate gameDate
    ){
        GameSchResDto resDto = journalUsecase.getSingleGameSch(user.getMember().getId(), gameDate);

        if (resDto == null) {
            return ResponseEntity.ok(SuccessResponse.success(SuccessCode.NO_SCHEDULE_ON_DATE, null));
        }
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resDto));
    }



    @Operation(
            summary = "특정 직관 일지 조회",
            description = "journalId는 직관일지 목록 API(/summary, /schedule)를 통해 확인된 값을 전달해야 합니다. seatViewId는 시야 정보가 연결된 경우에만 포함됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "직관일지 상세 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JourDetailResDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "시야 정보 미포함",
                                                    summary = "seatView가 연결되지 않은 경우",
                                                    value = """
                        {
                          "code": "SUCCESS",
                          "message": "요청이 정상적으로 처리되었습니다.",
                          "data": {
                            "jourDetail": {
                              "journalId": 4,
                              "gameDate": "2025-06-03 18:30",
                              "supportTeamSC": "OB",
                              "opponentTeamSC": "OB",
                              "ourScore": 2,
                              "theirScore": 10,
                              "stadiumSC": "JAM",
                              "emotion": "감동",
                              "media_url": "",
                              "review_text": ""
                            },
                            "seatViewId": null
                          }
                        }
                        """
                                            ),
                                            @ExampleObject(
                                                    name = "seatView 연결됨",
                                                    summary = "시야 정보가 연결된 경우",
                                                    value = """
                        {
                          "code": "SUCCESS",
                          "message": "요청이 정상적으로 처리되었습니다.",
                          "data": {
                            "jourDetail": {
                              "journalId": 3,
                              "gameDate": "2025-06-03 18:30",
                              "supportTeamSC": "OB",
                              "opponentTeamSC": "OB",
                              "ourScore": 3,
                              "theirScore": 1,
                              "stadiumSC": "JAM",
                              "emotion": "감동",
                              "media_url": "https://s3.amazonaws.com/.../image.jpg",
                              "review_text": "오늘 정말 재미있었다!"
                            },
                            "seatViewId": 3
                          }
                        }
                        """
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping("/detail/{journalId}")
    public ResponseEntity<SuccessResponse<JourUpdateResDto>> getDetailJournal(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "직관 일지 ID. 목록 API에서 선택한 항목의 ID를 전달", required = true)
            @PathVariable Long journalId
    ) {
        JourUpdateResDto resDto = journalUsecase.getDetailJournal(user.getMember().getId(), journalId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resDto));

    }



    @Operation(
            summary = "특정 직관 일지 수정",
            description = """
        기존에 작성된 직관 일지 내용을 수정합니다. 본인만 수정할 수 있으며, 
        수정 시 감정 태그, 리뷰, 점수, 이미지 링크 등을 포함할 수 있습니다.
        seatView는 별도 API로 연결되며 본 API에서 수정되지 않습니다.
    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "직관일지 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JourUpdateResDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "수정 완료 예시",
                                                    summary = "수정 완료된 직관일지와 seatViewId 반환",
                                                    value = """
                        {
                          "code": "SUCCESS",
                          "message": "요청이 정상적으로 처리되었습니다.",
                          "data": {
                            "jourDetail": {
                              "journalId": 3,
                              "gameDate": "2025-06-03 18:30",
                              "supportTeamSC": "OB",
                              "opponentTeamSC": "OB",
                              "stadiumSC": "JAM",
                              "emotion": "감동",
                              "media_url": "https://s3.amazonaws.com/.../image.jpg",
                              "review_text": "후기를 수정했어요!"
                            },
                            "seatViewId": 3
                          }
                        }
                        """
                                            )
                                    }
                            )
                    )
            }
    )
    @PatchMapping("/update/{journalId}")
    @ErrorApiResponses.Common
    public ResponseEntity<SuccessResponse<JourUpdateResDto>> updateJournal(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long journalId,
            @RequestBody JourUpdateReqDto dto
    ) {
        JourUpdateResDto updatedJournal = journalUsecase.updateJournal(user.getMember().getId(), journalId, dto);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, updatedJournal));
    }


    @Operation(
            summary = "공개 직관 일지 피드 조회",
            description = """
                공개 설정된 직관 일지를 조회합니다. 인증이 필요합니다.

                📌 **팀 필터링**
                - `teamShortCode=ALL`: 전체 공개 일지 조회
                - `teamShortCode=LG`: 특정 팀(작성자 응원팀 기준) 일지만 조회

                📌 **페이지네이션**
                - 무한 스크롤 방식 (Slice 기반)
                - `page`, `size` 파라미터로 제어
                - 최신순(createdAt DESC)으로 정렬

                📌 **응답 필드**
                - `writedByMe`: 내가 작성한 일지인지 여부
                - `likedByMe`: 내가 좋아요 눌렀는지 여부
                - `scrapedByMe`: 내가 스크랩했는지 여부

                ✅ 예시 요청:
                - 전체 조회: `/journals/feed?teamShortCode=ALL&page=0&size=10`
                - LG팬 일지만: `/journals/feed?teamShortCode=LG&page=0&size=10`
                """
    )
    @ErrorApiResponses.Common
    @ApiResponse(
            responseCode = "200",
            description = "피드 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SliceResponse.class),
                    examples = {
                            @ExampleObject(name = "피드 목록", value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": {
                                    "content": [
                                      {
                                        "journalId": 123,
                                        "thumbnailUrl": "https://s3.amazonaws.com/.../image.jpg",
                                        "member": {
                                          "nickName": "볼빨간스트라스버그",
                                          "profile_url": "https://k.kakaocdn.net/.../img.jpg"
                                        },
                                        "writedByMe": false,
                                        "reviewPreview": "오늘 경기 정말 재밌었다! 우리 팀이 역전승...",
                                        "createdAt": "2025-06-03 18:30",
                                        "likeCount": 15,
                                        "likedByMe": true,
                                        "commentCount": 3,
                                        "scrapCount": 2,
                                        "scrapedByMe": false
                                      }
                                    ],
                                    "hasNext": true,
                                    "page": 0,
                                    "size": 10
                                  }
                                }
                                """),
                            @ExampleObject(name = "피드 없음", value = """
                                {
                                  "code": "SUCCESS",
                                  "message": "요청이 정상적으로 처리되었습니다.",
                                  "data": {
                                    "content": [],
                                    "hasNext": false,
                                    "page": 0,
                                    "size": 10
                                  }
                                }
                                """)
                    }
            )
    )
    @GetMapping("/feed")
    public ResponseEntity<SuccessResponse<SliceResponse<JournalFeedResDto>>> getPublicJournalFeed(
            @Parameter(description = "팀 숏코드 (ALL: 전체 조회, 특정 팀코드: 해당 팀 응원 사용자 일지만)", example = "ALL")
            @RequestParam String teamShortCode,

            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0",
                    schema = @Schema(type = "integer", minimum = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "페이지 크기",
                    example = "10",
                    schema = @Schema(type = "integer", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10") int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<JournalFeedResDto> result = journalUsecase.getPublicJournalFeed(user.getMemberId(), teamShortCode, pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }

    @Operation(
            summary = "내가 쓴 직관 일지 목록 조회",
            description = """
                내가 작성한 직관 일지 목록을 조회합니다.

                ✔ 최신순(date DESC)으로 정렬되어 반환됩니다.
                ✔ page는 0부터 시작합니다. (0=첫 페이지)
                ✔ size는 한 페이지에서 가져올 일지 수를 의미합니다.
                ✔ hasNext가 true이면 다음 페이지 요청이 가능합니다.
                """,
            tags = {"마이페이지"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내가 쓴 직관 일지 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SliceResponse.class),
                            examples = {
                                    @ExampleObject(name = "내가 쓴 직관 일지 목록", value = """
                                        {
                                          "code": "SUCCESS",
                                          "message": "요청이 정상적으로 처리되었습니다.",
                                          "data": {
                                            "content": [
                                              {
                                                "journalId": 7,
                                                "media_url": "https://inninglog-bucket.s3.ap-northeast-2.amazonaws.com/journal/1/photo123.jpeg?X-Amz-Expires=600",
                                                "resultScore": "WIN",
                                                "emotion": "감동",
                                                "gameDate": "2025-06-03 18:30",
                                                "supportTeamSC": "OB",
                                                "opponentTeamSC": "SS",
                                                "stadiumSC": "JAM",
                                                "likeCount": 15,
                                                "commentCount": 8,
                                                "scrapCount": 3,
                                                "likedByMe": true,
                                                "scrapedByMe": false
                                              }
                                            ],
                                            "hasNext": true,
                                            "page": 0,
                                            "size": 10
                                          }
                                        }
                                        """)
                            }
                    )
            )
    })
    @GetMapping("/my")
    public ResponseEntity<SuccessResponse<SliceResponse<JournalSumListResDto>>> getMyJournals(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 일지 개수", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        SliceResponse<JournalSumListResDto> result = journalUsecase.getMyJournals(user.getMember(), pageable);

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, result));
    }
}

