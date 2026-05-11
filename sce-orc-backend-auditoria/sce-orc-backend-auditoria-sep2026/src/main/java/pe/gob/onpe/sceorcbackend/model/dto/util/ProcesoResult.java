package pe.gob.onpe.sceorcbackend.model.dto.util;

import lombok.Data;


@Data
public class ProcesoResult <T,S> {
    private boolean observaciones;
    private String mensajeObservacion;
    private T data;
    private S data2;

    public ProcesoResult() {
    }

    public ProcesoResult(boolean observaciones, String mensajeObservacion) {
        this.observaciones = observaciones;
        this.mensajeObservacion = mensajeObservacion;
    }

    public ProcesoResult(boolean observaciones, String mensajeObservacion, T data) {
        this.observaciones = observaciones;
        this.mensajeObservacion = mensajeObservacion;
        this.data = data;
    }

    public ProcesoResult(boolean observaciones, String mensajeObservacion, T data, S data2) {
        this.observaciones = observaciones;
        this.mensajeObservacion = mensajeObservacion;
        this.data = data;
        this.data2= data2;
    }




}
