package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;


import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionAsociadosRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionDevueltasRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaPorCorregirListItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion.TabResolucionDTO;
import pe.gob.onpe.sceorcbackend.utils.reimpresion.ReimpresionCargoDto;

import java.util.List;
import java.util.Optional;

public interface ResolucionService extends CrudService<TabResolucion>{

    Optional<TabResolucion> findById(Long id);

    ActaJeeBean obtenerActasEnvioJee(Long idEleccion);
    GenericResponse<Object> obtenerActaEnvioJee(String cc, String numeActa, String numeMesa);
    ResolucionAsociadosRequest getResolucion(Long id);
    SearchFilterResponse<ResolucionDevueltasRequest> getResolucionesDevueltas(int page, int size);
    ResolucionAsociadosRequest getRandomResolucion(String usuario);
    ResumenResolucionesDto resumenResoluciones(String numeroResolucion);
    GenericResponse<Object> validarActaDevueltaJee(String codigoCentroComputo, String nroActa, String nroCopiaAndDig);
    GenericResponse<Object> obtenerInfoActa(Integer codTipoResolucion, String nroActaCopiaDig, Long idProceso);
    GenericResponse<ActaBean> obtenerInfoActaById(Long idActa);
    GenericResponse<Object> registrarAsociacionConActas(TokenInfo tokenInfo, ResolucionAsociadosRequest resolucionAsociadosRequest, Long idUbigeo, Long idLocalVotacion, Long idEleccion);


    TabResolucion saveResolucion(String numeroResolucion, int numeroPaginas, Archivo archivo, String usuario);

    GenericResponse<AplicarActaBean> aplicarResolucion(TokenInfo tokenInfo, ActaBean actaBean);

    GenericResponse<TabResolucionDTO> actualizarEstadoDigitalizacion(TokenInfo tokenInfo, Long idResolucion, String estadoDigitalizacion);

    void anularResolucion(TokenInfo tokenInfo, Long idResolucion);

    GenericResponse<List<ActaBean>> getInfoActaParaAsociacionResoluciones(Integer codTipoResolucion, String nroActaCopiaDig, Long idUbigeo, Long idLocalVotacion, Long idEleccion, Long idProceso);

    GenericResponse<Object> generarCargoEntrega(TokenInfo tokenInfo, List<ActaBean> actaBeanLis);
    
    GenericResponse<Object> generarCargoEntregaOficio(TokenInfo tokenInfo, ActaBean actaBeanLis);

    GenericResponse<Object> generarCargoEntregaActaDevuelta(TokenInfo tokenInfo, List<ActaBean> actaBeanLis);

    GenericResponse<Object> generarCargoEntregaMesaNoInstaladas(TokenInfo tokenInfo, ResolucionAsociadosRequest resolucionAsociadosRequest);

    GenericResponse<Object> generarCargoEntregaInfundadas(TokenInfo tokenInfo, ResolucionAsociadosRequest resolucionAsociadosRequest);
    
    GenericResponse<Object> generarOficioActaObservada(TokenInfo tokenInfo, List<ActaBean> actaBeanLis);

    Long count();

    void deleteAllInBatch();

    List<ReimpresionCargoDto> reimpresionCargos(String mesa);

    List<DigitizationListResolucionItem> listaResolucionesDigtal(String nombreUsuario);

    List<DigitizationListResolucionItem> listaResolucionesParaEditar(String nombreUsuario);

    GenericResponse<TabResolucionDTO> validarParaEdicion(TokenInfo tokenInfo, String numeroResolucion);


    void digitalizarResolucion(TokenInfo tokenInfo, Long idResolucion, String numeroResolucion, Integer numeroPaginas, MultipartFile file);

    GenericResponse<Boolean> bloquearYAsignarResolucion(Long idResolucion, String usuario);

    GenericResponse<Boolean> desbloquearResolucion(Long idResolucion, String usuario);
}
