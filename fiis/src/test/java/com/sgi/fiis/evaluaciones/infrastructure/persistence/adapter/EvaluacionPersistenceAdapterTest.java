package com.sgi.fiis.evaluaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.entity.EvaluacionJpaEntity;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.mapper.EvaluacionPersistenceMapper;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.repository.EvaluacionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionPersistenceAdapterTest {

    @Mock
    private EvaluacionJpaRepository evaluacionJpaRepository;

    private EvaluacionPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionPersistenceAdapter(evaluacionJpaRepository);
    }

    @Test
    void guardarDebePersistirYRetornarDominio() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        EvaluacionJpaEntity entityGuardada = EvaluacionPersistenceMapper.toJpaEntity(
                Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );

        when(evaluacionJpaRepository.save(any(EvaluacionJpaEntity.class)))
                .thenReturn(entityGuardada);

        Evaluacion resultado = adapter.guardar(evaluacion);

        assertEquals(1L, resultado.getIdEvaluacion());
        assertEquals(1L, resultado.getIdProyecto());
        assertEquals(2L, resultado.getIdEvaluador());
        assertTrue(resultado.estaPendiente());

        verify(evaluacionJpaRepository).save(any(EvaluacionJpaEntity.class));
    }

    @Test
    void buscarPorIdDebeRetornarEvaluacionCuandoExiste() {
        EvaluacionJpaEntity entity = EvaluacionPersistenceMapper.toJpaEntity(
                Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );

        when(evaluacionJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Evaluacion> resultado = adapter.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdEvaluacion());
    }

    @Test
    void buscarPorIdDebeRetornarVacioCuandoNoExiste() {
        when(evaluacionJpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Evaluacion> resultado = adapter.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPorEvaluadorDebeRetornarLista() {
        EvaluacionJpaEntity entity = EvaluacionPersistenceMapper.toJpaEntity(
                Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );

        when(evaluacionJpaRepository.findByIdEvaluador(2L)).thenReturn(List.of(entity));

        List<Evaluacion> resultado = adapter.listarPorEvaluador(2L);

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getIdEvaluador());
    }

    @Test
    void listarTodasDebeRetornarLista() {
        EvaluacionJpaEntity entity = EvaluacionPersistenceMapper.toJpaEntity(
                Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );

        when(evaluacionJpaRepository.findAll()).thenReturn(List.of(entity));

        List<Evaluacion> resultado = adapter.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdEvaluacion());
    }

    @Test
    void existeEvaluacionPendienteParaProyectoDebeRetornarTrue() {
        when(evaluacionJpaRepository.existsByIdProyectoAndIdEvaluadorAndResultadoIsNullAndFechaEvaluacionIsNull(1L, 2L))
                .thenReturn(true);

        boolean resultado = adapter.existeEvaluacionPendienteParaProyecto(1L, 2L);

        assertTrue(resultado);
    }

    @Test
    void existeEvaluacionPendienteParaPlanTesisDebeRetornarTrue() {
        when(evaluacionJpaRepository.existsByIdPlanTesisAndIdEvaluadorAndResultadoIsNullAndFechaEvaluacionIsNull(5L, 2L))
                .thenReturn(true);

        boolean resultado = adapter.existeEvaluacionPendienteParaPlanTesis(5L, 2L);

        assertTrue(resultado);
    }
}