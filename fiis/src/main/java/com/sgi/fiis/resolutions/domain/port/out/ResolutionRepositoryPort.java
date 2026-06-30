package com.sgi.fiis.resolutions.domain.port.out;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import java.util.Optional;

public interface ResolutionRepositoryPort {
    Resolution save(Resolution resolution);
    Optional<Resolution> findById(Long idResolucion);
    boolean existsByNumber(String numeroResolucion);
}
