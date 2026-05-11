package pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion;

public interface TabResolucionProjection {
    Long getId();
    String getNumeroResolucion();
    String getEstadoResolucion();
    String getEstadoDigitalizacion();
}
