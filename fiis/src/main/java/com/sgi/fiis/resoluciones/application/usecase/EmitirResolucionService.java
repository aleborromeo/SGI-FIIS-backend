package com.sgi.fiis.resoluciones.application.usecase;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionCommand;
import com.sgi.fiis.resoluciones.domain.port.in.EmitirResolucionUseCase;
import com.sgi.fiis.resoluciones.domain.port.out.DocumentoStoragePort;
import com.sgi.fiis.resoluciones.domain.port.out.ResolucionRepositoryPort;
import com.sgi.fiis.resoluciones.domain.port.out.TramiteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmitirResolucionService implements EmitirResolucionUseCase {

    private final ResolucionRepositoryPort resolucionRepositoryPort;
    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final DocumentoStoragePort documentoStoragePort;

    public EmitirResolucionService(ResolucionRepositoryPort resolucionRepositoryPort,
                                   TramiteRepositoryPort tramiteRepositoryPort,
                                   DocumentoStoragePort documentoStoragePort) {
        this.resolucionRepositoryPort = resolucionRepositoryPort;
        this.tramiteRepositoryPort = tramiteRepositoryPort;
        this.documentoStoragePort = documentoStoragePort;
    }

    @Override
    @Transactional
    public Resolucion emitir(EmitirResolucionCommand command) {
        // 1. Validar si el trámite existe
        if (!tramiteRepositoryPort.existeTramite(command.idTramite())) {
            throw new IllegalArgumentException("El trámite especificado no existe.");
        }

        // 2. Validar que el número de resolución no esté duplicado
        if (resolucionRepositoryPort.existePorNumero(command.numeroResolucion())) {
            throw new IllegalArgumentException("El número de resolución ya se encuentra registrado.");
        }

        // 3. Guardar el documento adjunto (PDF) a través del puerto
        Long idDocumento = documentoStoragePort.guardarDocumento(
                command.archivoBytes(),
                command.nombreArchivo(),
                command.tipoContenido()
        );

        // 4. Crear la entidad de dominio
        Resolucion nuevaResolucion = new Resolucion(
                null, // El ID se generará en la BD
                command.numeroResolucion(),
                command.fechaEmision(),
                command.asunto(),
                command.idTramite(),
                idDocumento,
                LocalDateTime.now()
        );

        // 5. Guardar la resolución
        Resolucion resolucionGuardada = resolucionRepositoryPort.guardar(nuevaResolucion);

        // 6. Actualizar el estado del trámite a APROBADO_CON_RESOLUCION
        tramiteRepositoryPort.actualizarEstadoAAprobadoConResolucion(command.idTramite());

        return resolucionGuardada;
    }
}
