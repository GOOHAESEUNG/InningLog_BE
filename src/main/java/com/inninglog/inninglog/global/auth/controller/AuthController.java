package com.inninglog.inninglog.global.auth.controller;

import com.inninglog.inninglog.global.auth.dto.LoginRequest;
import com.inninglog.inninglog.global.auth.dto.LoginResponse;
import com.inninglog.inninglog.global.auth.dto.SignupRequest;
import com.inninglog.inninglog.global.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "로그인/회원가입 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "일반 회원가입",
            description = """
            일반 사용자의 회원가입을 처리합니다.  
            - 아이디(userID)는 중복되면 안 됩니다.  
            - 아이디는 영문 + 숫자 조합의 6~12자리여야 합니다.  
            - 비밀번호는 숫자 4자리여야 합니다.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 아이디입니다. (EXIST_USERID)"),
            @ApiResponse(responseCode = "400", description = "비밀번호 형식이 올바르지 않습니다. (INVALID_PASSWORD_FORMAT)")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest dto) {
        authService.signup(dto.getUserID(), dto.getPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "일반 로그인",
            description = """
                    일반 사용자 로그인 처리 및 JWT 발급  
                    - 성공 시 JWT 토큰이 반환됩니다.  
                    - 아이디 또는 비밀번호가 틀리면 오류가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 / JWT 토큰 반환"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 아이디입니다. (USER_NOT_FOUND)"),
            @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다. (INVALID_PASSWORD)")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest dto) {
        return ResponseEntity.ok(authService.resdto(dto));
    }

    @Operation(
            summary = "아이디 중복 체크",
            description = "회원가입 시 입력한 userID가 이미 존재하는지 확인합니다."
    )
    @ApiResponse(responseCode = "200", description = "중복 여부 반환 (true = 중복 / false = 사용 가능)")
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkDuplicateID(
            @Parameter(description = "중복 확인할 사용자 ID", example = "dodo123")
            @RequestParam String userID
    ) {
        boolean exists = authService.isUserIdDuplicated(userID);
        return ResponseEntity.ok(exists);
    }
}