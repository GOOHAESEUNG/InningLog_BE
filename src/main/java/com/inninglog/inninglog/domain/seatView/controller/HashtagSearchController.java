package com.inninglog.inninglog.domain.seatView.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.pageable.SimplePageResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatViewImageResult;
import com.inninglog.inninglog.domain.seatView.service.HashtagSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.util.List;

@RestController
@RequestMapping("/seatViews/hashtag")
@RequiredArgsConstructor
@Tag(name = "좌석시야", description = "좌석 시야 후기 관련 API")
public class HashtagSearchController {

    private final HashtagSearchService hashtagSearchService;

    @Operation(
            summary = "해시태그 기반 좌석 검색 (모아보기)",
            description = """
                    선택한 감정 태그를 기준으로 좌석 시야 후기를 검색합니다.  
                    최대 **5개까지 태그 선택**이 가능하며, **선택한 모든 태그를 포함한 좌석만 조회**됩니다.  
                    해당 API는 **모아보기(사진만 제공)** 형태로 결과를 반환합니다.
                    
                    ※ 결과는 **최신순으로 정렬**됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해시태그 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
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
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (해시태그 개수 초과 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "에러 응답 예시",
                                    value = """
                                            {
                                              "code": "BAD_REQUEST",
                                              "message": "해시태그는 최소 1개, 최대 5개까지 선택할 수 있습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @ErrorApiResponses.Common
    @GetMapping("/gallery")
    public ResponseEntity<SuccessResponse<SimplePageResponse<SeatViewImageResult>>> searchSeatViewsGallery(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "구장 단축코드", required = true, example = "JAM")
            @RequestParam String stadiumShortCode,

            @Parameter(description = "해시태그 코드 목록 (최대 5개 선택 가능)", required = true)
            @RequestParam List<String> hashtagCodes,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (한 페이지당 항목 수)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SeatViewImageResult> resultPage = hashtagSearchService.searchSeatViewsByHashtagsGallery(
                user.getMember().getId(), stadiumShortCode, hashtagCodes, pageable
        );

        SuccessCode code = resultPage.isEmpty() ? SuccessCode.SEATVIEW_EMPTY : SuccessCode.SEATVIEW_LIST_FETCHED;

        SimplePageResponse<SeatViewImageResult> simplePage = SimplePageResponse.<SeatViewImageResult>builder()
                .content(resultPage.getContent())
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .isLast(resultPage.isLast())
                .build();

        return ResponseEntity.ok(SuccessResponse.success(code, simplePage));
    }
}