package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import java.io.Serializable;
import java.util.Objects;

public class ResearchGroupLineId implements Serializable {
    private Integer groupId;
    private Integer lineId;

    public ResearchGroupLineId() {}

    public ResearchGroupLineId(Integer groupId, Integer lineId) {
        this.groupId = groupId;
        this.lineId = lineId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchGroupLineId that = (ResearchGroupLineId) o;
        return Objects.equals(groupId, that.groupId) &&
               Objects.equals(lineId, that.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, lineId);
    }
}
