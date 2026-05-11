package pe.gob.onpe.scebackend.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutoridadReporteDto {

	private String departamento;
    private String provincia;
    private String distrito;
    private String ubigeo;
    private String eleccion;
    private Integer codigoEleccion;
    private String cargo;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCandidato;
}
