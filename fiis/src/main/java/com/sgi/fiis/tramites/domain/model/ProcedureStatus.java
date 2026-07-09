package com.sgi.fiis.tramites.domain.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum ProcedureStatus {

    REGISTRADO,
    PENDIENTE_COORDINADOR,
    OBSERVADO,
    SUBSANADO,
    PENDIENTE_DIRECCION,
    PENDIENTE_DECANATO,
    APROBADO_CON_RESOLUCION,
    FINALIZADO,
    RECHAZADO;

    private static final Map<ProcedureStatus, Set<ProcedureStatus>> TRANSICIONES_VALIDAS;

    static {
        Map<ProcedureStatus, Set<ProcedureStatus>> mapa = new EnumMap<>(ProcedureStatus.class);
        mapa.put(REGISTRADO,              EnumSet.of(PENDIENTE_COORDINADOR));
        mapa.put(PENDIENTE_COORDINADOR,   EnumSet.of(PENDIENTE_DIRECCION, OBSERVADO, RECHAZADO));
        mapa.put(OBSERVADO,               EnumSet.of(SUBSANADO));
        mapa.put(SUBSANADO,               EnumSet.of(PENDIENTE_COORDINADOR));
        mapa.put(PENDIENTE_DIRECCION,     EnumSet.of(PENDIENTE_DECANATO, OBSERVADO, RECHAZADO));
        mapa.put(PENDIENTE_DECANATO,      EnumSet.of(APROBADO_CON_RESOLUCION, OBSERVADO));
        mapa.put(APROBADO_CON_RESOLUCION, EnumSet.of(FINALIZADO));
        mapa.put(FINALIZADO,              Collections.emptySet());
        mapa.put(RECHAZADO,               Collections.emptySet());
        TRANSICIONES_VALIDAS = Collections.unmodifiableMap(mapa);
    }

    public boolean canTransitionTo(ProcedureStatus destino) {
        return TRANSICIONES_VALIDAS.getOrDefault(this, Collections.emptySet()).contains(destino);
    }

    public boolean isTerminalStatus() {
        return this == FINALIZADO || this == RECHAZADO;
    }
}
