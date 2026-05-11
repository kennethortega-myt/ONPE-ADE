package pe.gob.onpe.sceorcbackend.exception;

import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

public class SasaTimeoutException extends RuntimeException {

    public SasaTimeoutException(String message) {
        super(message);
    }

    public SasaTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public SasaTimeoutException(Throwable cause) {
        super(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, cause);
    }

}
