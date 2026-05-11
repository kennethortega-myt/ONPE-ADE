package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaFicticioExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaRealExportDto;
import pe.gob.onpe.scebackend.model.dto.AmbitoElectoralDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoFicticioExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoRealExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CentroComputoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaFormatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaOpcionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaFormatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabParametroExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaPreferencialExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetDistritoElectoralEleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetParametroExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetUbigeoEleccionAgrupacionPoliticaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.EleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportOrcDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.FormatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.LocalVotacionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.MiembroMesaSorteadoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OpcionVotoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcCatalogoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDetCatalogoReferenciaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDetConfigDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDetTipoEleccionDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDocElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcSeccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PuestaCeroExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.TabJuradoElectoralEspecialExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoEleccionDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.UsuarioExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionModeloExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IExportarOrcService;

@Service
public class ExportarOrcService implements IExportarOrcService {

	private AgrupacionPoliticaExportService agrupacionPoliticaService;
	
	private AgrupacionPoliticaFicticioExportService agrupacionPoliticaFicticioService;
	
	private AgrupacionPoliticaRealExportService agrupacionPoliticaRealService;

	private ActaExportService actaService;

	private DetActaExportService detActaService;
	
	private DetActaPreferencialExportService detActaPreferencialService;

	private AmbitoElectoralExportService ambitoElectoralService;

	private CentroComputoExportService centroComputoService;

	private DetUbigeoEleccionAgrupacionPoliticaExportService dtueapService;

	private EleccionExportService eleccionService;

	private LocalVotacionExportService localVotacionService;

	private MesaExportService mesaService;

	private ProcesoElectoralExportService procesoElectoralService;

	private UbigeoExportService ubigeoService;

	private UbigeoEleccionExportService ubigeEleccionService;

	private OrcCatalogoExportService catalogoService;

	private OrcDetCatalogoEstructuraExportService catalogoEstructuraService;

	private OrcDetCatalogoReferenciaExportService catalogoReferenciaService;

	private FormatoExportService formatoExportService;

	private DetActaFormatoExportService detActaFormatoExportService;

	private CandidatoExportService candidatoExportService;

	private CandidatoFicticioExportService candidatoFicticioExportService;

	private CandidatoRealExportService candidatoRealExportService;

	private DistritoElectoralExportService distritoElectoralExportService;

	private PuestaCeroExportService puestaCeroExportService;

	private VersionExportService versionService;

	private UsuarioExportService usuarioService;

	private MiembroMesaSorteadoExportService miembroMesaSorteadoExportService;

	private OrcDetalleConfiguracionDocumentoElectoralExportService detalleConfiguracionDocumentoElectoralExportService;

	private OrcDetalleTipoEleccionDocumentoElectoralExportService detalleTipoEleccionDocumentoElectoralExportService;

	private OrcDocumentoElectoralExportService documentoElectoralExportService;
	
	private OrcSeccionExportService seccionExportService;
	
	private CabActaFormatoExportService cabActaFormatoExportService;
	
	private DetActaOpcionExportService detActaOpcionExportService;
	
	private OpcionVotoExportService opcionVotoExportService;
	
	private CabParametroExportService cabParametroExportService;

	private DetParametroExportService detParametroExportService;
	
	private DetDistritoElectoralEleccionExportService detDistritoElectoralEleccionExportService;
	
	private JuradoElectoralEspecialExportService juradoElectoralEspecialExportService;
	
	private VersionModeloExportService versionModeloExportService;

	public ExportarOrcService(
			AgrupacionPoliticaExportService agrupacionPoliticaService,
			AgrupacionPoliticaFicticioExportService agrupacionPoliticaFicticioService,
			AgrupacionPoliticaRealExportService agrupacionPoliticaRealService,
			ActaExportService actaService,
			DetActaExportService detActaService,
			DetActaPreferencialExportService detActaPreferencialService,
			AmbitoElectoralExportService ambitoElectoralService,
			CentroComputoExportService centroComputoService,
			DetUbigeoEleccionAgrupacionPoliticaExportService dtueapService,
			EleccionExportService eleccionService,
			LocalVotacionExportService localVotacionService,
			MesaExportService mesaService,
			ProcesoElectoralExportService procesoElectoralService,
			UbigeoExportService ubigeoService,
			UbigeoEleccionExportService ubigeEleccionService,
			OrcCatalogoExportService catalogoService,
			OrcDetCatalogoEstructuraExportService catalogoEstructuraService,
			OrcDetCatalogoReferenciaExportService catalogoReferenciaService,
			FormatoExportService formatoExportService,
			DetActaFormatoExportService detActaFormatoExportService,
			CandidatoExportService candidatoExportService,
			CandidatoFicticioExportService candidatoFicticioExportService,
			CandidatoRealExportService candidatoRealExportService,
			DistritoElectoralExportService distritoElectoralExportService,
			PuestaCeroExportService puestaCeroExportService,
			VersionExportService versionService,
			UsuarioExportService usuarioService,
			MiembroMesaSorteadoExportService miembroMesaSorteadoExportService,
			OrcDetalleConfiguracionDocumentoElectoralExportService detalleConfiguracionDocumentoElectoralExportService,
			OrcDetalleTipoEleccionDocumentoElectoralExportService detalleTipoEleccionDocumentoElectoralExportService,
			OrcDocumentoElectoralExportService documentoElectoralExportService,
			OrcSeccionExportService seccionExportService,
			CabActaFormatoExportService cabActaFormatoExportService,
			DetActaOpcionExportService detActaOpcionExportService,
			OpcionVotoExportService opcionVotoExportService,
			CabParametroExportService cabParametroExportService,
			DetParametroExportService detParametroExportService,
			DetDistritoElectoralEleccionExportService detDistritoElectoralEleccionExportService,
			JuradoElectoralEspecialExportService juradoElectoralEspecialExportService,
			VersionModeloExportService versionModeloExportService) {

		this.agrupacionPoliticaService = agrupacionPoliticaService;
		this.agrupacionPoliticaFicticioService = agrupacionPoliticaFicticioService;
		this.agrupacionPoliticaRealService = agrupacionPoliticaRealService;
		this.actaService = actaService;
		this.detActaService = detActaService;
		this.detActaPreferencialService = detActaPreferencialService;
		this.ambitoElectoralService = ambitoElectoralService;
		this.centroComputoService = centroComputoService;
		this.dtueapService = dtueapService;
		this.eleccionService = eleccionService;
		this.localVotacionService = localVotacionService;
		this.mesaService = mesaService;
		this.procesoElectoralService = procesoElectoralService;
		this.ubigeoService = ubigeoService;
		this.ubigeEleccionService = ubigeEleccionService;
		this.catalogoService = catalogoService;
		this.catalogoEstructuraService = catalogoEstructuraService;
		this.catalogoReferenciaService = catalogoReferenciaService;
		this.formatoExportService = formatoExportService;
		this.detActaFormatoExportService = detActaFormatoExportService;
		this.candidatoExportService = candidatoExportService;
		this.candidatoFicticioExportService = candidatoFicticioExportService;
		this.candidatoRealExportService = candidatoRealExportService;
		this.distritoElectoralExportService = distritoElectoralExportService;
		this.puestaCeroExportService = puestaCeroExportService;
		this.versionService = versionService;
		this.usuarioService = usuarioService;
		this.miembroMesaSorteadoExportService = miembroMesaSorteadoExportService;
		this.detalleConfiguracionDocumentoElectoralExportService = detalleConfiguracionDocumentoElectoralExportService;
		this.detalleTipoEleccionDocumentoElectoralExportService = detalleTipoEleccionDocumentoElectoralExportService;
		this.documentoElectoralExportService = documentoElectoralExportService;
		this.seccionExportService = seccionExportService;
		this.cabActaFormatoExportService = cabActaFormatoExportService;
		this.detActaOpcionExportService = detActaOpcionExportService;
		this.opcionVotoExportService = opcionVotoExportService;
		this.cabParametroExportService = cabParametroExportService;
		this.detParametroExportService = detParametroExportService;
		this.detDistritoElectoralEleccionExportService = detDistritoElectoralEleccionExportService;
		this.juradoElectoralEspecialExportService = juradoElectoralEspecialExportService;
		this.versionModeloExportService = versionModeloExportService;
	}

	
	@Override
	@Transactional("locationTransactionManager")
	public ExportOrcDto exportar(String proceso, String idCc) {
		
		List<AgrupacionPoliticaExportDto> agrupacionesDto = this.agrupacionPoliticaService.findByCc(idCc);
		List<AgrupacionPoliticaFicticioExportDto> agrupacionesFicticiasDto = this.agrupacionPoliticaFicticioService.findByCc(idCc);
		List<AgrupacionPoliticaRealExportDto> agrupacionesRealesDto = this.agrupacionPoliticaRealService.findByCc(idCc);
		//List<CabActaExportDto> actasDto = this.actaService.findByCc(idCc);
		List<AmbitoElectoralDto> ambitoElectoralDto = this.ambitoElectoralService.findByCc(idCc);
		List<CentroComputoExportDto> ccDto = this.centroComputoService.findByCc(idCc);
		List<DetActaExportDto> detActaDto = this.detActaService.findByCc(idCc);
		List<DetActaPreferencialExportDto> detActaPreferencialDto = this.detActaPreferencialService.findByCc(idCc);
		List<DetUbigeoEleccionAgrupacionPoliticaExportDto> dtueapDto = this.dtueapService.findByCc(idCc);
		List<EleccionExportDto> eleccionesDto = this.eleccionService.findByCc(idCc);
		List<LocalVotacionExportDto> locales = this.localVotacionService.findByCc(idCc);
		//List<MesaExportDto> mesas = this.mesaService.findByCc(idCc);
		List<UbigeoExportDto> ubigeos = this.ubigeoService.findByCc(idCc);
		List<ProcesoElectoralExportDto> procesos = this.procesoElectoralService.findByCc(idCc);
		List<UbigeoEleccionDto> ueDtos = this.ubigeEleccionService.findByCc(idCc);
		List<OrcCatalogoExportDto> catalogosDto = this.catalogoService.findAll();
		List<AdmDetCatalogoEstructuraExportDto> estructurasDto = this.catalogoEstructuraService.findAll();
		List<OrcDetCatalogoReferenciaExportDto> referenciasDto = this.catalogoReferenciaService.findAll();
		List<FormatoExportDto> formatosDto = this.formatoExportService.findByCc(idCc);
		List<CabActaFormatoExportDto> cabActasFormatosDto = this.cabActaFormatoExportService.findByCc(idCc);
		List<DetActaFormatoExportDto> detActasFormatosDto = this.detActaFormatoExportService.findByCc(idCc);
		List<CandidatoExportDto> candidatoDto = this.candidatoExportService.findByCc(idCc);
		List<CandidatoFicticioExportDto> candidatoFicticioDto = this.candidatoFicticioExportService.findByCc(idCc);
		List<CandidatoRealExportDto> candidatoRealDto = this.candidatoRealExportService.findByCc(idCc);
		List<DistritoElectoralExportDto> distritoElectoralDto = this.distritoElectoralExportService.findByCc(idCc);
		List<PuestaCeroExportDto> puestaCeroDto = this.puestaCeroExportService.findByCc(idCc);
		List<VersionExportDto> versionDto = this.versionService.findAll();
		List<UsuarioExportDto> usuarioDto = this.usuarioService.findByCc(idCc);
		List<MiembroMesaSorteadoExportDto> miembrosSorteado = this.miembroMesaSorteadoExportService.findByCc(idCc);
		List<DetActaOpcionExportDto> detActaOpcion = this.detActaOpcionExportService.findByCc(idCc);
		List<CabParametroExportDto> cabParametro = this.cabParametroExportService.findAll();
		List<DetParametroExportDto> detParametro = this.detParametroExportService.findAll();
		List<DetDistritoElectoralEleccionExportDto> detDistritoElectoralEleccion = this.detDistritoElectoralEleccionExportService.findByCc(idCc);
		List<TabJuradoElectoralEspecialExportDto> juradoElectoralEspecial= juradoElectoralEspecialExportService.findByCc(idCc);
		List<VersionModeloExportDto> versionModelos = versionModeloExportService.findAll();
		
		List<OrcDetConfigDocElectoralExportDto> detalleConfigDocElectorales = this.detalleConfiguracionDocumentoElectoralExportService.findByCc(idCc);
		List<OrcDetTipoEleccionDocElectoralExportDto> detalleTipoEleccionDocElectorales = this.detalleTipoEleccionDocumentoElectoralExportService.findByCc(idCc);
		List<OrcDocElectoralExportDto> docElectorales = this.documentoElectoralExportService.findAll();
		List<OrcSeccionExportDto> secciones = this.seccionExportService.findAll();
		List<OpcionVotoExportDto> opcionesVoto = this.opcionVotoExportService.findAll();
		
		ExportOrcDto exportar = new ExportOrcDto();
		exportar.setAgrupacionPolitica(agrupacionesDto);
		exportar.setAgrupacionPoliticaFicticia(agrupacionesFicticiasDto);
		exportar.setAgrupacionPoliticaReal(agrupacionesRealesDto);
		//exportar.setActa(actasDto);
		exportar.setAmbitoElectoral(ambitoElectoralDto);
		exportar.setCentroComputo(ccDto);
		exportar.setDetActa(detActaDto);
		exportar.setUbigeoEleccionAgrupacionPolitica(dtueapDto);
		exportar.setEleccion(eleccionesDto);
		exportar.setLocalVotacion(locales);
		exportar.setUbigeo(ubigeos);
		//exportar.setMesa(mesas);
		exportar.setUbigeoEleccion(ueDtos);		
		exportar.setProcesoElectoral(procesos);
		exportar.setCatalogo(catalogosDto);
		exportar.setCatalogoEstructura(estructurasDto);
		exportar.setCatalogoReferencia(referenciasDto);
		exportar.setFormatos(formatosDto);
		exportar.setCabActasFormatos(cabActasFormatosDto);
		exportar.setDetActasFormatos(detActasFormatosDto);
		exportar.setDistritoElectorales(distritoElectoralDto);
		exportar.setCandidatos(candidatoDto);
		exportar.setCandidatosFicticios(candidatoFicticioDto);
		exportar.setCandidatosReales(candidatoRealDto);
		exportar.setVersion(versionDto);
		exportar.setPuestaCero(puestaCeroDto);
		exportar.setUsuario(usuarioDto);
		exportar.setDetActaPreferencial(detActaPreferencialDto);
		exportar.setMiembroMesaSorteado(miembrosSorteado);
		exportar.setDetalleConfiguracionDocumentoElectoral(detalleConfigDocElectorales);
		exportar.setDetalleTipoEleccionDocumentoElectoral(detalleTipoEleccionDocElectorales);
		exportar.setDocumentoElectoral(docElectorales);
		exportar.setSeccion(secciones);
		exportar.setDetActaOpcion(detActaOpcion);
		exportar.setOpcionesVoto(opcionesVoto);
		exportar.setCabParametro(cabParametro);		
		exportar.setDetParametro(detParametro);
		exportar.setDetalleDistritoElectoralEleccion(detDistritoElectoralEleccion);
		exportar.setJuradoElectoralEspecial(juradoElectoralEspecial);
		exportar.setVersionModelos(versionModelos);
		return exportar;
	}
	
}
