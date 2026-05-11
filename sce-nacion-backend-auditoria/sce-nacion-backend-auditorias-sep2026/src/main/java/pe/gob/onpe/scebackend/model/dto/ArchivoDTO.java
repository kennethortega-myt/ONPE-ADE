package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;


@Data
public class ArchivoDTO {

    private Long id;

    private String guid;


    private String nombre;

   
    private String nombreOriginal;


    private String formato;


    private String peso;


    private String ruta;


    private Integer activo;
    
    private String usuario;
}
