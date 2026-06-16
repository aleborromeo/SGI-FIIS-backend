package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarGruposUseCase {

    private final GrupoInvestigacionRepositoryPort grupoRepository;

    public ListarGruposUseCase(GrupoInvestigacionRepositoryPort grupoRepository) {
        this.grupoRepository = grupoRepository;
    }

    public List<GrupoInvestigacion> execute() {
        return grupoRepository.findAll();
    }
}
