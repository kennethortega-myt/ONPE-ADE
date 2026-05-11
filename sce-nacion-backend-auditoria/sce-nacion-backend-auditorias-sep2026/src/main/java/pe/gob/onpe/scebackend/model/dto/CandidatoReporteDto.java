package pe.gob.onpe.scebackend.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CandidatoReporteDto {

	private String departamento;
    private String provincia;
    private String distrito;
    private String ubigeo;
    private String agrupacionPolitica;
    private String eleccion;
    private Integer codigoEleccion;
    private String cargo;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCandidato;
    private Integer numeroOrden;
    
}
