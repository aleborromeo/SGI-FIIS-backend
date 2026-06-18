package pe.unas.fiis.sgifiis.thesis.domain.exception;

public class PlanTesisNoEncontradoException extends RuntimeException {
    public PlanTesisNoEncontradoException(Integer id) { super("No existe el plan de tesis con id: " + id); }
}
