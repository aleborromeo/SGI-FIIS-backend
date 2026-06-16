package com.sgi.fiis.reportes.domain.model;

import java.util.List;

/**
 * Wrapper de respuesta paginada reutilizable para todos los endpoints de reportes.
 *
 * @param <T> tipo del elemento de datos
 */
public class PaginatedResponse<T> {

    private List<T> data;
    private long    total;
    private int     page;
    private int     size;

    public PaginatedResponse(List<T> data, long total, int page, int size) {
        this.data  = data;
        this.total = total;
        this.page  = page;
        this.size  = size;
    }

    public List<T> getData()  { return data; }
    public long    getTotal() { return total; }
    public int     getPage()  { return page; }
    public int     getSize()  { return size; }
}
