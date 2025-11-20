package com.inninglog.inninglog.domain.comment.controller;

import com.inninglog.inninglog.domain.comment.domain.Comment;
import com.inninglog.inninglog.domain.comment.service.CommentDeleteService;
import com.inninglog.inninglog.domain.comment.service.CommentUsecase;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "댓글", description = "댓글 관련 API")
public class CommentDeleteController {

    private final CommentUsecase commentUsecase;

    @Operation(
            summary = "댓글 삭제",
            description = """
                특정 댓글을 소프트 삭제합니다.
                이미 삭제된 댓글이거나 존재하지 않는 댓글 ID가 전달될 경우 에러가 발생합니다.
                댓글 삭제는 실제 DB 삭제가 아닌, `isDeleted = true` 처리(soft delete) 방식으로 동작합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음 또는 이미 삭제된 댓글"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<SuccessResponse<Void>> deleteComments(
            @Parameter(description = "삭제할 댓글의 ID", example = "12")
            @PathVariable("commentId") long commentId
    ){
        commentUsecase.deleteComment(commentId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK));
    }
}
