package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProbableCandidatoElecto {

	private String distritoElecDesc;
	private String agrupolDesc;
	private String votosAgrupol;
	private String escanosObtenidos;
	private String nombreElecto;
	private String numeroDni;
	private String votosValidos;
	private String ordenCandidato;
	private String ordenObtenido;
	private String estadoCandidato;
	private String observacion;
	private String cargoCandidato;
	
}
