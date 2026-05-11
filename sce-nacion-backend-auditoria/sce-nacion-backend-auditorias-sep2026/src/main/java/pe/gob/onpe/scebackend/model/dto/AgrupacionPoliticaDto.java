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
public class AgrupacionPoliticaDto extends DatosAuditoriaDto{

	private Long   	id;
	private String  codigo;
	private String  descripcion;
	private Long	tipoAgrupacionPolitica;
	private Integer estado;
	private String  ubigeoMaximo;
	private Integer activo;

	
}
