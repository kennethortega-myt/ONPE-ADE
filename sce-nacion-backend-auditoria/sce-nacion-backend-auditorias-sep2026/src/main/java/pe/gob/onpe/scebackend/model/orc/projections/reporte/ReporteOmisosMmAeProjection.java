package pe.gob.onpe.scebackend.model.orc.projections.reporte;

public interface ReporteOmisosMmAeProjection {
    String getCodigoUbigeo();
    String getCodigoCentroComputo();
    String getDepartamento();
    String getProvincia();
    String getDistrito();
    Integer getTotalMesas();
    Integer getTotalElectores();
    Integer getTotalMesasRegistradas();
}
