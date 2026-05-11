package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatoDto {

    private Integer id;
    private Integer idDistritoElectoral;
    private Long idEleccion;
    private String codigoEleccion;
    private Long idAgrupacionPolitica;
    private Long idUbigeo;
    private Integer cargo;
    private String documentoIdentidad;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private Integer sexo;
    private Integer estado;
    private Integer lista;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
