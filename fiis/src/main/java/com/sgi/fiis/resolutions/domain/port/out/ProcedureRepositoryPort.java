package com.sgi.fiis.resolutions.domain.port.out;

public interface ProcedureRepositoryPort {
    boolean existsProcedure(Long idTramite);
    void updateStatusToApprovedWithResolution(Long idTramite);
}
