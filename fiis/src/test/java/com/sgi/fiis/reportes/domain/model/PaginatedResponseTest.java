package com.sgi.fiis.reportes.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias para {@link PaginatedResponse}.
 * Verifica que la copia defensiva de la lista previene modificaciones externas.
 */
@DisplayName("PaginatedResponse - Pruebas unitarias")
class PaginatedResponseTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor y getters básicos
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Constructor debe asignar todos los campos correctamente")
    void constructor_debeAsignarCamposCorrectamente() {
        List<String> items = List.of("a", "b", "c");

        PaginatedResponse<String> respuesta = new PaginatedResponse<>(items, 100L, 2, 10);

        assertThat(respuesta.getData()).containsExactly("a", "b", "c");
        assertThat(respuesta.getTotal()).isEqualTo(100L);
        assertThat(respuesta.getPage()).isEqualTo(2);
        assertThat(respuesta.getSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("getData debe retornar lista vacía cuando se pasa null")
    void constructor_conNull_debeRetornarListaVacia() {
        PaginatedResponse<String> respuesta = new PaginatedResponse<>(null, 0L, 0, 20);

        assertThat(respuesta.getData()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("getData debe retornar lista vacía cuando se pasa lista vacía")
    void constructor_conListaVacia_debeRetornarListaVacia() {
        PaginatedResponse<String> respuesta = new PaginatedResponse<>(List.of(), 0L, 0, 20);

        assertThat(respuesta.getData()).isNotNull().isEmpty();
        assertThat(respuesta.getTotal()).isZero();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Copia defensiva — BUG RISK fix (DeepSource)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Modificar la lista original no debe afectar a getData (copia defensiva)")
    void getData_modificarListaOriginal_noDebeAfectarInternamente() {
        List<String> lista = new ArrayList<>();
        lista.add("item1");

        PaginatedResponse<String> respuesta = new PaginatedResponse<>(lista, 1L, 0, 20);

        // modificar la lista original después de crear la respuesta
        lista.add("item2");
        lista.add("item3");

        // la respuesta interna no debe verse afectada
        assertThat(respuesta.getData()).hasSize(1).containsExactly("item1");
    }

    @Test
    @DisplayName("getData debe retornar lista inmutable (no debe permitir añadir elementos)")
    void getData_debeSerInmutable() {
        PaginatedResponse<String> respuesta =
                new PaginatedResponse<>(List.of("a"), 1L, 0, 20);

        // S5778: una sola invocación que puede lanzar la excepción
        List<String> data = respuesta.getData();
        assertThatThrownBy(() -> data.add("b"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("getData debe retornar lista inmutable (no debe permitir eliminar elementos)")
    void getData_debeSerInmutableAlEliminar() {
        PaginatedResponse<String> respuesta =
                new PaginatedResponse<>(new ArrayList<>(List.of("a", "b")), 2L, 0, 20);

        // S5778: una sola invocación que puede lanzar la excepción
        List<String> data = respuesta.getData();
        assertThatThrownBy(() -> data.remove("a"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Casos de borde: page y size
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Debe preservar page=0 y total=0 correctamente")
    void constructor_conPaginaCeroYTotalCero_debeAsignarse() {
        PaginatedResponse<Integer> respuesta = new PaginatedResponse<>(List.of(), 0L, 0, 20);

        assertThat(respuesta.getPage()).isZero();
        assertThat(respuesta.getTotal()).isZero();
    }

    @Test
    @DisplayName("Debe preservar valores grandes de total correctamente")
    void constructor_conTotalGrande_debeAsignarse() {
        PaginatedResponse<Integer> respuesta =
                new PaginatedResponse<>(List.of(), 999_999L, 49, 20);

        assertThat(respuesta.getTotal()).isEqualTo(999_999L);
        assertThat(respuesta.getPage()).isEqualTo(49);
    }
}
