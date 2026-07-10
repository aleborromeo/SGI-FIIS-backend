package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link PaginatedResponse}.
 * Verifies that the defensive copy of the list prevents external modification.
 */
@DisplayName("PaginatedResponse - Unit Tests")
class PaginatedResponseTest {

    // -------------------------------------------------------------------------
    // Constructor and Basic Getters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Constructor should assign all fields correctly")
    void constructor_assignsFieldsCorrectly() {
        List<String> items = List.of("a", "b", "c");

        PaginatedResponse<String> response = new PaginatedResponse<>(items, 100L, 2, 10);

        assertThat(response.getData()).containsExactly("a", "b", "c");
        assertThat(response.getTotal()).isEqualTo(100L);
        assertThat(response.getPage()).isEqualTo(2);
        assertThat(response.getSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("getData should return empty list when null is passed")
    void constructor_withNull_returnsEmptyList() {
        PaginatedResponse<String> response = new PaginatedResponse<>(null, 0L, 0, 20);

        assertThat(response.getData()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("getData should return empty list when empty list is passed")
    void constructor_withEmptyList_returnsEmptyList() {
        PaginatedResponse<String> response = new PaginatedResponse<>(List.of(), 0L, 0, 20);

        assertThat(response.getData()).isNotNull().isEmpty();
        assertThat(response.getTotal()).isZero();
    }

    // -------------------------------------------------------------------------
    // Defensive Copy Verification
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Modifying original list should not affect getData (defensive copy)")
    void getData_modifyingOriginalList_doesNotAffectInternally() {
        List<String> list = new ArrayList<>();
        list.add("item1");

        PaginatedResponse<String> response = new PaginatedResponse<>(list, 1L, 0, 20);

        // modify original list after creating response
        list.add("item2");
        list.add("item3");

        // internal response should not be affected
        assertThat(response.getData()).hasSize(1).containsExactly("item1");
    }

    @Test
    @DisplayName("getData should return unmodifiable list (should not allow adding items)")
    void getData_shouldBeUnmodifiable() {
        PaginatedResponse<String> response =
                new PaginatedResponse<>(List.of("a"), 1L, 0, 20);

        List<String> data = response.getData();
        assertThatThrownBy(() -> data.add("b"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("getData should return unmodifiable list (should not allow removing items)")
    void getData_shouldBeUnmodifiableOnRemove() {
        PaginatedResponse<String> response =
                new PaginatedResponse<>(new ArrayList<>(List.of("a", "b")), 2L, 0, 20);

        List<String> data = response.getData();
        assertThatThrownBy(() -> data.remove("a"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // -------------------------------------------------------------------------
    // Edge Cases: page and size
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should preserve page=0 and total=0 correctly")
    void constructor_withPageZeroAndTotalZero_assignsCorrectly() {
        PaginatedResponse<Integer> response = new PaginatedResponse<>(List.of(), 0L, 0, 20);

        assertThat(response.getPage()).isZero();
        assertThat(response.getTotal()).isZero();
    }

    @Test
    @DisplayName("Should preserve large total values correctly")
    void constructor_withLargeTotal_assignsCorrectly() {
        PaginatedResponse<Integer> response =
                new PaginatedResponse<>(List.of(), 999_999L, 49, 20);

        assertThat(response.getTotal()).isEqualTo(999_999L);
        assertThat(response.getPage()).isEqualTo(49);
    }
}
