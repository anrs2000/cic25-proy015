package es.cic.curso25.proy015.exception;

public class maxVehiculosException extends RuntimeException{
    maxVehiculosException(String mensaje){
        super(mensaje);
    }
    maxVehiculosException(String mensaje, Throwable error){
        super(mensaje, error);
    }
}
