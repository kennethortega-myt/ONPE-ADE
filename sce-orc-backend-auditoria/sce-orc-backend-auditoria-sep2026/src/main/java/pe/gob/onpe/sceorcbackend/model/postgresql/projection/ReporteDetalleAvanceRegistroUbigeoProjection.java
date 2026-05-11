package pe.gob.onpe.sceorcbackend.model.postgresql.projection;

public interface ReporteDetalleAvanceRegistroUbigeoProjection {
    String getCodigoAmbitoElectoral();
    String getCodigoCentroComputo();
    String getCodigoUbigeo();
    String getDepartamento();
    String getProvincia();
    String getDistrito();
    String getProcesada();
    String getMesa();
    Integer getTotalMesasProcesadas();
    Integer getTotalMesas();
    Integer getTotalMesaSinProcesar();
    Double getPorcentajeAvance();
}