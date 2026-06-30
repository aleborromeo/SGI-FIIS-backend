package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ObtenerGrupoUseCase {

    private final GrupoInvestigacionRepositoryPort grupoRepository;

    public ObtenerGrupoUseCase(GrupoInvestigacionRepositoryPort grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    public GrupoInvestigacion execute(Integer id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GrupoInvestigacion", "id", id));
    }
}
