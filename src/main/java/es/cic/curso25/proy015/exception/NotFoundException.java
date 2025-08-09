package es.cic.curso25.proy015.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String mensaje){
        super(mensaje);
    }
    public NotFoundException(String mensaje, Throwable error){
        super(mensaje, error);
    }
}
