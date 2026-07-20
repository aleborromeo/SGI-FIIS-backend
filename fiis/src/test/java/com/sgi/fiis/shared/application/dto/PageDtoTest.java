package com.sgi.fiis.shared.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PageDto Unit Tests")
class PageDtoTest {

    @Test
    @DisplayName("Should initialize fields correctly and compute pages")
    void testPageDtoInitialization() {
        List<String> content = List.of("item1", "item2");
        PageDto<String> pageDto = new PageDto<>(content, 11L, 0, 5);

        assertEquals(content, pageDto.getContent());
        assertEquals(11L, pageDto.getTotalElements());
        assertEquals(0, pageDto.getPage());
        assertEquals(5, pageDto.getSize());
        assertEquals(3, pageDto.getTotalPages()); // ceil(11/5) = 3
    }

    @Test
    @DisplayName("Should handle null content and zero size safely")
    void testPageDtoNullContentAndZeroSize() {
        PageDto<String> pageDto = new PageDto<>(null, 0L, 1, 0);

        assertNotNull(pageDto.getContent());
        assertTrue(pageDto.getContent().isEmpty());
        assertEquals(0L, pageDto.getTotalElements());
        assertEquals(1, pageDto.getPage());
        assertEquals(0, pageDto.getSize());
        assertEquals(0, pageDto.getTotalPages());
    }
}
