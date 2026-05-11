package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeguimientoOficioDTO implements Serializable {

    private static final long serialVersionUID = -5578091677307991840L;

    private Long idOficio;
    private String numeroficio;

    private Long actaPlomaId;
    private String idArchivoEscrutinio;
    private String idArchivoInstalacionSufragio;
    private String idArchivoEscrutinioFirmado;
    private String idArchivoInstalacionFirmado;
    private String idArchivoSufragioFirmado;

    private String numeroActaPloma;

    private Long actaCelesteId;
    private String numeroActaCeleste;

    private String eleccion;

    private Date fechaEnvio;
    private Date fechaRespuesta;

    private Long idResolucion;
    private String numeroResolucion;
    private String numeroExpediente;
    private String archivoJNE;

    private String estadoOficio;
    private boolean staeIntegrada;
}
