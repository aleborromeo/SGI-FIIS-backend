package pe.unas.fiis.sgifiis.thesis.domain.port.out;

import java.util.List;
import java.util.Optional;
import pe.unas.fiis.sgifiis.thesis.domain.InformeTesis;

public interface InformeTesisRepositoryPort {
    InformeTesis save(InformeTesis informeTesis);
    Optional<InformeTesis> findById(Integer idInformeTesis);
    List<InformeTesis> findByPlanTesis(Integer idPlanTesis);
}
