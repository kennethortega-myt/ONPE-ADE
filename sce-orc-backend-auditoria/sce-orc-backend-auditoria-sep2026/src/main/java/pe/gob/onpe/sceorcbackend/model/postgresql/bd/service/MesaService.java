package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto;
import pe.gob.onpe.sceorcbackend.model.dto.MesaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.ReprocesarMesaResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.mesas.DigitizationListMesasItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.OtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.MesaProjection;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MesaService extends CrudService<Mesa> {

    Optional<Mesa> findById(Long id);

    Mesa findByCodigo(String mesa);

    List<Mesa> findByCodigoIn(List<String> codigos);

    List<Mesa> findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMm(String estadoDigtalle, String estadoMesa, String usuario);


    List<Mesa> findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMmIsNull(String estadoDigtalle, String estadoMesa);

    List<Mesa> findByEstadoDigitalizacionMmAndUsuarioAsignadoMm(String estadoDigitalizacion, String usuario);

    List<Mesa> findMesasByEstadoMmAndWithRectangulos(String estadoDigitalizacion, String estadoDigtalPerdidaTotal);

    List<DigitizationListMesasItem> listListaElectoresDigtal(String usuario);

    List<DigitizationListMesasItem> listMiembrosMesaDigtal(String usuario);

    void approveMesa(Long mesaId, String tipoDocumento, String nombreUsuario, String codigoCentroComputo);

    void rejectMesa(Long mesaId, String tipoDocumento, String nombreUsuario);

    List<ActaBean> validaHabilitarContingenciaStae(String mesa);

    void habilitarContingenciaStae(List<ActaBean> actaBeanList, String nombreUsuario);

    long count();

    int reseteaValores(String estadoDigitalizacion, String estadoMesa, String usuario, Date fechaModificacion);

    void actualizarEstadoDigitalizaionPR(Long idMesa, String usuario, String estado);

    void actualizarEstadoDigitalizaionPRisEdit(Long idMesa, String estado);

    List<MesaActaDto> findMesaRamdomPR(String codigoEleccion, List<String> estados, Integer tipoFiltro, String estadoMesa);

    void actualizarEstadoDigitalizaionME(Long idMesa, String usuario, String estado);

    void actualizarEstadoDigitalizaionMEisEdit(Long idMesa, String estado);

    List<MesaActaDto> findMesaRamdomME(String codigoEleccion, List<String> estadosActa, Integer tipoFiltro, String estadoMesa);

    List<Mesa> listLiberarMesasME();
    List<Mesa> listLiberarMesasPR();

    Integer validarActaPrincipalProcesada(Mesa mesa);
    GenericResponse<ReprocesarMesaResponseDto> buscarMesaReprocesar(String codMesa);
    void procesarReprocesarMesa(List<ReprocesarMesaResponseDto> data, String usuario, String codigoCC);

    List<MesaProjection> buscarMesaLeControlDigtalTomadas(List<String> list);

    void liberarMesaLeControlDigtalTomadas(Long id);

    List<MesaProjection> buscarMesamMmControlDigtalTomadas(List<String> cEstadoDigtalDigitalizada);

    void liberarMesaMmControlDigtalTomadas(Long id);

    List<MesaProjection> buscarMesaLeVerificacionTomadas(
            List<String> reprocesar,
            String cEstadoDigtalPendiente,
            String estadoDigtlPerdidaTotal,
            String noInstalada);

    void liberarMesaLeVerificacionTomadas(Long id);

    List<MesaProjection> buscarMesaMmVerificacionTomadas(List<String> estadosDigitalizacion,
                                                         String cEstadoDigtalPendiente,
                                                         String estadoDigtlPerdidaTotal,
                                                         String noInstalada);

    void liberarMesaMmVerificacionTomadas(Long id);

    Long contarMmPorActivo(String estadoDigitalizacionMe,Integer activo);

    Long contarLePorActivo(String estadoDigitalizacionPr, Integer activo);

    Long contarMePorActivo(String estadoDigitalizacionMe,Integer activo);

    Long contarPrPorActivo(String estadoDigitalizacionPr, Integer activo);

    GenericResponse<MesaDTO> buscarMesaEliminarOmiso(String codMesa);

    void procesarEliminarOmisosMesa(List<MesaDTO> data, String usuario, String codigoCC);

    List<Long> findMesaIdsLeRandom(
            List<String> estadosFiltro,
            String estadoP,
            String estadoMesaNoinstalada,
            List<String> estadosSC,
            List<String> estadosActaExcluidos
    );

    List<Long> findMesasAsignadasConFiltro(
       String usuario,
       List<String> estadosFiltro,
       String estadoDigitalPendiente,
       String estadoMesaNoInstalada
    );

    List<OtroDocumentoDto> listarListaElectoresScanner(String nombreUsuario);

    List<OtroDocumentoDto> listarMiembrosMesaScanner(String nombreUsuario);

    Long obtenerMesaForUpdate(final String estado);

    void actualizarUsuarioAsignadoMM(String usuario, Long idMesa);

}
