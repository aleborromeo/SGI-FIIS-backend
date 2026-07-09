package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.resolutions.domain.port.out.ProcedureRepositoryPort;
import com.sgi.fiis.tramites.application.usecase.RegisterResolutionUseCase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("resolutionsProcedureRepositoryAdapter")
public class ResolutionsProcedureRepositoryAdapter implements ProcedureRepositoryPort {

    private final JdbcTemplate jdbcTemplate;
    private final RegisterResolutionUseCase registerResolutionUseCase;

    public ResolutionsProcedureRepositoryAdapter(
            JdbcTemplate jdbcTemplate,
            RegisterResolutionUseCase registerResolutionUseCase) {
        this.jdbcTemplate = jdbcTemplate;
        this.registerResolutionUseCase = registerResolutionUseCase;
    }

    @Override
    public boolean existsProcedure(Long idTramite) {
        String sql = "SELECT count(*) FROM tramites WHERE id_tramite = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idTramite);
        return count != null && count > 0;
    }

    @Override
    public void updateStatusToApprovedWithResolution(Long idTramite) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long decanoId = 1L; // Fallback if no security context exists (e.g. tests)
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            decanoId = userDetails.getId();
        }
        registerResolutionUseCase.execute(idTramite, decanoId);
    }
}
