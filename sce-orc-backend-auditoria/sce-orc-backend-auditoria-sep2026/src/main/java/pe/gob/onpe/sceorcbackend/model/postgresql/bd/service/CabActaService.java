package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import org.springframework.data.repository.query.Param;
import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.ActaConArchivosNull;
import pe.gob.onpe.sceorcbackend.model.dto.MonitoreoNacionBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationRejectMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.ReturnMonitoreoActas;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.*;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationSummaryResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;
import pe.gob.onpe.sceorcbackend.utils.ActaDTO;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.TrazabilidadDto;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CabActaService extends CrudService<Acta> {

  Optional<Acta> findById(Long id);
  
  /**
   * Buscar acta por ID con bloqueo pesimista (FOR UPDATE)
   * Debe ejecutarse dentro de una transacción activa
   */
  Optional<Acta> findByIdForUpdate(Long id);
  
  List<Acta> findByMesaId(Long idMesa);
  Long countByEstadoDigitalizacionNot(String estadoDigitalizacion);

  Long getActaRandom(String eleccionId, String usuario);
  Long getActaProcesamientoManualRandom(String eleccionId, String usuario);

  List<DigitizationListActasItem> listActas(String codigoEleccion, String codigoCentroComputo, String usuario, String status, int offset, int limit);

  DigitizationSummaryResponse summary(String codigoEleccion);

  GenericResponse<String> liberarActaControlDigitalizacion(Long actaIdLo, String usuario);

  void approveMesa(DigitizationApproveMesaRequest request, String usuario, String proceso, String cc);


  GenericResponse<String> finalizarAtencionControlDigitalizacion(String codigoEleccion, TokenInfo tokenInfo);

  void rejectActa(String electionId, DigitizationRejectMesaRequest request, TokenInfo tokenInfo);


    GenericResponse<Boolean> reprocesarActas(List<ActaReprocesadaListIItem> actasReprocesarList, TokenInfo tokenInfo);

    GenericResponse<ActaReprocesadaListIItem> validarReprocesamientoActa(String mesaCopiaDigito);

    GenericResponse<DigitizationListActasItem> bloquearActaControlDigitalizacion(DigitizationListActasItem digitizationListActasItem, String usuario);


  int reseteaValores(String estadoActa, String estadoComputo, String estadoDigitalizacion, String usuario, Date fechaModificacion);

  GenericResponse<TrazabilidadDto> trazabilidadActa(String mesaCopiaDigito);
  GenericResponse<List<TrazabilidadDto>> trazabilidadActaPorMesa(String nrMesa);

  List<ActaPorCorregirListItem> listarActasPorCorregirPorUsuario(TokenInfo tokenInfo);

  ActaPorCorregir actasPorCorregirInfo(Long actaId);

  List<String> validarActasPorCorregir(ActaPorCorregir actaPorCorregir);

  String registrarActasPorCorregir(ActaPorCorregir actaPorCorregir, TokenInfo tokenInfo);

  ReturnMonitoreoActas listActasMonitoreo(MonitoreoNacionBusquedaDto monitoreoNacionBusqueda);

    GenericResponse<List<ActaReprocesadaListIItem>> listReprocesar(String nombreUsuario);

    GenericResponse<Boolean> reprocesarListActas(List<ActaReprocesadaListIItem> actasReprocesarList, String nombreUsuario);

    GenericResponse<Boolean> rechazarActaEnVerificacion(TokenInfo tokenInfo, String mesa, String codigoEleccion);

  GenericResponse<Boolean> puestaCeroPorActa(String mesa, String codigoEleccion, String usuario);

  List<ActaConArchivosNull> actasConArchivosNull();


  List<ActaDTO> findActasNative();


  ActaRepository getCabActaRepository();

  UsuarioService getUsuarioService();

  DetActaAccionService getDetActaAccionService();

  List<Long> findByEstadoActaAndVerificadorAndCodigoEleccion(
      String estadoC,
      String estadoW,
      String usuarioVerificador,
      String codigoEleccion);
  List<Long> findByEstadoActaAndVerificadorAndCodigoEleccionAndDigitalizacion(
      String estadoC,
      String estadoW,
      String usuarioVerificador,
      String codigoEleccion,
      Long digitalizacion);

  
  Object[] summaryControlCalidad(String codigoEleccion, String estadoDigitalizacion, String estadoComputoPen,
                                 String estadoDigitalizadaVal, String estadoComputoVal, String estadoDigPendiente, String estadoActaProcesada);
  
  List<Acta> actasPendientesControlCalidadAsignados(String usuarioControlCalidad, String codigoEleccion); 
  
  void observarActaControlCalidad(Long idActa, TokenInfo tokenInfo);
  
  void aceptarActaControlCalidad(Long idActa, TokenInfo tokenInfo);
  
  void asignarUsuarioActaControlCalidad(String idEleccion, TokenInfo tokenInfo, int cantidad);
  
  void desasignarUsuarioActaControlCalidad(List<Long> idsActas, TokenInfo tokenInfo);

  ArchivosActaDTO listarArchivosPorSolucion(Long actaId);

  List<Long> listarArchivosPorActa(Long actaId, String mesa, String codigoEleccion);

  List<ActaScanProjection> listActasSceScanner(String codigoEleccion, String estadoDigitalizacion);
}
