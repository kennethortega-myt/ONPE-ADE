package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmDetTipoEleccionDocElectoralHistDto {

	private Long id;
	private Long idProcesoElectoral;
	private Long idEleccion;
	private Long idDocumentoElectoral;
	private String 	rangoInicial;
    private String 	rangoFinal;
    private String 	digitoChequeo;
    private String 	digitoError;
	private Long correlativo;
	private Integer requerido;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;

}
