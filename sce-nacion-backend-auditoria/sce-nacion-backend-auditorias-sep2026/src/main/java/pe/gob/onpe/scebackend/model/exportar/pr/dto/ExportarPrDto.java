package pe.gob.onpe.scebackend.model.exportar.pr.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.*;

@Data
public class ExportarPrDto {

	private List<ProcesoElectoralExportDto> proceso;
	private List<EleccionExportDto> eleccion;
	private List<UbigeoExportDto>	ubigeo;
	private List<LocalVotacionExportDto> localVotacion;
	private List<AgrupacionPoliticaExportDto> agrupacionPolitica;
	private List<UbigeoEleccionDto>	ubigeoEleccion;
	private List<DetUbigeoEleccionAgrupacionPoliticaExportDto> ubigeoEleccionAgrupacionPolitica;
	private List<DistritoElectoralExportDto> distritoElectoral;
	private List<CandidatoExportDto> candidato;
	private List<CatalogoExportDto> catalogo;
	private List<AdmDetCatalogoEstructuraExportDto> catalogoEstructura;
	private List<DetCatalogoReferenciaExportDto> catalogoReferencia;
	private Map<String, List<VwPrEleccionExportDto>> vistasEleccion;
	private List<VwPrResumenExportDto> vistaResumen;
	private List<VwPrActaExportDto> vistaActa;
	private List<VwPrParticipacionCiudadanaExportDto> vistaParticipacionCiudadano;
	private List<VwPrMesaExportDto> vistaMesa;
	private List<VwPrTotalCandidatosPorAgrupacionPoliticaExportDto> vistaTotalCandidatosPorAgrupacionPoliticaExportDto;
	
	
}
