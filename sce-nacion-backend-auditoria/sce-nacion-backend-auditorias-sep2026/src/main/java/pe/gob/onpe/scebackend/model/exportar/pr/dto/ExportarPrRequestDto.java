package pe.gob.onpe.scebackend.model.exportar.pr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportarPrRequestDto {

	private String acronimoProceso;
	private Integer getBd;
	private Integer getVistaEleccion;
	private Integer getVistaResumen;
	private Integer getVistaActa;
	private Integer getVistaParticipacionCiudadana;
	private Integer getVistaMesa;
	private Integer getVistaTotalCandidatosPorAgrupacionPolitica;

	
}
