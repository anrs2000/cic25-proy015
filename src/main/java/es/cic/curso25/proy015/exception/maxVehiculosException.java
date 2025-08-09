package es.cic.curso25.proy015.exception;

public class maxVehiculosException extends RuntimeException{
    public maxVehiculosException(String mensaje){
        super(mensaje);
    }
    public maxVehiculosException(String mensaje, Throwable error){
        super(mensaje, error);
    }
}
