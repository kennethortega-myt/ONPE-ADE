package pe.gob.onpe.scebackend.model.exportar.orc.dto;



import lombok.Data;


@Data
public class ArchivoExportDto {

    private Integer id;

    private String guid;


    private String nombre;

   
    private String nombreOriginal;


    private String formato;


    private String peso;


    private String ruta;


    private Integer activo;
    
    private String usuario;
}
