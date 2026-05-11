package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MaePadronDto {
    private Long id;
    private String codigoMesa;
    private String documentoIdentidad;
    private Long idMesa;
    private Integer idTipoDocumentoIdentidad;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer orden;
    private String ubigeo;
    private String ubigeoReniec;
    private Integer sexo;
    private Integer vd;
    private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
 
}
