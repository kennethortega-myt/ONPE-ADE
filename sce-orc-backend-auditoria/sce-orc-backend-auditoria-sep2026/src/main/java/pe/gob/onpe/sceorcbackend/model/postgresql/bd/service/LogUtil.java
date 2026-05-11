package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

public interface LogUtil {
    void iniciarPropiedadesLog(String mesa, Long acta);
    void limpiarPropiedadesLog();

}
