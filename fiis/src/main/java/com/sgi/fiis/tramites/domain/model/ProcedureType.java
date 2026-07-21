package com.sgi.fiis.tramites.domain.model;

public enum ProcedureType {

    PROJECT("PROYECTO"),
    THESIS("TESIS"),
    REPORT("INFORME"),
    PLAN_TESIS("PLAN_TESIS"),
    REPORT_AVANCE("INFORME_AVANCE");

    private final String dbValue;

    ProcedureType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ProcedureType fromDbValue(String dbValue) {
        for (ProcedureType t : values()) {
            if (t.dbValue.equals(dbValue)) return t;
        }
        throw new IllegalArgumentException("Unknown ProcedureType DB value: " + dbValue);
    }
}
