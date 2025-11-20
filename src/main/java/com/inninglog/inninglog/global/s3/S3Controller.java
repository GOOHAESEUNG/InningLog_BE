package com.inninglog.inninglog.global.s3;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "사진 업로드", description = "S3 Presigned URL 관련 API")
public class S3Controller {

    private final PreSginedPutService preSginedPutService;

    @Operation(
            summary = "직관 일지용 Presigned URL 발급",
            description = "프론트에서 직접 S3에 이미지를 업로드할 수 있도록, 일정 시간 동안 유효한 Presigned URL을 발급합니다.",
            parameters = {
                    @Parameter(name = "fileName", description = "업로드할 파일명 (확장자 포함)", example = "image123.jpeg"),
                    @Parameter(name = "contentType", description = "파일 MIME 타입", example = "image/jpeg")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Presigned URL 발급 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 예시",
                                            value = """
                                            {
                                              "code": "OK",
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "data": "https://inninglog.s3.ap-northeast-2.amazonaws.com/journal/1/image123.jpeg?X-Amz-Algorithm=..."
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/journal/presigned")
    public ResponseEntity<SuccessResponse<String>> generateJourPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType,
            @AuthenticationPrincipal CustomUserDetails user
            ) {
        String url = preSginedPutService.journalPutPreUrl(user.getMember().getId(), fileName, contentType);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, url));
    }

    @Operation(
            summary = "좌석 시야용 Presigned URL 발급",
            description = "좌석 시야 이미지를 업로드하기 위한 Presigned URL을 반환합니다.",
            parameters = {
                    @Parameter(name = "fileName", description = "업로드할 파일명 (확장자 포함)", example = "seatview001.png"),
                    @Parameter(name = "contentType", description = "파일 MIME 타입", example = "image/png")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Presigned URL 발급 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 예시",
                                            value = """
                                            {
                                              "code": "OK",
                                              "message": "요청이 성공적으로 처리되었습니다.",
                                              "data": "https://inninglog.s3.ap-northeast-2.amazonaws.com/seatView/1/seatview001.png?X-Amz-Algorithm=..."
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/seatView/presigned")
    public ResponseEntity<SuccessResponse<String>> generateSeatPresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        String url = preSginedPutService.seatViewPutPreUrl(user.getMember().getId(), fileName, contentType);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, url));
    }
}