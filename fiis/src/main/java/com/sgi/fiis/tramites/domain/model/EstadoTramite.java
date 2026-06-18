package com.sgi.fiis.tramites.domain.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum EstadoTramite {

    REGISTRADO,
    PENDIENTE_COORDINADOR,
    OBSERVADO,
    SUBSANADO,
    PENDIENTE_DIRECCION,
    PENDIENTE_DECANATO,
    APROBADO_CON_RESOLUCION,
    FINALIZADO,
    RECHAZADO;

    private static final Map<EstadoTramite, Set<EstadoTramite>> TRANSICIONES_VALIDAS;

    static {
        Map<EstadoTramite, Set<EstadoTramite>> mapa = new EnumMap<>(EstadoTramite.class);
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

    public boolean puedeTransicionarA(EstadoTramite destino) {
        return TRANSICIONES_VALIDAS.getOrDefault(this, Collections.emptySet()).contains(destino);
    }

    public boolean esEstadoTerminal() {
        return this == FINALIZADO || this == RECHAZADO;
    }
}
