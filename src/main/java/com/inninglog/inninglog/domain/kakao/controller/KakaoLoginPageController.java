package com.inninglog.inninglog.domain.kakao.controller;

import com.inninglog.inninglog.domain.kakao.dto.KakaoLoginPageResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = "인증", description = "로그인/회원가입 관련 API")
public class KakaoLoginPageController {

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @Operation(
            summary = "카카오 로그인 페이지 요청",
            description = "프론트에서 카카오 로그인 페이지로 리다이렉트하기 위한 URL을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 로그인 URL 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = KakaoLoginPageResDTO.class)
                    )
            )
    })
    @GetMapping("/page")
    public ResponseEntity<?> loginPage() {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + client_id + "&redirect_uri=" + redirect_uri;

        return ResponseEntity.ok().body(new KakaoLoginPageResDTO(location));
    }
}
