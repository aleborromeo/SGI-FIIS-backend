package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "lineas_por_grupo")
@IdClass(ResearchGroupLineId.class)
public class ResearchGroupLineEntity {

    @Id
    @Column(name = "id_grupo")
    private Integer groupId;

    @Id
    @Column(name = "id_linea")
    private Integer lineId;

    public ResearchGroupLineEntity() {}

    public ResearchGroupLineEntity(Integer groupId, Integer lineId) {
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
}
