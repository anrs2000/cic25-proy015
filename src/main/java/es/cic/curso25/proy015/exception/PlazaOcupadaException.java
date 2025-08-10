package es.cic.curso25.proy015.exception;

public class PlazaOcupadaException extends RuntimeException{
    public PlazaOcupadaException(String mensaje){
        super(mensaje);
    }
    public PlazaOcupadaException(String mensaje, Throwable error){
        super(mensaje, error);
    }
}
