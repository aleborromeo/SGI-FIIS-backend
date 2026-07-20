package com.sgi.fiis.shared.application.dto;

import java.util.Collections;
import java.util.List;

public class PageDto<T> {

    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int page;
    private final int size;

    public PageDto(List<T> content, long totalElements, int page, int size) {
        this.content = Collections.unmodifiableList(content != null ? content : List.of());
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.page = page;
        this.size = size;
    }

    public List<T> getContent() { return content; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}
