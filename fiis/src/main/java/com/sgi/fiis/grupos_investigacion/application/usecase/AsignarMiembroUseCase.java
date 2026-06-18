package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.domain.port.GrupoInvestigacionRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembresiaRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AsignarMiembroUseCase {

    private final GrupoInvestigacionRepositoryPort grupoRepository;
    private final MembresiaRepositoryPort membresiaRepository;

    public AsignarMiembroUseCase(GrupoInvestigacionRepositoryPort grupoRepository,
                                  MembresiaRepositoryPort membresiaRepository) {
        this.grupoRepository = grupoRepository;
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional
    public Membresia execute(Integer idGrupo, Integer idUsuario) {
        if (grupoRepository.findById(idGrupo).isEmpty()) {
            throw new ResourceNotFoundException("GrupoInvestigacion", "id", idGrupo);
        }

        if (!grupoRepository.existeUsuarioActivo(idUsuario)) {
            throw new BusinessException("El usuario con id " + idUsuario + " no existe o no está activo");
        }

        if (membresiaRepository.existsActivaByUsuario(idUsuario)) {
            throw new BusinessException("El usuario con id " + idUsuario + " ya pertenece a un grupo de investigación activo (RF-21)");
        }

        Membresia membresia = Membresia.builder()
                .idGrupo(idGrupo)
                .idUsuario(idUsuario)
                .esActivo(true)
                .fechaInicio(LocalDateTime.now())
                .build();

        return membresiaRepository.save(membresia);
    }
}
