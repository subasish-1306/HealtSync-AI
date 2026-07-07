package com.healthsync.common;

import java.util.List;

/**
 * Standard pagination details holder for database list queries.
 */
public record PaginationResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {}
