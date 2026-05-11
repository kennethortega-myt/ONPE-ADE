package pe.gob.onpe.scebackend.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitoreoListActaItem {
    private Long actaId;
    private String grupoActa;
    private String nroActa;
    private String mesa;
    private String estado;
    private String fecha;
    private Integer verActa;
    private String imagenEscrutinio;
    private String imagenInstalacion;
    private String imagenSufragio;
    private String imagenInstalacionSufragio;
    private String estadoDescripcion;
    private Long nro;
}
