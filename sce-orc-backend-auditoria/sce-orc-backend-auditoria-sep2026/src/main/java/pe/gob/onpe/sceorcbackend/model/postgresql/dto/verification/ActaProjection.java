package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

public interface ActaProjection {
    Long getId();
    String getVerificador();
    String getVerificador2();
    String getUsuarioCorreccion();
    String getUsuarioProcesamientoManual();
    String getUsuarioAsignado();
    String getUsuarioControlCalidad();
}
