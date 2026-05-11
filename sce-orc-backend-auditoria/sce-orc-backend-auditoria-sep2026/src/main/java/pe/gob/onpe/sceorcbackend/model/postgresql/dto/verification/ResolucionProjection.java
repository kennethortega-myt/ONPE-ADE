package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

public interface ResolucionProjection {

    Long getId();
    String getNumeroResolucion();
    String getUsuarioControl();
    String getAudUsuarioAsignado();

}
