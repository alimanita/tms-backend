package com.transport.tms.util;

import com.transport.tms.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PageMapper {

    private PageMapper() {
    }

    public static <T, R> PageResponse<R> map(Page<T> page, Function<T, R> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
