package com.sgi.fiis.convocatorias.infrastructure.persistence;

import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentEntity;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentJpaRepository;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SaveCallAdapter Unit Tests")
class SaveCallAdapterTest {

    private ResearchCallJpaRepository jpaRepository;
    private DocumentJpaRepository documentRepository;
    private ResearchLineJpaRepository lineRepository;
    private SpringDataUserRepository userRepository;
    private SaveCallAdapter adapter;

    private static final LocalDate START = LocalDate.of(2026, Month.JANUARY, 1);
    private static final LocalDate END = LocalDate.of(2026, Month.DECEMBER, 31);

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        jpaRepository = mock(ResearchCallJpaRepository.class);
        documentRepository = mock(DocumentJpaRepository.class);
        lineRepository = mock(ResearchLineJpaRepository.class);
        userRepository = mock(SpringDataUserRepository.class);
        adapter = new SaveCallAdapter(jpaRepository, documentRepository, lineRepository, userRepository);
    }

    private ResearchCallEntity createEntity(Integer id, String status) {
        ResearchCallEntity entity = new ResearchCallEntity();
        entity.setId(id);
        entity.setTitle("Test Call");
        entity.setDescription("Description");
        entity.setTitleJson("{\"es\":\"Test Call\"}");
        entity.setDescriptionJson("{\"es\":\"Description\"}");
        entity.setStartDate(START);
        entity.setEndDate(END);
        entity.setStatus(status);

        UserEntity creator = new UserEntity();
        creator.setId(1L);
        entity.setCreator(creator);

        return entity;
    }

    @Test
    @DisplayName("save: creates entity and returns domain")
    void save_createsAndReturns() {
        ResearchCall domain = new ResearchCall(null, "New Call", "Desc", START, END,
                CallStatus.OPEN, null, List.of(1));

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        ResearchCall result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(jpaRepository).save(any());
    }

    @Test
    @DisplayName("save: maps CLOSED status")
    void save_closedStatus() {
        ResearchCall domain = new ResearchCall(null, "Closed Call", "Desc", START, END,
                CallStatus.CLOSED, null, null);

        ResearchCallEntity savedEntity = createEntity(1, "CERRADA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> "CERRADA".equals(e.getStatus())));
    }

    @Test
    @DisplayName("save: maps FINISHED status")
    void save_finishedStatus() {
        ResearchCall domain = new ResearchCall(null, "Finished Call", "Desc", START, END,
                CallStatus.FINISHED, null, null);

        ResearchCallEntity savedEntity = createEntity(1, "FINALIZADA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> "FINALIZADA".equals(e.getStatus())));
    }

    @Test
    @DisplayName("save: with document and creator")
    void save_withDocumentAndCreator() {
        ResearchCall domain = new ResearchCall(null, "Call", "Desc", START, END,
                CallStatus.OPEN, 5, 10, List.of(1));

        DocumentEntity doc = new DocumentEntity();
        doc.setId(5);
        UserEntity creator = new UserEntity();
        creator.setId(10L);

        when(documentRepository.findById(5)).thenReturn(Optional.of(doc));
        when(userRepository.findById(10L)).thenReturn(Optional.of(creator));

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> e.getDocument() != null && e.getCreator() != null));
    }

    @Test
    @DisplayName("save: with null document and creator")
    void save_nullDocumentAndCreator() {
        ResearchCall domain = new ResearchCall(null, "Call", "Desc", START, END,
                CallStatus.OPEN, null, null);

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> e.getDocument() == null && e.getCreator() == null));
    }

    @Test
    @DisplayName("findById: found")
    void findById_found() {
        when(jpaRepository.findById(1)).thenReturn(Optional.of(createEntity(1, "ABIERTA")));

        Optional<ResearchCall> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("findById: not found")
    void findById_notFound() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(99).isEmpty());
    }

    @Test
    @DisplayName("findByStatus: OPEN returns ABIERTA")
    void findByStatus_open() {
        when(jpaRepository.findByStatus("ABIERTA")).thenReturn(List.of(createEntity(1, "ABIERTA")));

        List<ResearchCall> result = adapter.findByStatus(CallStatus.OPEN);

        assertEquals(1, result.size());
        assertEquals(CallStatus.OPEN, result.get(0).getStatus());
    }

    @Test
    @DisplayName("findByStatus: CLOSED returns CERRADA")
    void findByStatus_closed() {
        when(jpaRepository.findByStatus("CERRADA")).thenReturn(List.of(createEntity(1, "CERRADA")));

        List<ResearchCall> result = adapter.findByStatus(CallStatus.CLOSED);

        assertEquals(1, result.size());
        assertEquals(CallStatus.CLOSED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("findByStatus: FINISHED returns FINALIZADA")
    void findByStatus_finished() {
        when(jpaRepository.findByStatus("FINALIZADA")).thenReturn(List.of(createEntity(1, "FINALIZADA")));

        List<ResearchCall> result = adapter.findByStatus(CallStatus.FINISHED);

        assertEquals(1, result.size());
        assertEquals(CallStatus.FINISHED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("findByStatus: returns empty list")
    void findByStatus_empty() {
        when(jpaRepository.findByStatus("ABIERTA")).thenReturn(List.of());

        assertTrue(adapter.findByStatus(CallStatus.OPEN).isEmpty());
    }

    @Test
    @DisplayName("areLinesActive: null returns false")
    void areLinesActive_null() {
        assertFalse(adapter.areLinesActive(null));
    }

    @Test
    @DisplayName("areLinesActive: empty returns false")
    void areLinesActive_empty() {
        assertFalse(adapter.areLinesActive(List.of()));
    }

    @Test
    @DisplayName("areLinesActive: all active returns true")
    void areLinesActive_allActive() {
        ResearchLineEntity line1 = new ResearchLineEntity();
        line1.setId(1);
        line1.setActive(true);
        ResearchLineEntity line2 = new ResearchLineEntity();
        line2.setId(2);
        line2.setActive(true);

        when(lineRepository.findById(1)).thenReturn(Optional.of(line1));
        when(lineRepository.findById(2)).thenReturn(Optional.of(line2));

        assertTrue(adapter.areLinesActive(List.of(1, 2)));
    }

    @Test
    @DisplayName("areLinesActive: one inactive returns false")
    void areLinesActive_oneInactive() {
        ResearchLineEntity line1 = new ResearchLineEntity();
        line1.setId(1);
        line1.setActive(true);
        ResearchLineEntity line2 = new ResearchLineEntity();
        line2.setId(2);
        line2.setActive(false);

        when(lineRepository.findById(1)).thenReturn(Optional.of(line1));
        when(lineRepository.findById(2)).thenReturn(Optional.of(line2));

        assertFalse(adapter.areLinesActive(List.of(1, 2)));
    }

    @Test
    @DisplayName("areLinesActive: one not found returns false")
    void areLinesActive_oneNotFound() {
        ResearchLineEntity line1 = new ResearchLineEntity();
        line1.setId(1);
        line1.setActive(true);

        when(lineRepository.findById(1)).thenReturn(Optional.of(line1));
        when(lineRepository.findById(2)).thenReturn(Optional.empty());

        assertFalse(adapter.areLinesActive(List.of(1, 2)));
    }

    @Test
    @DisplayName("findAll: returns all calls")
    void findAll_returnsAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                createEntity(1, "ABIERTA"),
                createEntity(2, "CERRADA")));

        List<ResearchCall> result = adapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("toDomain: CLOSED status maps correctly")
    void toDomain_closedStatus() {
        when(jpaRepository.findById(1)).thenReturn(Optional.of(createEntity(1, "CERRADA")));

        ResearchCall result = adapter.findById(1).get();
        assertEquals(CallStatus.CLOSED, result.getStatus());
    }

    @Test
    @DisplayName("toDomain: FINISHED status maps correctly")
    void toDomain_finishedStatus() {
        when(jpaRepository.findById(1)).thenReturn(Optional.of(createEntity(1, "FINALIZADA")));

        ResearchCall result = adapter.findById(1).get();
        assertEquals(CallStatus.FINISHED, result.getStatus());
    }

    @Test
    @DisplayName("toDomain: with document and creator")
    void toDomain_withDocumentAndCreator() {
        ResearchCallEntity entity = createEntity(1, "ABIERTA");
        DocumentEntity doc = new DocumentEntity();
        doc.setId(5);
        entity.setDocument(doc);
        UserEntity creator = new UserEntity();
        creator.setId(10L);
        entity.setCreator(creator);

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        ResearchCall result = adapter.findById(1).get();
        assertEquals(5, result.getDocumentId());
        assertEquals(10, result.getCreatorId());
    }

    @Test
    @DisplayName("toDomain: with research lines")
    void toDomain_withResearchLines() {
        ResearchCallEntity entity = createEntity(1, "ABIERTA");
        ResearchLineEntity line = new ResearchLineEntity();
        line.setId(1);
        entity.setResearchLines(List.of(line));

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        ResearchCall result = adapter.findById(1).get();
        assertEquals(1, result.getResearchLineIds().size());
        assertEquals(1, result.getResearchLineIds().get(0));
    }

    @Test
    @DisplayName("toDomain: null research lines returns empty list")
    void toDomain_nullResearchLines() {
        ResearchCallEntity entity = createEntity(1, "ABIERTA");
        entity.setResearchLines(null);

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));

        ResearchCall result = adapter.findById(1).get();
        assertNotNull(result.getResearchLineIds());
        assertTrue(result.getResearchLineIds().isEmpty());
    }

    @Test
    @DisplayName("save: with research lines")
    void save_withResearchLines() {
        ResearchCall domain = new ResearchCall(null, "Call", "Desc", START, END,
                CallStatus.OPEN, null, List.of(1, 2));

        ResearchLineEntity line1 = new ResearchLineEntity();
        line1.setId(1);
        ResearchLineEntity line2 = new ResearchLineEntity();
        line2.setId(2);
        when(lineRepository.findById(1)).thenReturn(Optional.of(line1));
        when(lineRepository.findById(2)).thenReturn(Optional.of(line2));

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> e.getResearchLines() != null && e.getResearchLines().size() == 2));
    }

    @Test
    @DisplayName("save: with null research lines sets empty list")
    void save_nullResearchLines() {
        ResearchCall domain = new ResearchCall(null, "Call", "Desc", START, END,
                CallStatus.OPEN, null, null);

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> e.getResearchLines() != null && e.getResearchLines().isEmpty()));
    }

    @Test
    @DisplayName("save: maps OPEN status to ABIERTA")
    void save_openStatus() {
        ResearchCall domain = new ResearchCall(null, "Open Call", "Desc", START, END,
                CallStatus.OPEN, null, null);

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> "ABIERTA".equals(e.getStatus())));
    }

    @Test
    @DisplayName("save: with null title and description")
    void save_nullTitleAndDescription() {
        ResearchCall domain = new ResearchCall(null, null, null, START, END,
                CallStatus.OPEN, null, null);

        ResearchCallEntity savedEntity = createEntity(1, "ABIERTA");
        when(jpaRepository.save(any())).thenReturn(savedEntity);

        adapter.save(domain);

        verify(jpaRepository).save(argThat(e -> e.getTitle() == null && e.getDescription() == null));
    }
}
