package pe.gob.onpe.sceorcbackend.utils;

import lombok.Data;

@Data
public class ActaDTO {
    private Long id;
    private String estadoActa;
    private String estadoCc;
    private String codigoEleccion;

    private String estadoDigitalizacion;
    private String codigo;

    public ActaDTO(Long id, String estadoActa, String codigoEleccion) {
        this.id = id;
        this.estadoActa = estadoActa;
        this.codigoEleccion = codigoEleccion;
    }

    public ActaDTO(String estadoActa, String estadoCc, String estadoDigitalizacion, String codigo) {
        this.estadoActa = estadoActa;
        this.estadoCc = estadoCc;
        this.estadoDigitalizacion = estadoDigitalizacion;
        this.codigo = codigo;
    }
}
