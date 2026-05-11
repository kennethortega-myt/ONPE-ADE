package pe.gob.onpe.sceorcbackend.model.importar.dto;


import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmDetConfigDocElectoralHistDto {

    private Long 	id;
	private Long    idDetalleTipoEleccionDocumentoElectoral;
	private Long 	idSeccion;
	private Integer tipoDato;
	private Integer habilitado;
	private Integer correlativo;
	private BigDecimal 	pixelTopX;
    private BigDecimal 	pixelTopY;
    private BigDecimal 	pixelBottomX;
    private BigDecimal 	pixelBottomY;
    private BigDecimal 	coordenadaRelativaTopX;
    private BigDecimal 	coordenadaRelativaTopY;
    private BigDecimal 	coordenadaRelativaBottomX;
    private BigDecimal 	coordenadaRelativaBottomY;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;

}
