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
public class MonitoreoNacionBusquedaDto {

	private Long idProceso;
	private Long idEleccion;
	private Long idDepartamento;
	private Long idProvincia;
	private Long idUbigeo;
	private String mesa;
	private Long idLocal;
	private String grupoActa;
	private Integer cantidadPorpagina;
	private Integer pageIndex;
	
}
