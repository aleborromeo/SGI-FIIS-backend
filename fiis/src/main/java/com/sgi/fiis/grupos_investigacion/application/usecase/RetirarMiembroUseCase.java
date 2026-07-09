package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetirarMiembroUseCase {

    private final MembresiaRepositoryPort membresiaRepository;

    public RetirarMiembroUseCase(MembresiaRepositoryPort membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional
    public Membresia execute(Integer idGrupo, Integer idUsuario) {
        Membresia membresia = membresiaRepository.findActivaByUsuarioEnGrupo(idUsuario, idGrupo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "grupos.error.membership-not-found", idUsuario, idGrupo));
        membresia.retirar();
        return membresiaRepository.save(membresia);
    }
}
