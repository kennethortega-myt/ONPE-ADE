package pe.gob.onpe.sceorcbackend.model.postgresql.util;


import lombok.Data;

@Data
public class ModeloItem {

    private String votos1;
    private Long idArchivo1;

    private String votos2;
    private Long idArchivo2;

    private String votos3;
    private Long idArchivo3;

    private String votos4;
    private Long idArchivo4;

    private String votos5;
    private Long idArchivo5;

    private String votos6;
    private Long idArchivo6;


    public ModeloItem() {


    }

    public ModeloItem(String votos1, Long idArchivo1,
                      String votos2, Long idArchivo2,
                      String votos3, Long idArchivo3,
                      String votos4, Long idArchivo4,
                      String votos5, Long idArchivo5,
                      String votos6, Long idArchivo6) {
        this.votos1 = votos1;
        this.idArchivo1 = idArchivo1;
        this.votos2 = votos2;
        this.idArchivo2 = idArchivo2;
        this.votos3 = votos3;
        this.idArchivo3 = idArchivo3;
        this.votos4 = votos4;
        this.idArchivo4 = idArchivo4;
        this.votos5 = votos5;
        this.idArchivo5 = idArchivo5;
        this.votos6 = votos6;
        this.idArchivo6 = idArchivo6;
    }
}