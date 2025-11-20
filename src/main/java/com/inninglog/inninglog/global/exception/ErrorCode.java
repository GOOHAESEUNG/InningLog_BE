package com.inninglog.inninglog.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    METHOD_ARGUMENT_NOT_VALID("USER_NOT_FOUND",HttpStatus.BAD_REQUEST, "잘못된 [인자]입니다."),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND",HttpStatus.NOT_FOUND, "요청한 [RESOURCE, URL]를 찾을 수 없습니다."),
    PARAMETER_NOT_FOUND("PARAMETER_NOT_FOUND",HttpStatus.BAD_REQUEST, "요청에 [Parameter]가 존재하지 않습니다."),

    // 인증 / 회원 관련
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    INVALID_NICKNAME("INVALID_NICKNAME", HttpStatus.BAD_REQUEST, "닉네임 형식이 올바르지 않습니다."),
    ALREADY_SET("ALREADY_SET", HttpStatus.CONFLICT, "이미 팀이 설정되었습니다."),
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS",HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    EXIST_USERID("EXIST_USERID",HttpStatus.ALREADY_REPORTED,"이미 존재하는 아이디 입니다."),
    INVALID_PASSWORD_FORMAT("INVALID_PASSWORD_FORMAT", HttpStatus.BAD_REQUEST, "비밀번호 형식이 맞지 않습니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다."),
    INVALID_USERID_FORMAT("INVALID_USERID_FORMAT", HttpStatus.BAD_REQUEST, "아이디 형식이 맞지 않습니다."),

    // 팀 / 구장 / 경기 관련
    TEAM_NOT_FOUND("TEAM_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 팀입니다."),
    STADIUM_NOT_FOUND("STADIUM_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 경기장입니다."),
    GAME_NOT_FOUND("GAME_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 게임입니다."),
    NO_VISITED_GAMES("NO_VISITED_GAME", HttpStatus.BAD_REQUEST, "직관한 경기가 없습니다."),

    // 일지 / 시야 / 태그 관련
    INVALID_FILE_NAME("INVALID_FILE_NAME", HttpStatus.BAD_REQUEST, "파일 이름은 슬래시(/) 또는 경로 문자(../)를 포함할 수 없습니다."),
    S3_UPLOAD_FAILED("S3_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 중 오류가 발생했습니다."),
    FILE_IS_EMPTY("FILE_IS_EMPTY", HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다."),
    JOURNAL_NOT_FOUND("JOURNAL_NOT_FOUND", HttpStatus.NOT_FOUND, "작성하지 않은 일지입니다."),
    EMOTION_TAG_NOT_FOUND("EMOTION_TAG_NOT_FOUND", HttpStatus.NOT_FOUND, "감정 태그가 없습니다."),
    ZONE_NOT_FOUND("ZONE_NOT_FOUND", HttpStatus.NOT_FOUND, "등록되지 않은 존입니다."),
    SEATVIEW_NOT_FOUND("SEATVIEW_NOT_FOUND",HttpStatus.NOT_FOUND,"작성하지 않은 좌석 시야 후기입니다."),
    SEATVIEW_ALREADY_EXISTS("SEATVIEW_ALREADY_EXISTS", HttpStatus.BAD_REQUEST, "이미 좌석 시야 글이 작성된 직관 일지입니다."),
    INVALID_SEAT_SEARCH("INVALID_SEAT_SEARCH", HttpStatus.BAD_REQUEST,"존 정보 없이 열만으로는 검색할 수 없습니다."),
    INVALID_HASHTAG_REQUEST("INVALID_HASHTAG_REQUEST",HttpStatus.BAD_REQUEST, "해시태그는 최소 1개, 최대 5개까지 선택할 수 있습니다."),

    //게시글 관련
    POST_NOT_FOUND("POST_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),

    //댓글 관련
    ROOT_COMMENT_NOT_FOUND("ROOT_COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 상위 댓글입니다."),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

    //좋아요 관련
    LIKE_ALREADY_EXISTS("LIKE_ALREADY_EXISTS", HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 컨텐츠입니다."),
    LIKE_NOT_FOUND("LIKE_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 좋아요 입니다."),

    //스크랩 관련
    SCRAP_ALREADY_EXISTS("SCRAP_ALREADY_EXISTS", HttpStatus.BAD_REQUEST, "이미 스크랩한 컨텐츠입니다."),
    SCRAP_NOT_FOUND("SCRAP_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 스크랩 입니다."),

    // 공통 예외
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "요청값이 올바르지 않습니다."),
    TYPE_MISMATCH("TYPE_MISMATCH", HttpStatus.BAD_REQUEST, "요청 파라미터 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}