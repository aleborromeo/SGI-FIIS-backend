package com.sgi.fiis.reports.domain.model;

import java.time.LocalDateTime;

public class ProcedureRecentActivity {

    private Integer procedureId;
    private String procedureCode;
    private String procedureType;
    private String currentStatus;
    private Integer movementCount;
    private LocalDateTime lastMovementDate;
    private String lastAction;
    private String lastUserName;

    public ProcedureRecentActivity() {
    }

    public Integer getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Integer procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(String procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Integer getMovementCount() {
        return movementCount;
    }

    public void setMovementCount(Integer movementCount) {
        this.movementCount = movementCount;
    }

    public LocalDateTime getLastMovementDate() {
        return lastMovementDate;
    }

    public void setLastMovementDate(LocalDateTime lastMovementDate) {
        this.lastMovementDate = lastMovementDate;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getLastUserName() {
        return lastUserName;
    }

    public void setLastUserName(String lastUserName) {
        this.lastUserName = lastUserName;
    }
}
