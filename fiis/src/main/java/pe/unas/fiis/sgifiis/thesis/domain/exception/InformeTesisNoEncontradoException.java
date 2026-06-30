package pe.unas.fiis.sgifiis.thesis.domain.exception;

public class InformeTesisNoEncontradoException extends RuntimeException {
    public InformeTesisNoEncontradoException(Integer id) { super("No existe el informe de tesis con id: " + id); }
}
