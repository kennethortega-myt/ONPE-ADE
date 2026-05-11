package pe.gob.onpe.sceorcbackend.model.postgresql.projection;

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
