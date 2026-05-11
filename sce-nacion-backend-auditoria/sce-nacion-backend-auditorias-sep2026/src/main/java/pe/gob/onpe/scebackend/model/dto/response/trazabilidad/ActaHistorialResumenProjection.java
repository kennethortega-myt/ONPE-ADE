package pe.gob.onpe.scebackend.model.dto.response.trazabilidad;

import java.time.LocalDateTime;

public interface ActaHistorialResumenProjection {
    Long getId();
    String getEstadoDigitalizacion();
    String getEstadoActa();
    String getEstadoCc();
    String getEstadoActaResolucion();
    String getEstadoErrorMaterial();
    String getVerificador();
    String getVerificadorv2();
    LocalDateTime getFechaModificacion();
    String getUsuarioModificacion();
}