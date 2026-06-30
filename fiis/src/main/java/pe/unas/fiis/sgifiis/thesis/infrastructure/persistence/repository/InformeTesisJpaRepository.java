package pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.unas.fiis.sgifiis.thesis.infrastructure.persistence.entity.InformeTesisEntity;

public interface InformeTesisJpaRepository extends JpaRepository<InformeTesisEntity, Integer> {
    List<InformeTesisEntity> findByIdPlanTesis(Integer idPlanTesis);
}
