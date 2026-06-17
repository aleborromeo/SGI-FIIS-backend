package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarMiembrosUseCase {

    private final GrupoInvestigacionRepositoryPort grupoRepository;
    private final MembresiaRepositoryPort membresiaRepository;

    public ListarMiembrosUseCase(GrupoInvestigacionRepositoryPort grupoRepository,
                                  MembresiaRepositoryPort membresiaRepository) {
        this.grupoRepository = grupoRepository;
        this.membresiaRepository = membresiaRepository;
    }

    public List<Membresia> execute(Integer idGrupo) {
        if (grupoRepository.findById(idGrupo).isEmpty()) {
            throw new ResourceNotFoundException("GrupoInvestigacion", "id", idGrupo);
        }
        return membresiaRepository.findActivasByGrupo(idGrupo);
    }
}
