package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;

@Data
public class ReporteUsuarioJasperDto {

    private String usuario;
    private String documento;
    private String apellidos;
    private String perfil;
    private String correo;
    private String asignado;
    private String activo;
}
