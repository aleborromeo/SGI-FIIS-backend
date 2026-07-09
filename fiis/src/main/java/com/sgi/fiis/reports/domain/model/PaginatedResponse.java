package com.sgi.fiis.reports.domain.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable paginated response wrapper for all report endpoints.
 * The data list is stored as an unmodifiable copy to prevent external modification.
 *
 * @param <T> type of data element
 */
public class PaginatedResponse<T> {

    private final List<T> data;
    private final long    total;
    private final int     page;
    private final int     size;

    public PaginatedResponse(List<T> data, long total, int page, int size) {
        this.data  = Collections.unmodifiableList(new ArrayList<>(data != null ? data : List.of()));
        this.total = total;
        this.page  = page;
        this.size  = size;
    }

    public List<T> getData()  { return data; }
    public long    getTotal() { return total; }
    public int     getPage()  { return page; }
    public int     getSize()  { return size; }
}
