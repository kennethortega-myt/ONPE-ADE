package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetUbigeoEleccionAgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.EleccionViewExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.ExportarPrDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.ExportarPrRequestDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.LocalVotacionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.CatalogoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.DetCatalogoReferenciaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoEleccionDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrMesaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrParticipacionCiudadanaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrResumenExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrTotalCandidatosPorAgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IAgrupacionPoliticaExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.ICandidatoExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IDetUbigeoEleccionAgrupacionPoliticaExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IDistritoElectoralExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IEleccionExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IExportarPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.ILocalVotacionExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IOrcCatalogoExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IOrcDetCatalogoEstructuraExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IOrcDetCatalogoReferenciaExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IUbigeoEleccionExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IUbigeoExportPrService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrActaService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrMesaService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrParticipacionCiudadanaService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrResumenService;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IVwPrTotalCandidatosPorAgrupacionPoliticaService;

@Service
public class ExportarPrService implements IExportarPrService {

	@Autowired
	private IEleccionExportPrService eleccionService;
	
	@Autowired
	private IAgrupacionPoliticaExportPrService agrupacionPoliticaService;
	
	@Autowired
	private IUbigeoExportPrService ubigeoService;
	
	@Autowired
	private IUbigeoEleccionExportPrService ubigeoEleccionService;
	
	@Autowired
	private IDetUbigeoEleccionAgrupacionPoliticaExportPrService detUbigeoEleccionAgrupacionPoliticaExportPrService;
	
	@Autowired
	private IOrcCatalogoExportPrService orcCatalogoExportPrService;
	
	@Autowired
	private IOrcDetCatalogoEstructuraExportPrService orcDetCatalogoEstructuraExportPrService;
	
	@Autowired
	private IOrcDetCatalogoReferenciaExportPrService orcDetCatalogoReferenciaExportPrService;
	
	@Autowired
	private IVwPrResumenService vwPrResumenService;
	
	@Autowired
	private IVwPrActaService vwPrActaService;
	
	@Autowired
	private ProcesoElectoralExportPrService procesoElectoralExportPrService;
	
	@Autowired
	private IVwPrParticipacionCiudadanaService vwPrParticipacionCiudadana;
	
	@Autowired
	private ILocalVotacionExportPrService localVotacionExportPrService;
	
	@Autowired
	private IDistritoElectoralExportPrService distritoElectoralExportPrService; 
	
	@Autowired
	private ICandidatoExportPrService candidatoExportPrService;
	
	@Autowired
	private IVwPrMesaService vwPrMesaService;
	
	@Autowired
	private IVwPrTotalCandidatosPorAgrupacionPoliticaService vwPrTotalCandidatosPorAgrupacionPoliticaService;
	
	@Override
	@Transactional("locationTransactionManager")
	public ExportarPrDto exportar(ExportarPrRequestDto request) {
		

		ExportarPrDto export = new ExportarPrDto();
		
		List<ProcesoElectoralExportDto> proceso = null;
		List<UbigeoExportDto>	ubigeo = null;
		List<AgrupacionPoliticaExportDto> agrupacionPolitica = null;
		List<UbigeoEleccionDto>	ubigeoEleccion = null;
		List<DetUbigeoEleccionAgrupacionPoliticaExportDto> ubigeoEleccionAgrupacionPolitica = null;
		List<CatalogoExportDto> catalogo = null;
		List<AdmDetCatalogoEstructuraExportDto> catalogoEstructura = null;
		List<DetCatalogoReferenciaExportDto> catalogoReferencia = null;
		List<LocalVotacionExportDto> localVotacion = null;
		List<DistritoElectoralExportDto> distritoElectoral = null;
		List<CandidatoExportDto> candidato = null;
		
		if(request.getGetBd()!=null && request.getGetBd().equals(1)) {
			proceso = this.procesoElectoralExportPrService.findAll();
			ubigeo = this.ubigeoService.findAll();
			agrupacionPolitica = this.agrupacionPoliticaService.findAll();
			ubigeoEleccion = this.ubigeoEleccionService.findAll();
			ubigeoEleccionAgrupacionPolitica = this.detUbigeoEleccionAgrupacionPoliticaExportPrService.findAll();
			catalogo = this.orcCatalogoExportPrService.findAll();
			catalogoEstructura = this.orcDetCatalogoEstructuraExportPrService.findAll();
			catalogoReferencia = this.orcDetCatalogoReferenciaExportPrService.findAll();
			localVotacion = this.localVotacionExportPrService.findAll();
			distritoElectoral = this.distritoElectoralExportPrService.findAll();
			candidato = this.candidatoExportPrService.findAll();
		}
		
		
		List<VwPrResumenExportDto> vistaResumen = null;
		List<VwPrActaExportDto> vistaActa = null;
		List<VwPrParticipacionCiudadanaExportDto> vistaParticipacionCiudadana = null;
		List<VwPrMesaExportDto> vistaMesa = null;
		EleccionViewExportDto eleccionResult = null;
		List<VwPrTotalCandidatosPorAgrupacionPoliticaExportDto> vistaTotalCandidatosPorAgrupacionPolitica = null;
		
		if(request.getGetVistaResumen()!=null && request.getGetVistaResumen().equals(1)) {
			vistaResumen = this.vwPrResumenService.findAll();
		}
		
		if(request.getGetVistaActa()!=null && request.getGetVistaActa().equals(1)) {
			vistaActa = this.vwPrActaService.findAll();
		}
		
		if(request.getGetVistaParticipacionCiudadana()!=null && request.getGetVistaParticipacionCiudadana().equals(1)) {
			vistaParticipacionCiudadana = this.vwPrParticipacionCiudadana.findAll();
		}
		
		if(request.getGetVistaEleccion()!=null && request.getGetVistaEleccion().equals(1)) {
			eleccionResult = this.eleccionService.findAll(1);
		}
		
		if(request.getGetVistaMesa()!=null && request.getGetVistaMesa().equals(1)) {
			vistaMesa = this.vwPrMesaService.findAll();
		}
		
		if(request.getGetVistaTotalCandidatosPorAgrupacionPolitica()!=null && request.getGetVistaTotalCandidatosPorAgrupacionPolitica().equals(1)) {
			vistaTotalCandidatosPorAgrupacionPolitica = this.vwPrTotalCandidatosPorAgrupacionPoliticaService.findAll();
		}
		
		// map to DTO
		
		export.setProceso(proceso);
		
	
		if(eleccionResult!=null) {
			export.setEleccion(eleccionResult.getElecciones());
			export.setVistasEleccion(eleccionResult.getVistas());
		}
		
		
		
		export.setUbigeo(ubigeo);
		export.setAgrupacionPolitica(agrupacionPolitica);
		export.setUbigeoEleccion(ubigeoEleccion);
		export.setLocalVotacion(localVotacion);
		export.setUbigeoEleccionAgrupacionPolitica(ubigeoEleccionAgrupacionPolitica);
		export.setCatalogo(catalogo);
		export.setCatalogoEstructura(catalogoEstructura);
		export.setCatalogoReferencia(catalogoReferencia);
		export.setVistaResumen(vistaResumen);
		export.setVistaActa(vistaActa);
		export.setVistaParticipacionCiudadano(vistaParticipacionCiudadana);
		export.setVistaMesa(vistaMesa);
		export.setDistritoElectoral(distritoElectoral);
		export.setCandidato(candidato);
		export.setVistaTotalCandidatosPorAgrupacionPoliticaExportDto(vistaTotalCandidatosPorAgrupacionPolitica);
		
		return export;
	}

}
