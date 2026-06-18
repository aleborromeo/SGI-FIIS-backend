package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.unas.fiis.sgifiis.thesis.domain.InformeTesis;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.InformeTesisRepositoryPort;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.mapper.InformeTesisMapper;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.repository.InformeTesisJpaRepository;

@Repository
public class InformeTesisRepositoryAdapter implements InformeTesisRepositoryPort {
    private final InformeTesisJpaRepository repository;
    private final InformeTesisMapper mapper;

    public InformeTesisRepositoryAdapter(InformeTesisJpaRepository repository, InformeTesisMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public InformeTesis save(InformeTesis informeTesis) { return mapper.toDomain(repository.save(mapper.toEntity(informeTesis))); }
    public Optional<InformeTesis> findById(Integer idInformeTesis) { return repository.findById(idInformeTesis).map(mapper::toDomain); }
    public List<InformeTesis> findByPlanTesis(Integer idPlanTesis) { return repository.findByIdPlanTesis(idPlanTesis).stream().map(mapper::toDomain).toList(); }
}
