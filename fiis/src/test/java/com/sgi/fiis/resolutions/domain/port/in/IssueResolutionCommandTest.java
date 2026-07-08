package com.sgi.fiis.resolutions.domain.port.in;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IssueResolutionCommandTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDate now = LocalDate.now();
        byte[] fileBytes = new byte[]{1, 2, 3};
        
        IssueResolutionCommand command1 = new IssueResolutionCommand(
                "RES-001", now, "Asunto", 1L, fileBytes, "test.pdf", "application/pdf"
        );
        
        IssueResolutionCommand command2 = new IssueResolutionCommand(
                "RES-001", now, "Asunto", 1L, fileBytes, "test.pdf", "application/pdf"
        );
        
        IssueResolutionCommand command3 = new IssueResolutionCommand(
                "RES-002", now, "Asunto", 1L, fileBytes, "test.pdf", "application/pdf"
        );

        assertEquals(command1, command1);
        assertEquals(command1, command2);
        assertNotEquals(command1, command3);
        assertNotEquals(command1, null);
        assertNotEquals(command1, new Object());
        
        assertEquals(command1.hashCode(), command2.hashCode());
        assertNotEquals(command1.hashCode(), command3.hashCode());
    }

    @Test
    void testToString() {
        IssueResolutionCommand command = new IssueResolutionCommand(
                "RES-001", LocalDate.of(2023, 10, 1), "Asunto", 1L, new byte[]{1}, "test.pdf", "application/pdf"
        );
        
        String toString = command.toString();
        assertTrue(toString.contains("RES-001"));
        assertTrue(toString.contains("Asunto"));
        assertTrue(toString.contains("test.pdf"));
        assertTrue(toString.contains("array of size 1"));
        
        IssueResolutionCommand commandNullBytes = new IssueResolutionCommand(
                "RES-001", LocalDate.of(2023, 10, 1), "Asunto", 1L, null, "test.pdf", "application/pdf"
        );
        assertTrue(commandNullBytes.toString().contains("null"));
    }
}
