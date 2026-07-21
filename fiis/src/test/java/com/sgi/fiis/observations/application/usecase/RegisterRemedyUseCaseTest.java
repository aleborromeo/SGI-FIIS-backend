package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.observations.application.dto.RemedyRequestDTO;
import com.sgi.fiis.observations.application.dto.RemedyResponseDTO;
import com.sgi.fiis.observations.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observations.domain.exception.InvalidRemedyException;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationStatus;
import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import com.sgi.fiis.observations.domain.port.RemedyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterRemedyUseCaseTest {

    @Mock
    private ObservationRepository observationRepository;

    @Mock
    private RemedyRepository remedyRepository;

    private RegisterRemedyUseCase registerRemedyUseCase;

    @BeforeEach
    void setUp() {
        registerRemedyUseCase = new RegisterRemedyUseCase(observationRepository, remedyRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void execute_WhenObservationIsPending_ShouldMarkRemediedAndSaveRemedy() {
        // Arrange
        Integer observationId = 100;
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(5)
                .description("He subido el documento firmado correctamente.")
                .attachedDocumentId(45)
                .build();

        LocalDateTime fechaFija = LocalDateTime.of(2026, Month.JUNE, 17, 12, 0);
        Observation observationOriginal = Observation.builder()
                .id(observationId)
                .procedureId(1)
                .reviewerId(10)
                .type(ObservationType.TECNICA)
                .description("Falta la firma en el documento de propuesta técnica")
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(fechaFija)
                .updatedAt(fechaFija)
                .build();

        Remedy remedySaved = Remedy.builder()
                .id(200)
                .observationId(observationId)
                .applicantId(5)
                .description("He subido el documento firmado correctamente.")
                .attachedDocumentId(45)
                .createdAt(fechaFija)
                .updatedAt(fechaFija)
                .build();

        when(observationRepository.findById(observationId)).thenReturn(Optional.of(observationOriginal));
        when(observationRepository.save(any(Observation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(remedyRepository.save(any(Remedy.class))).thenReturn(remedySaved);

        // Act
        RemedyResponseDTO response = registerRemedyUseCase.execute(observationId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getId());
        assertEquals(observationId, response.getObservationId());
        assertEquals(5, response.getApplicantId());
        assertEquals("He subido el documento firmado correctamente.", response.getDescription());
        assertEquals(45, response.getAttachedDocumentId());
        assertEquals(fechaFija, response.getCreatedAt());
        assertEquals(fechaFija, response.getUpdatedAt());

        // Verify that observation was marked as REMEDIED and updated
        assertEquals(ObservationStatus.SUBSANADA, observationOriginal.getStatus());
        verify(observationRepository, times(1)).save(observationOriginal);
        verify(remedyRepository, times(1)).save(any(Remedy.class));
    }

    @Test
    void execute_WhenObservationNotFound_ShouldThrowObservationNotFoundException() {
        // Arrange
        Integer observationId = 999;
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(5)
                .description("Test")
                .build();

        when(observationRepository.findById(observationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObservationNotFoundException.class, () -> registerRemedyUseCase.execute(observationId, request));
        verify(observationRepository, never()).save(any(Observation.class));
        verify(remedyRepository, never()).save(any(Remedy.class));
    }

    @Test
    void execute_WhenObservationAlreadyRemedied_ShouldThrowInvalidRemedyException() {
        // Arrange
        Integer observationId = 100;
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(5)
                .description("Test")
                .build();

        LocalDateTime fechaFija = LocalDateTime.of(2026, Month.JUNE, 17, 12, 0);
        Observation observationRemedied = Observation.builder()
                .id(observationId)
                .procedureId(1)
                .reviewerId(10)
                .type(ObservationType.TECNICA)
                .description("Falta la firma")
                .status(ObservationStatus.SUBSANADA)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(fechaFija)
                .updatedAt(fechaFija)
                .build();

        when(observationRepository.findById(observationId)).thenReturn(Optional.of(observationRemedied));

        // Act & Assert
        assertThrows(InvalidRemedyException.class, () -> registerRemedyUseCase.execute(observationId, request));
        verify(observationRepository, never()).save(any(Observation.class));
        verify(remedyRepository, never()).save(any(Remedy.class));
    }

    @Test
    void execute_WhenAuthenticatedUser_ShouldUseUserIdFromSecurityContext() {
        Integer observationId = 100;
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(null)
                .description("Documento firmado")
                .attachedDocumentId(45)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(99L, "student@test.com", "pass", true, List.of(), "");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Observation observation = Observation.builder()
                .id(observationId).procedureId(1).reviewerId(10)
                .type(ObservationType.TECNICA)
                .description("Falta firma")
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(LocalDateTime.of(2026, Month.JUNE, 17, 12, 0))
                .updatedAt(LocalDateTime.of(2026, Month.JUNE, 17, 12, 0))
                .build();

        when(observationRepository.findById(observationId)).thenReturn(Optional.of(observation));
        when(observationRepository.save(any(Observation.class))).thenAnswer(inv -> inv.getArgument(0));
        LocalDateTime now = LocalDateTime.of(2026, Month.JUNE, 17, 12, 0);
        when(remedyRepository.save(any(Remedy.class))).thenReturn(
                Remedy.builder().id(200).observationId(observationId).applicantId(99)
                        .description("Documento firmado").attachedDocumentId(45)
                        .createdAt(now).updatedAt(now).build()
        );

        RemedyResponseDTO response = registerRemedyUseCase.execute(observationId, request);

        assertNotNull(response);
        assertEquals(99, response.getApplicantId());
    }

    @Test
    void execute_WhenAuthPrincipalNotCustomUserDetails_ShouldFallbackToDto() {
        Integer observationId = 100;
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(5)
                .description("Documento firmado")
                .attachedDocumentId(45)
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Observation observation = Observation.builder()
                .id(observationId).procedureId(1).reviewerId(10)
                .type(ObservationType.TECNICA)
                .description("Falta firma")
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(LocalDateTime.of(2026, Month.JUNE, 17, 12, 0))
                .updatedAt(LocalDateTime.of(2026, Month.JUNE, 17, 12, 0))
                .build();

        LocalDateTime now = LocalDateTime.of(2026, Month.JUNE, 17, 12, 0);
        when(observationRepository.findById(observationId)).thenReturn(Optional.of(observation));
        when(observationRepository.save(any(Observation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(remedyRepository.save(any(Remedy.class))).thenReturn(
                Remedy.builder().id(201).observationId(observationId).applicantId(5)
                        .description("Documento firmado").attachedDocumentId(45)
                        .createdAt(now).updatedAt(now).build()
        );

        RemedyResponseDTO response = registerRemedyUseCase.execute(observationId, request);

        assertNotNull(response);
        assertEquals(5, response.getApplicantId());
    }
}
