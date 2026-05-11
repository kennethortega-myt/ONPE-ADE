package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListaParticipantesResponseDto extends ReporteBaseDto{

	private int numFila;
	private String codDescODPE;
	private String codDescCC;
	private String numMesa;
	private String descDepartamento;
	private String descDistrito;
	private String descProvincia;
	private String codUbigeo;
	private int eleHabil;
	private String numEle;
	private String votante;
	private String estado;
	private Integer partiparon;
	private Integer omitieron;
}
