package com.sgi.fiis.convocatorias.infrastructure.persistence;

import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveCallAdapterTest {

    private ResearchCallJpaRepository jpaRepository;
    private DocumentJpaRepository documentJpaRepository;
    private ResearchLineJpaRepository lineJpaRepository;
    private SaveCallAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = Mockito.mock(ResearchCallJpaRepository.class);
        documentJpaRepository = Mockito.mock(DocumentJpaRepository.class);
        lineJpaRepository = Mockito.mock(ResearchLineJpaRepository.class);
        adapter = new SaveCallAdapter(jpaRepository, documentJpaRepository, lineJpaRepository);
    }

    @Test
    void shouldSaveCallOpen() {
        ResearchCall domain = new ResearchCall(1, "Call Open", "Description", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.OPEN, null, null);
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(1)
                .title("Call Open")
                .description("Description")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("ABIERTA")
                .build();

        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Call Open", result.getTitle());
        assertEquals(CallStatus.OPEN, result.getStatus());
    }

    @Test
    void shouldSaveCallClosed() {
        ResearchCall domain = new ResearchCall(1, "Call Closed", "Description", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.CLOSED, null, null);
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(1)
                .title("Call Closed")
                .description("Description")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("CERRADA")
                .build();

        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertEquals(CallStatus.CLOSED, result.getStatus());
    }

    @Test
    void shouldSaveCallFinished() {
        ResearchCall domain = new ResearchCall(1, "Call Finished", "Description", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.FINISHED, null, null);
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(1)
                .title("Call Finished")
                .description("Description")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("FINALIZADA")
                .build();

        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertEquals(CallStatus.FINISHED, result.getStatus());
    }

    @Test
    void shouldFindById() {
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(2)
                .title("Some Call")
                .description("Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .status("CERRADA")
                .build();

        when(jpaRepository.findById(2)).thenReturn(Optional.of(entity));

        Optional<ResearchCall> result = adapter.findById(2);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId());
        assertEquals(CallStatus.CLOSED, result.get().getStatus());
    }

    @Test
    void shouldFindByIdEmpty() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());
        Optional<ResearchCall> result = adapter.findById(99);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByStatus() {
        ResearchCallEntity openEntity = ResearchCallEntity.builder().id(1).description("Desc").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("ABIERTA").build();
        ResearchCallEntity closedEntity = ResearchCallEntity.builder().id(2).description("Desc").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("CERRADA").build();
        ResearchCallEntity finishedEntity = ResearchCallEntity.builder().id(3).description("Desc").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("FINALIZADA").build();

        when(jpaRepository.findByStatus("ABIERTA")).thenReturn(Arrays.asList(openEntity));
        when(jpaRepository.findByStatus("CERRADA")).thenReturn(Arrays.asList(closedEntity));
        when(jpaRepository.findByStatus("FINALIZADA")).thenReturn(Arrays.asList(finishedEntity));

        List<ResearchCall> openResult = adapter.findByStatus(CallStatus.OPEN);
        assertEquals(1, openResult.size());
        assertEquals(CallStatus.OPEN, openResult.get(0).getStatus());

        List<ResearchCall> closedResult = adapter.findByStatus(CallStatus.CLOSED);
        assertEquals(1, closedResult.size());
        assertEquals(CallStatus.CLOSED, closedResult.get(0).getStatus());

        List<ResearchCall> finishedResult = adapter.findByStatus(CallStatus.FINISHED);
        assertEquals(1, finishedResult.size());
        assertEquals(CallStatus.FINISHED, finishedResult.get(0).getStatus());
    }

    @Test
    void shouldFindAll() {
        ResearchCallEntity entity1 = ResearchCallEntity.builder().id(1).description("Desc").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("ABIERTA").build();
        ResearchCallEntity entity2 = ResearchCallEntity.builder().id(2).description("Desc").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("CERRADA").build();
        when(jpaRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<ResearchCall> result = adapter.findAll();
        assertEquals(2, result.size());
    }
}
