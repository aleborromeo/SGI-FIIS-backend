package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearGrupoUseCase {

    private final GrupoInvestigacionRepositoryPort grupoRepository;

    public CrearGrupoUseCase(GrupoInvestigacionRepositoryPort grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    @Transactional
    public GrupoInvestigacion execute(GrupoInvestigacion grupo) {
        if (grupoRepository.existsByCodigo(grupo.getCodigoGrupo())) {
            throw new DuplicateResourceException("GrupoInvestigacion", "codigoGrupo", grupo.getCodigoGrupo());
        }
        grupo.setEsActivo(true);
        return grupoRepository.save(grupo);
    }
}
