package pe.gob.onpe.sceorcbackend.model.postgresql.projection;

import java.util.Date;

public interface ReporteTransaccionesRealizadasProjection {
    String getCentroComputo();
    String getNombreCentroComputo();
    Date getFechaMovimiento();
    String getUsuario();
    String getApellidosNombresUsuario();
    String getMovimiento();
}