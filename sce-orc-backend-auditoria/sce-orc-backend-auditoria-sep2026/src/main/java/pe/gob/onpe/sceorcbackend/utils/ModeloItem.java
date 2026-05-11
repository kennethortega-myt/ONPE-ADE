package pe.gob.onpe.sceorcbackend.utils;

import lombok.Data;

@Data
public class ModeloItem {
    private String votos;
    private String idArchivo;

    public ModeloItem() {


    }

    public ModeloItem(String votos, String idArchivo) {
        this.votos = votos;
        this.idArchivo = idArchivo;
    }
}
