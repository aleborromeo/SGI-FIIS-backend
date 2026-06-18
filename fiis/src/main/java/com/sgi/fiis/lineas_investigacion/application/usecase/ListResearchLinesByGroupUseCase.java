package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarLineasPorGrupoUseCase {

    private final LineaInvestigacionRepositoryPort lineaRepository;

    public ListarLineasPorGrupoUseCase(LineaInvestigacionRepositoryPort lineaRepository) {
        this.lineaRepository = lineaRepository;
    }

    public List<LineaInvestigacion> execute(Integer idGrupo) {
        return lineaRepository.findActivasByGrupo(idGrupo);
    }
}
