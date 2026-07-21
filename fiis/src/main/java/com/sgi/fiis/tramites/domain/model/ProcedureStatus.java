package com.sgi.fiis.tramites.domain.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum ProcedureStatus {

    REGISTRADO("REGISTRADO"),
    PENDIENTE_COORDINADOR("PENDIENTE_COORDINADOR"),
    OBSERVADO("OBSERVADO"),
    SUBSANADO("SUBSANADO"),
    PENDIENTE_DIRECCION("PENDIENTE_DIRECCION"),
    PENDIENTE_DECANATO("PENDIENTE_DECANATO"),
    APROBADO_CON_RESOLUCION("APROBADO_CON_RESOLUCION"),
    FINALIZADO("FINALIZADO"),
    RECHAZADO("RECHAZADO");

    private final String dbValue;

    ProcedureStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ProcedureStatus fromDbValue(String dbValue) {
        for (ProcedureStatus s : values()) {
            if (s.dbValue.equals(dbValue)) return s;
        }
        throw new IllegalArgumentException("Unknown ProcedureStatus DB value: " + dbValue);
    }

    private static final Map<ProcedureStatus, Set<ProcedureStatus>> TRANSICIONES_VALIDAS;

    static {
        Map<ProcedureStatus, Set<ProcedureStatus>> mapa = new EnumMap<>(ProcedureStatus.class);
        mapa.put(REGISTRADO,               EnumSet.of(PENDIENTE_COORDINADOR));
        mapa.put(PENDIENTE_COORDINADOR,      EnumSet.of(PENDIENTE_DIRECCION, OBSERVADO, RECHAZADO));
        mapa.put(OBSERVADO,                 EnumSet.of(SUBSANADO));
        mapa.put(SUBSANADO,               EnumSet.of(PENDIENTE_COORDINADOR));
        mapa.put(PENDIENTE_DIRECCION,        EnumSet.of(PENDIENTE_DECANATO, OBSERVADO, RECHAZADO));
        mapa.put(PENDIENTE_DECANATO,             EnumSet.of(APROBADO_CON_RESOLUCION, OBSERVADO));
        mapa.put(APROBADO_CON_RESOLUCION, EnumSet.of(FINALIZADO));
        mapa.put(FINALIZADO,                Collections.emptySet());
        mapa.put(RECHAZADO,                 Collections.emptySet());
        TRANSICIONES_VALIDAS = Collections.unmodifiableMap(mapa);
    }

    public boolean canTransitionTo(ProcedureStatus destino) {
        return TRANSICIONES_VALIDAS.getOrDefault(this, Collections.emptySet()).contains(destino);
    }

    public boolean isTerminalStatus() {
        return this == FINALIZADO || this == RECHAZADO;
    }
}
