package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.sceorcbackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.VerificacionHabilitacionDiaEleccionRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.ProcesoElectoralResponseDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.VerificacionHabilitacionDiaEleccionResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.elecciones.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IProcesoElectoralMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DetTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetalleCatalogoEstructuraRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ProcesoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProcesoElectoralServiceImpl implements MaeProcesoElectoralService {

  Logger logger = LoggerFactory.getLogger(ProcesoElectoralServiceImpl.class);
	
  private final ProcesoElectoralRepository procesoElectoralRepository;
  
  private final DetParametroRepository detParametroRepository;

  private final DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository;

  private final EleccionService eleccionService;

  private  final IProcesoElectoralMapper procesoElectoralMapper;
  
  private final DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService;
  
  @Value("${sce.nacion.url}")
  private String urlNacion;
  
  public static final String URL_NACION_VERIFICAR_FECHA_CONVOCATORIA ="proceso-electoral/acronimo/";
  
  private final RestTemplate clientExport;

  public ProcesoElectoralServiceImpl(
		  DetParametroRepository detParametroRepository,
		  ProcesoElectoralRepository procesoElectoralRepository,
		  DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository, 
		  EleccionService eleccionService,
		  IProcesoElectoralMapper procesoElectoralMapper,
		  DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService,
		  RestTemplate clientExport
		  ) {
	this.detParametroRepository = detParametroRepository;
    this.procesoElectoralRepository = procesoElectoralRepository;
    this.detalleCatalogoEstructuraRepository = detalleCatalogoEstructuraRepository;
    this.eleccionService = eleccionService;
    this.procesoElectoralMapper = procesoElectoralMapper;
    this.detTipoEleccionDocumentoElectoralService = detTipoEleccionDocumentoElectoralService;
    this.clientExport = clientExport;
  }

  @Override
  public void save(ProcesoElectoral procesoElectoral) {
    this.procesoElectoralRepository.save(procesoElectoral);
  }

  @Override
  public void saveAll(List<ProcesoElectoral> k) {
    this.procesoElectoralRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.procesoElectoralRepository.deleteAll();
  }

  @Override
  public List<ProcesoElectoral> findAll() {
    return this.procesoElectoralRepository.findAll();
  }

  @Override
  public List<ProcesoElectoralResponseDTO> findAll2() {
    return this.procesoElectoralRepository.findAll().stream().map(a->this.procesoElectoralMapper.entityToDTO(a)).toList();
  }

  @Override
  public ProcesoElectoral findByActivo() {
    return this.procesoElectoralRepository.findByActivo(1);
  }

  @Override
  public ProcesoAmbitoDto getTipoAmbito(String acronimo) {
    ProcesoAmbitoDto procesoAmb = new ProcesoAmbitoDto();
    ProcesoElectoral proceso = this.procesoElectoralRepository.findByAcronimo(acronimo);

    Long idAmbitoElectoral = proceso.getTipoAmbitoElectoral();
    Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
    String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;

    OrcDetalleCatalogoEstructura
        estructura = this.detalleCatalogoEstructuraRepository.findByIdAndColumnaAndCodigoI(idTablaCatalogo.intValue(), columna,
        idAmbitoElectoral.intValue());

    if (proceso != null) {
      procesoAmb.setIdProceso(proceso.getId());
      procesoAmb.setNombreProceso(proceso.getNombre());
    }

    if (estructura != null) {
      procesoAmb.setIdTipoAmbito(estructura.getCodigoI());
      procesoAmb.setNombreTipoAmbito(estructura.getCodigoS());
    }

    return procesoAmb;
  }

  @Override
  public ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso) {
    ProcesoAmbitoDto procesoAmb = null;
    Optional<ProcesoElectoral> procesoOp = this.procesoElectoralRepository.findById(idProceso);

    if (procesoOp.isPresent()) {

      procesoAmb = new ProcesoAmbitoDto();
      ProcesoElectoral proceso = procesoOp.get();
      Long idTipoAmbitoElectoral = proceso.getTipoAmbitoElectoral();
      Long idTablaCatalogo = ConstantesCatalogo.ID_TABLA_MAE_PROCESO_ELECTORAL;
      String columna = ConstantesCatalogo.C_COLUMNA_TIPO_AMBITO_ELECTORAL;

      OrcDetalleCatalogoEstructura estructura =
          this.detalleCatalogoEstructuraRepository.findByIdAndColumnaAndCodigoI(idTablaCatalogo.intValue(), columna,
              idTipoAmbitoElectoral.intValue());

      if (proceso != null) {
        procesoAmb.setIdProceso(proceso.getId());
        procesoAmb.setNombreProceso(proceso.getNombre());
      }

      if (estructura != null) {
        procesoAmb.setIdTipoAmbito(estructura.getCodigoI());
        procesoAmb.setNombreTipoAmbito(estructura.getCodigoS());
      }
    }

    return procesoAmb;
  }

  @Override
  public List<EleccionDto> getElecciones() {
    try {
      List<EleccionDto> eleccionListDto = new ArrayList<>();
      List<Eleccion> maeEleccionList = this.eleccionService.findAll();

      List<Eleccion> maeEleccionListSorted = maeEleccionList.stream().sorted(Comparator.comparing(Eleccion::getCodigo)).toList();

      for (Eleccion maeEleccion : maeEleccionListSorted) {
        EleccionDto eleccionDto = new EleccionDto();
        eleccionDto.setCodigo(maeEleccion.getCodigo());
        eleccionDto.setNombre(maeEleccion.getNombre());
        ProcesoElectoral maeProcesoElectoral = this.procesoElectoralRepository.findByActivo(1);
        DetTipoEleccionDocumentoElectoral tipoEleccionDocumentoElectoralHistorialAE =
            detTipoEleccionDocumentoElectoralService.findByConfiguracionProcesoElectoralAndEleccionAndDocumentoElectoral(
                maeProcesoElectoral.getId(),
                maeEleccion.getId(), ConstantesComunes.ABREV_ACTA_ESCRUTINIO);

        if (tipoEleccionDocumentoElectoralHistorialAE != null) {
          eleccionDto.setRangoInicial(tipoEleccionDocumentoElectoralHistorialAE.getRangoInicial());
          eleccionDto.setRangoFinal(tipoEleccionDocumentoElectoralHistorialAE.getRangoFinal());
          eleccionDto.setDigitoChequeoAE(tipoEleccionDocumentoElectoralHistorialAE.getDigitoChequeo());
          eleccionDto.setDigitoCequeoError(tipoEleccionDocumentoElectoralHistorialAE.getDigitoError());
        }

        DetTipoEleccionDocumentoElectoral tipoEleccionDocumentoElectoralHistorialAIS =
            detTipoEleccionDocumentoElectoralService.findByConfiguracionProcesoElectoralAndEleccionAndDocumentoElectoral(
                maeProcesoElectoral.getId(),
                maeEleccion.getId(), ConstantesComunes.ABREV_ACTA_INSTALACION_SUGRAFIO);

        if (tipoEleccionDocumentoElectoralHistorialAIS != null) {
          eleccionDto.setDigitoChequeoAIS(tipoEleccionDocumentoElectoralHistorialAIS.getDigitoChequeo());
        }

        eleccionListDto.add(eleccionDto);
      }
      return eleccionListDto;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public ProcesoElectoral findBynActivo(Integer activo) {
    return this.procesoElectoralRepository.findByActivo(activo);
  }

  /**
   * True = esta autorizado
   * False = no, no esta autorizado
   */
  @Override
  public boolean verificarHabilitacionDiaEleccion(String acronimo, String formatoFecha) {
		ProcesoElectoral procesoElectoral = procesoElectoralRepository.findByAcronimo(acronimo);
		String hora = detParametroRepository.getHora(
				ConstantesParametros.CAB_PARAM_HORA_PROCESAMIENTO, 
				ConstantesParametros.DET_PARAM_HORA_PROCESAMIENTO);
		
		if(procesoElectoral!=null && hora!=null){
			Date fechaConvocatoria = procesoElectoral.getFechaConvocatoria();
			String sFechaConvocatoria = DateUtil.getDateString(fechaConvocatoria, formatoFecha);
			String sFechaHoraConvocatoria = String.format("%s %s", sFechaConvocatoria, hora);
			Date fechaHoraConvocatoria = DateUtil.getDate(sFechaHoraConvocatoria, SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH);
			Date fechaActual = DateUtil.getFechaActualPeruana();
	        logger.info("Fecha actual peruana: {}", fechaActual);
	        logger.info("Fecha convocatoria: {}", fechaHoraConvocatoria);
	        return fechaActual.before(fechaHoraConvocatoria);
		} else {
			return true;
		}
  }
  
  @SuppressWarnings("unused")
  private boolean verificarHabilitacionDiaEleccionEnNacion(VerificacionHabilitacionDiaEleccionRequest request){
	  HttpHeaders headers = new HttpHeaders();
      headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
      headers.set(SceConstantes.TENANT_HEADER, request.getAcronimo());
      headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      
      HttpEntity<VerificacionHabilitacionDiaEleccionRequest> httpEntity = new HttpEntity<>(request, headers);
      
		ResponseEntity<VerificacionHabilitacionDiaEleccionResponse> response = this.clientExport.exchange(
              urlNacion + URL_NACION_VERIFICAR_FECHA_CONVOCATORIA,
              HttpMethod.GET,
              httpEntity,
              VerificacionHabilitacionDiaEleccionResponse.class);


      VerificacionHabilitacionDiaEleccionResponse body = response.getBody();
      String formato = null;
      String sDateFechaConvocatoria = null;
      if(body!=null){ 
    	  formato = body.getFormatoFechaConvocatoria();
          sDateFechaConvocatoria = body.getFechaConvocatoria();
      } else{
    	  throw new RuntimeException("No se pudo definir la fecha de convocatoria");
      }
      Date fechaConvocatoria = DateUtil.getDate(sDateFechaConvocatoria, formato);
      Date fechaActual = DateUtil.getFechaActualPeruana(formato);
      return fechaActual.before(fechaConvocatoria);
  }
  
  public long contarTodos(){
	  return procesoElectoralRepository.contarTodos();
  }
  
}
