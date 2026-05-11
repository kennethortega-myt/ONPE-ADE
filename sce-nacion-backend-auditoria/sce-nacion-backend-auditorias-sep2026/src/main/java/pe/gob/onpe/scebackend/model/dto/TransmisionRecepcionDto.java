package pe.gob.onpe.scebackend.model.dto;

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
public class TransmisionRecepcionDto {

	private Long	id;
	private String  proceso;
	private Long	idCentroComputo;
	private Long idActa;
	private String	tramaDato;
	private String	tramaImagen;
	private Integer estado;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioProcesamiento;
	private String	audFechaProcesamiento;
	
}
