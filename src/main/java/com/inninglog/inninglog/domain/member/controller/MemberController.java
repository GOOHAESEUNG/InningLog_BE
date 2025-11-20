package com.inninglog.inninglog.domain.member.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.domain.member.dto.req.MemberSetupReqDto;
import com.inninglog.inninglog.domain.member.service.MemberService;
import com.inninglog.inninglog.domain.member.dto.req.NicknameReqDto;
import com.inninglog.inninglog.domain.member.dto.req.TypeReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    // 닉네임 수정
    @Operation(
            summary = "닉네임 수정",
            description = "JWT 토큰에서 인증된 회원 정보를 추출하여, 사용자의 닉네임을 수정합니다.\n\n" +
                    "요청 바디에는 새로운 닉네임이 포함되어야 하며, 중복된 닉네임일 경우 오류가 반환됩니다.\n"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "닉네임 수정", value = """
                                            {
                                              "code": "NICKNAME_UPDATED",
                                              "message": "닉네임이 성공적으로 수정되었습니다.",
                                              "data": null
                                            }
                                            """)
                            }))
    })
    @ErrorApiResponses.Nickname
    @PatchMapping("/nickname")
    public ResponseEntity<SuccessResponse<Void>> updateNickname(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody NicknameReqDto request
    ) {
        memberService.updateNickname(user.getMember().getId(), request.getNickname());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.NICKNAME_UPDATED));
    }

    // 회원 응원팀 설정
    @Operation(
            summary = "회원의 응원 팀 설정",
            description = """
        로그인한 회원의 응원 팀을 설정합니다.  
        요청 시 아래의 **구단 식별자(shortCode)** 중 하나를 전달해야 합니다.

        - LG 트윈스: `LG`
        - 두산 베어스: `OB`
        - SSG 랜더스: `SK`
        - 한화 이글스: `HH`
        - 삼성 라이온즈: `SS`
        - KT 위즈: `KT`
        - 롯데 자이언츠: `LT`
        - KIA 타이거즈: `HT`
        - NC 다이노스: `NC`
        - 키움 히어로즈: `WO`

        ⚠️ **주의:** 이미 응원 팀이 설정된 회원은 변경할 수 없습니다.  
        이 경우 `400 Bad Request` 에러가 반환됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "응원 팀 설정", value = """
                                            {
                                              "code": "TEAM_SET",
                                              "message": "응원 팀이 성공적으로 설정되었습니다.",
                                              "data": null
                                            }
                                            """)
                            }))
    })
    @ErrorApiResponses.Common
    @ErrorApiResponses.TeamSetting
    @PatchMapping("/setup")
    public ResponseEntity<SuccessResponse<Void>> updateType(
            @AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저가 응원하는 팀 설정",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TypeReqDto.class)))
            @RequestBody TypeReqDto request
    ) {
        memberService.updateMemberType(user.getMember().getId(), request.getTeamShortCode());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.TEAM_SET));
    }

    @Operation(
            summary = "회원 초기 설정 (닉네임 + 응원팀)",
            description = """
        회원 가입 후 최초 1회에 한해 닉네임과 응원팀을 한 번에 설정합니다.

        ⚠️ 응원팀은 한 번 설정하면 변경할 수 없습니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 설정 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "회원 설정", value = """
                                        {
                                          "code": "MEMBER_SETUP_SUCCESS",
                                          "message": "회원 정보 설정이 완료되었습니다.",
                                          "data": null
                                        }
                                        """)
                            }))
    })
    @ErrorApiResponses.Common
    @ErrorApiResponses.Nickname
    @ErrorApiResponses.TeamSetting
    @PostMapping("/setup")
    public ResponseEntity<SuccessResponse<Void>> setupMemberInfo(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody MemberSetupReqDto request
    ) {
        memberService.setupMemberInfo(user.getMember().getId(), request.getNickname(), request.getTeamShortCode());
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}