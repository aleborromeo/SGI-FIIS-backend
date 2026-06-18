package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LineaInvestigacionRepositoryAdapter implements LineaInvestigacionRepositoryPort {

    private final SpringDataLineaRepository springDataRepository;

    public LineaInvestigacionRepositoryAdapter(SpringDataLineaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public LineaInvestigacion save(LineaInvestigacion linea) {
        return toDomain(springDataRepository.save(toEntity(linea)));
    }

    @Override
    public Optional<LineaInvestigacion> findById(Integer id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<LineaInvestigacion> findAll() {
        return springDataRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<LineaInvestigacion> findAllActivas() {
        return springDataRepository.findByEsActivaTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<LineaInvestigacion> findActivasByGrupo(Integer idGrupo) {
        return springDataRepository.findActivasByIdGrupo(idGrupo).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByNombre(String nombreLinea) {
        return springDataRepository.existsByNombreLinea(nombreLinea);
    }

    private LineaInvestigacion toDomain(LineaInvestigacionEntity entity) {
        return LineaInvestigacion.builder()
                .id(entity.getId())
                .nombreLinea(entity.getNombreLinea())
                .esActiva(entity.isEsActiva())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private LineaInvestigacionEntity toEntity(LineaInvestigacion domain) {
        LineaInvestigacionEntity entity = new LineaInvestigacionEntity();
        entity.setId(domain.getId());
        entity.setNombreLinea(domain.getNombreLinea());
        entity.setEsActiva(domain.isEsActiva());
        entity.setFechaCreacion(domain.getFechaCreacion());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
        return entity;
    }
}
