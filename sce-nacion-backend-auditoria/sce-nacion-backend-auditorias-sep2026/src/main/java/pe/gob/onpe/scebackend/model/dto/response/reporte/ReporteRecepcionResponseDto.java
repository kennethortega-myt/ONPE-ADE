package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteRecepcionResponseDto {

	private Integer presidencialRecibida;
	private Integer parlamentoRecibida;
	private Integer diputadoRecibida;
	private Integer multipleRecibida;
	private Integer unicoRecibida;
	
	private Integer presidencialTotal;
	private Integer parlamentoTotal;
	private Integer diputadoTotal;
	private Integer multipleTotal;
	private Integer unicoTotal;	
	
	private String fe;
	private Integer pc;
	private String descCompu;
	
	private Integer rele;
	private Integer rha;
	private Integer rper;
	private Integer rme;
	
	private Integer re;
	private Integer ia;
	private Integer ir;
}
