package pe.gob.onpe.scebackend.model.exportar.orc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrcDetConfigDocElectoralExportDto {

	private Long 	id;
	private Long    idDetalleTipoEleccionDocumentoElectoral;
	private Long 	idSeccion;
	private Integer tipoDato;
	private Integer habilitado;
	private Integer correlativo;
	private Double 	pixelTopX;
    private Double 	pixelTopY;
    private Double 	pixelBottomX;
    private Double 	pixelBottomY;
    private Double 	coordenadaRelativaTopX;
    private Double 	coordenadaRelativaTopY;
    private Double 	coordenadaRelativaBottomX;
    private Double 	coordenadaRelativaBottomY;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
