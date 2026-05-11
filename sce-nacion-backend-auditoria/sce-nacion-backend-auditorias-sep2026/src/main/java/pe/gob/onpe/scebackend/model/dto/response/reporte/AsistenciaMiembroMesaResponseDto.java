package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsistenciaMiembroMesaResponseDto {

	private String codDescODPE;
	private String codDescCC;
	private String numMesaMadre;
	private String descDepartamento;
	private String descDistrito;
	private String descProvincia;
	private String codUbigeo;
	private Integer eleHabil;
	private String numEle;
	private String votante;
	private String descargo;
	private String numero;
	private String asistencia;
	private Integer tipoMiembro;
}
