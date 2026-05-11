package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idLocalVotacion;
    private String codigo;
    private Integer cantidadElectoresHabiles;
    private Integer cantidadElectoresHabilesExtranjeros;
    private Integer discapacidad;
    private Long solucionTecnologica;
    private String estadoMesa;
    private String estadoDigitalizacionLe;
    private String estadoDigitalizacionMm;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
