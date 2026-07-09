package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignarCoordinadorUseCase {

    private final GrupoInvestigacionRepositoryPort grupoRepository;

    public AsignarCoordinadorUseCase(GrupoInvestigacionRepositoryPort grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    @Transactional
    public GrupoInvestigacion execute(Integer idGrupo, Integer idUsuario) {
        GrupoInvestigacion grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new ResourceNotFoundException("grupos.error.not-found", idGrupo));

        if (!grupoRepository.existeUsuarioActivo(idUsuario)) {
            throw new BusinessException("grupos.error.coordinator-invalid", idUsuario);
        }

        grupo.setIdCoordinadorActual(idUsuario);
        return grupoRepository.save(grupo);
    }
}
