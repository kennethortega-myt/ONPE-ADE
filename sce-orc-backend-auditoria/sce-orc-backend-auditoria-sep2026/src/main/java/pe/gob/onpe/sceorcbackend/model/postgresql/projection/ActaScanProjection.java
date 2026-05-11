package pe.gob.onpe.sceorcbackend.model.postgresql.projection;

import java.util.Date;

public interface ActaScanProjection {
    Long getIdActa();
    String getNombreEleccion();
    String getCodigoEleccion();
    String getMesa();
    String getCopia();
    String getDigitoChequeoEscrutinio();
    String getEstadoActa();
    String getEstadoComputo();
    String getEstadoDigitalizacion();
    String getArchivoEscrutinio();
    String getArchivoInstalacion();
    String getArchivoSufragio();
    String getArchivoInstalacionSufragio();
    Integer getActivo();
    Long getSolucionTecnologica();
    Integer getTipoTransmision();
    Date getFechaModificacion();
}