package com.sgi.fiis.convocatorias.infrastructure.persistence;

import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineJpaRepository;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
class SaveCallAdapterTest {

    private static final int CREATOR_ID = 42;

    private ResearchCallJpaRepository jpaRepository;
    private DocumentJpaRepository documentJpaRepository;
    private ResearchLineJpaRepository lineJpaRepository;
    private com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository userRepository;
    private SaveCallAdapter adapter;

    private com.sgi.fiis.users.infrastructure.persistence.UserEntity fakeUserEntity;

    @BeforeEach
    public void setUp() {
        jpaRepository = mock(ResearchCallJpaRepository.class);
        documentJpaRepository = mock(DocumentJpaRepository.class);
        lineJpaRepository = mock(ResearchLineJpaRepository.class);
        userRepository = mock(com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository.class);
        adapter = new SaveCallAdapter(jpaRepository, documentJpaRepository, lineJpaRepository, userRepository);

        fakeUserEntity = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        fakeUserEntity.setId((long) CREATOR_ID);
        when(userRepository.findById((long) CREATOR_ID)).thenReturn(Optional.of(fakeUserEntity));
    }

    private com.sgi.fiis.users.infrastructure.persistence.UserEntity creatorEntity() {
        var u = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        u.setId((long) CREATOR_ID);
        return u;
    }

    @Test
    void shouldSaveCallOpen() {
        ResearchCall domain = new ResearchCall(null, "Call Open", "Description", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.OPEN, null, CREATOR_ID, null);
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(1)
                .title("Call Open")
                .description("Description")
                .titleJson("{\"es\":\"Call Open\"}")
                .descriptionJson("{\"es\":\"Description\"}")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("ABIERTA")
                .creator(creatorEntity())
                .build();

        when(userRepository.findById((long) CREATOR_ID)).thenReturn(Optional.of(creatorEntity()));
        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Call Open", result.getTitle());
        assertEquals(CallStatus.OPEN, result.getStatus());
        assertEquals(CREATOR_ID, result.getCreatorId());
    }

    @Test
    void shouldSaveCallClosed() {
        ResearchCall domain = new ResearchCall(1, "Call Closed", "Description", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.CLOSED, null, CREATOR_ID, null);
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(1)
                .title("Call Closed")
                .description("Description")
                .titleJson("{\"es\":\"Call Closed\"}")
                .descriptionJson("{\"es\":\"Description\"}")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("CERRADA")
                .creator(creatorEntity())
                .build();

        when(userRepository.findById((long) CREATOR_ID)).thenReturn(Optional.of(creatorEntity()));
        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertEquals(CallStatus.CLOSED, result.getStatus());
        assertEquals(CREATOR_ID, result.getCreatorId());
    }

    @Test
    void shouldSaveCallFinished() {
        ResearchCall domain = new ResearchCall(1, "Call Finished", "Description", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.FINISHED, null, CREATOR_ID, null);
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(1)
                .title("Call Finished")
                .description("Description")
                .titleJson("{\"es\":\"Call Finished\"}")
                .descriptionJson("{\"es\":\"Description\"}")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("FINALIZADA")
                .creator(creatorEntity())
                .build();

        when(userRepository.findById((long) CREATOR_ID)).thenReturn(Optional.of(creatorEntity()));
        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertEquals(CallStatus.FINISHED, result.getStatus());
        assertEquals(CREATOR_ID, result.getCreatorId());
    }

    @Test
    void shouldSaveCallWithDocument() {
        com.sgi.fiis.shared.infrastructure.persistence.DocumentEntity doc = new com.sgi.fiis.shared.infrastructure.persistence.DocumentEntity();
        doc.setId(99);

        ResearchCall domain = new ResearchCall(null, "Call With Doc", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.OPEN, 99, CREATOR_ID, List.of(1));
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(2)
                .title("Call With Doc")
                .description("Desc")
                .titleJson("{\"es\":\"Call With Doc\"}")
                .descriptionJson("{\"es\":\"Desc\"}")
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status("ABIERTA")
                .document(doc)
                .creator(creatorEntity())
                .researchLines(List.of())
                .build();

        when(userRepository.findById((long) CREATOR_ID)).thenReturn(Optional.of(creatorEntity()));
        when(documentJpaRepository.findById(99)).thenReturn(Optional.of(doc));
        when(lineJpaRepository.findById(1)).thenReturn(Optional.empty()); // avoid null in list
        when(jpaRepository.save(any(ResearchCallEntity.class))).thenReturn(entity);

        ResearchCall result = adapter.save(domain);
        assertNotNull(result);
        assertEquals(Integer.valueOf(99), result.getDocumentId());
        assertEquals(CREATOR_ID, result.getCreatorId());
    }

    @Test
    void shouldFindById() {
        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(2)
                .title("Some Call")
                .description("Description")
                .titleJson("{\"es\":\"Some Call\"}")
                .descriptionJson("{\"es\":\"Description\"}")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .status("CERRADA")
                .creator(creatorEntity())
                .build();

        when(jpaRepository.findById(2)).thenReturn(Optional.of(entity));

        Optional<ResearchCall> result = adapter.findById(2);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId());
        assertEquals(CallStatus.CLOSED, result.get().getStatus());
        assertEquals(CREATOR_ID, result.get().getCreatorId());
    }

    @Test
    void shouldFindByIdEmpty() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());
        Optional<ResearchCall> result = adapter.findById(99);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByStatus() {
        var openCreator = creatorEntity();
        var closedCreator = creatorEntity();
        var finishedCreator = creatorEntity();

        ResearchCallEntity openEntity = ResearchCallEntity.builder().id(1).title("Call 1").description("Desc").titleJson("{\"es\":\"Call 1\"}").descriptionJson("{\"es\":\"Desc\"}").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("ABIERTA").creator(openCreator).build();
        ResearchCallEntity closedEntity = ResearchCallEntity.builder().id(2).title("Call 2").description("Desc").titleJson("{\"es\":\"Call 2\"}").descriptionJson("{\"es\":\"Desc\"}").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("CERRADA").creator(closedCreator).build();
        ResearchCallEntity finishedEntity = ResearchCallEntity.builder().id(3).title("Call 3").description("Desc").titleJson("{\"es\":\"Call 3\"}").descriptionJson("{\"es\":\"Desc\"}").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("FINALIZADA").creator(finishedCreator).build();

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
        var entCreator = creatorEntity();
        ResearchCallEntity entity1 = ResearchCallEntity.builder().id(1).title("Call 1").description("Desc").titleJson("{\"es\":\"Call 1\"}").descriptionJson("{\"es\":\"Desc\"}").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("ABIERTA").creator(entCreator).build();
        ResearchCallEntity entity2 = ResearchCallEntity.builder().id(2).title("Call 2").description("Desc").titleJson("{\"es\":\"Call 2\"}").descriptionJson("{\"es\":\"Desc\"}").startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(10)).status("CERRADA").creator(entCreator).build();
        when(jpaRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<ResearchCall> result = adapter.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnFalseWhenLineIdsIsNullOrEmpty() {
        assertFalse(adapter.areLinesActive(null));
        assertFalse(adapter.areLinesActive(List.of()));
    }

    @Test
    void shouldReturnTrueWhenAllLinesAreActive() {
        com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity activeLine1 = new com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity();
        activeLine1.setId(10);
        activeLine1.setActive(true);

        com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity activeLine2 = new com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity();
        activeLine2.setId(20);
        activeLine2.setActive(true);

        when(lineJpaRepository.findById(10)).thenReturn(Optional.of(activeLine1));
        when(lineJpaRepository.findById(20)).thenReturn(Optional.of(activeLine2));

        assertTrue(adapter.areLinesActive(List.of(10, 20)));
    }

    @Test
    void shouldReturnFalseWhenSomeLinesAreInactiveOrNotFound() {
        com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity activeLine = new com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity();
        activeLine.setId(10);
        activeLine.setActive(true);

        com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity inactiveLine = new com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity();
        inactiveLine.setId(20);
        inactiveLine.setActive(false);

        when(lineJpaRepository.findById(10)).thenReturn(Optional.of(activeLine));
        when(lineJpaRepository.findById(20)).thenReturn(Optional.of(inactiveLine));
        when(lineJpaRepository.findById(30)).thenReturn(Optional.empty());

        assertFalse(adapter.areLinesActive(List.of(10, 20)));
        assertFalse(adapter.areLinesActive(List.of(10, 30)));
    }
}

