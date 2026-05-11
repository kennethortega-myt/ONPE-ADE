package pe.gob.onpe.scebackend.model.orc.projections.reporte;

public interface ReporteMesasSinOmisosProjection {
    String getCodigoUbigeo();
    String getCodigoODPE();
    String getCodigoCentroComputo();
    String getDepartamento();
    String getProvincia();
    String getDistrito();
    String getNumeroMesa();
    Integer getTotalMesas();
}