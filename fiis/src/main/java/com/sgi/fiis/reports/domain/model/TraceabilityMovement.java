package com.sgi.fiis.reports.domain.model;

import java.time.LocalDateTime;

/**
 * Output DTO for the chronological traceability of a procedure.
 * Maps the movements table (movimientos_tramite) with details of the user who acted.
 */
public class TraceabilityMovement {

    private Integer       movementId;
    private Integer       procedureId;
    private String        procedureCode;
    private String        actionUserName;
    private String        actionUserRole;
    private String        action;
    private String        previousStatus;
    private String        newStatus;
    private String        observation;
    private LocalDateTime movementDate;
    private String        ipOrigen;

    public TraceabilityMovement() {
    }

    public Integer       getMovementId()            { return movementId; }
    public void          setMovementId(Integer v)    { this.movementId = v; }

    public Integer       getProcedureId()            { return procedureId; }
    public void          setProcedureId(Integer v)    { this.procedureId = v; }

    public String        getProcedureCode()            { return procedureCode; }
    public void          setProcedureCode(String v)    { this.procedureCode = v; }

    public String        getActionUserName()            { return actionUserName; }
    public void          setActionUserName(String v)    { this.actionUserName = v; }

    public String        getActionUserRole()            { return actionUserRole; }
    public void          setActionUserRole(String v)    { this.actionUserRole = v; }

    public String        getAction()            { return action; }
    public void          setAction(String v)    { this.action = v; }

    public String        getPreviousStatus()            { return previousStatus; }
    public void          setPreviousStatus(String v)    { this.previousStatus = v; }

    public String        getNewStatus()            { return newStatus; }
    public void          setNewStatus(String v)    { this.newStatus = v; }

    public String        getObservation()            { return observation; }
    public void          setObservation(String v)    { this.observation = v; }

    public LocalDateTime getMovementDate()              { return movementDate; }
    public void          setMovementDate(LocalDateTime v){ this.movementDate = v; }

    public String        getIpOrigen()            { return ipOrigen; }
    public void          setIpOrigen(String v)    { this.ipOrigen = v; }
}
