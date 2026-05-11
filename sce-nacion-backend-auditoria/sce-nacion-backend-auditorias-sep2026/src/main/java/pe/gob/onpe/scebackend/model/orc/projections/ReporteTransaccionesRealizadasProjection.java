package pe.gob.onpe.scebackend.model.orc.projections;


import java.util.Date;

public interface ReporteTransaccionesRealizadasProjection {
    String getCentroComputo();
    String getNombreCentroComputo();
    Date getFechaMovimiento();
    String getUsuario();
    String getApellidosNombresUsuario();
    String getMovimiento();
}