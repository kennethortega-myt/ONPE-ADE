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
public class TransmisionRecepcionExportDto {

	private Long	id;
	private String  proceso;
	private Long	idCentroComputo;
	private String	tramaDato;
	private String	tramaImagen;
	private Integer estado;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioProcesamiento;
	private String	audFechaProcesamiento;
	
}
