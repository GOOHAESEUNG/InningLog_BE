package com.inninglog.inninglog.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Schema(description = "Slice 기반 목록 응답")
public record SliceResponse<T>(
        @Schema(description = "조회된 데이터 목록")
        List<T> content,

        @Schema(description = "다음 페이지가 존재하는지 여부", example = "true")
        boolean hasNext,

        @Schema(description = "현재 페이지 번호", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size
) {
    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.hasNext(),
                slice.getNumber(),
                slice.getSize()
        );
    }

    public static <T> SliceResponse<T> of(List<T> content, boolean hasNext, Pageable pageable) {
        return new SliceResponse<>(
                content,
                hasNext,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    public static <T> SliceResponse<T> empty(Pageable pageable) {
        return new SliceResponse<>(
                Collections.emptyList(),
                false,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }
}
