package com.sgi.fiis.reportes.domain.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper de respuesta paginada reutilizable para todos los endpoints de reportes.
 * La lista de datos se almacena como copia inmutable para evitar modificaciones externas.
 *
 * @param <T> tipo del elemento de datos
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
