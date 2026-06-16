package com.sgi.fiis.resoluciones.domain.port.in;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;

public interface EmitirResolucionUseCase {
    Resolucion emitir(EmitirResolucionCommand command);
}
