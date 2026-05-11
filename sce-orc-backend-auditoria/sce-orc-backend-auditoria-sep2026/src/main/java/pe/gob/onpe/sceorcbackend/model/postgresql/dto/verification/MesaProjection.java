package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

public interface MesaProjection {
    Long getId();
    String getUsuarioControlLe();
    String getUsuarioControlMm();
    String getUsuarioAsignadoLe();
    String getUsuarioAsignadoMm();
}
