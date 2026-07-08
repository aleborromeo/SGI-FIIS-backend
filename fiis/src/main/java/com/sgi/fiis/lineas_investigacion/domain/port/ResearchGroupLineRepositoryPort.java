package com.sgi.fiis.lineas_investigacion.domain.port;

public interface ResearchGroupLineRepositoryPort {
    void assignGroupToLine(Integer groupId, Integer lineId);
    void removeGroupFromLine(Integer groupId, Integer lineId);
    boolean isGroupAssignedToLine(Integer groupId, Integer lineId);
}
