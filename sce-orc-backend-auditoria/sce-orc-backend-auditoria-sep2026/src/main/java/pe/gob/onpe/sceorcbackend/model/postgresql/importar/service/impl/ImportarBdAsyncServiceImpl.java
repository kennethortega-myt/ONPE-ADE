package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.sceorcbackend.model.importar.mapper.UtilMapper;
import pe.gob.onpe.sceorcbackend.model.dto.request.ImportarRequest;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmDetConfigDocElectoralHistDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmDetTipoEleccionDocElectoralHistDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmDocElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AdmSeccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaFicticioDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaRealDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AmbitoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CabActaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CabParametroDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CandidatoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CandidatoFicticioDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CandidatoRealDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CatalogoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaOpcionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetActaPreferencialDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetCatalogoEstructuraDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetCatalogoReferenciaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetDistritoElectoralEleccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetParametroDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DetUbigeoEleccionAgrupacionPoliticaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.DistritoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.ImportarDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.LocalVotacionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.MesaDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.MiembroMesaSorteadoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.OpcionVotoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.PaginaOptDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.ProcesoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.TabJuradoElectoralEspecialDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.UbigeoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.UbigeoEleccionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.UsuarioDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.VersionDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.VersionModeloDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAgrupacionPoliticaFicticia;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAgrupacionPoliticaReal;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportAmbitoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCabParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportLocalVotacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportUbigeoEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetalleConfiguracionDocumentoElectoralHistorial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetalleTipoEleccionDocumentoElectoralHistorial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportSeccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportTabJuradoElectoralEspecial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportAccesoPcRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetalleConfiguracionDocumentoElectoralHistorialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetalleTipoEleccionDocumentoElectoralHistorialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDocumentoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportSeccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportTabResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDistritoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidato;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidatoFicticio;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportCandidatoReal;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportUbigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportMiembroMesaSorteado;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOpcionVoto;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetDistritoElectoralEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportUsuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportVersion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportVersionModelo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.exception.NoDataImportException;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportImportadorProgreso;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportMesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOrcCatalogo;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOrcDetalleCatalogoReferencia;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportOrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportActaHistorialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportAgrupacionPoliticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportAmbitoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportArchivoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCabActaCelesteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCabActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportTabJuradoElectoralEspecialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCabOtroDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCabParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCandidatoFicticioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCandidatoRealRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportAgrupacionPoliticaFicticiaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportAgrupacionPoliticaRealRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCandidatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCierreCentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaAccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaHistorialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaOficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaOpcionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaPreferencialHistorialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaPreferencialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetActaResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetDistritoElectoralEleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetOtroDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetUbigeoEleccionAgrupacionPoliticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDistritoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportEleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportLocalVotacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportMesaDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportMesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportMiembroMesaColaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportMiembroMesaEscrutinioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportMiembroMesaSorteadoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportOficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportOmisoMiembroMesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportOmisoVotanteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportOpcionVotoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportPadronElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportPersoneroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportCatalogoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetalleCatalogoEstructuraRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetalleCatalogoReferenciaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetalleTipoEleccionDocumentoElectoralEscrutinioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportProcesoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportPuestaCeroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportTransmisionEnvioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportTransmisionRecepcionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportUbigeoEleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportUbigeoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportUsuarioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportVersionModeloRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportVersionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportImportadorProgresoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.GestionarConstraintService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.GenerarSecuenciaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.BroadcastWebsocketServiceImpl;
import pe.gob.onpe.sceorcbackend.utils.ConstantesImportador;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Service
public class ImportarBdAsyncServiceImpl {

    private static final String BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS = "/topic/update-progress-with-details";
    Logger logger = LoggerFactory.getLogger(ImportarBdAsyncServiceImpl.class);

    private final BroadcastWebsocketServiceImpl broadcastWebsocketService;
    private final ObjectMapper objectMapper;
    private final ImportActaRepository cabActaRepository;
    private final ImportActaHistorialRepository actaHistorialRepository;
    private final ImportDetActaRepository detActaRepository;
    private final ImportDetActaHistorialRepository detActaHistorialRepository;
    private final ImportDetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository;
    private final ImportUbigeoEleccionRepository ubigeoEleccionRepository;
    private final ImportAmbitoElectoralRepository ambitoElectoralRepository;
    private final ImportAgrupacionPoliticaRepository agrupacionPoliticaRepository;
    private final ImportAgrupacionPoliticaFicticiaRepository agrupacionPoliticaFicticioRepository;
    private final ImportAgrupacionPoliticaRealRepository agrupacionPoliticaRealRepository;
    private final ImportCentroComputoRepository centroComputoRepository;
    private final ImportMesaRepository mesaRepository;
    private final ImportProcesoElectoralRepository procesoElectoralRepository;
    private final ImportEleccionRepository eleccionRepository;
    private final ImportUbigeoRepository ubigeoRepository;
    private final ImportLocalVotacionRepository localVotacionRepository;
    private final ImportCatalogoRepository catalogoRepository;
    private final ImportDetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository;
    private final ImportDetalleCatalogoReferenciaRepository detalleCatalogoReferenciaRepository;
    private final ImportFormatoRepository formatoRepository;
    private final ImportDetActaFormatoRepository detActaFormatoRepository;
    private final ImportCabActaFormatoRepository cabActaFormatoRepository;
    private final ImportCandidatoRepository candidatoRepository;
    private final ImportCandidatoFicticioRepository candidatoFicticioRepository;
    private final ImportCandidatoRealRepository candidatoRealRepository;
    private final ImportDistritoElectoralRepository distritoElectoralRepository;
    private final ImportPuestaCeroRepository puestaCeroRepository;
    private final ImportUsuarioRepository usuarioRepository;
    private final ImportDetActaPreferencialRepository detActaPreferencialRepository;
    private final ImportMiembroMesaSorteadoRepository miembroMesaSorteadoRepository;
    private final ImportArchivoRepository archivoRepository;
    private final ImportVersionRepository versionRepository;
    private final ImportCabActaCelesteRepository cabActaCelesteRepository;

    private final ImportDetalleConfiguracionDocumentoElectoralHistorialRepository admDcdehRepository;
    private final ImportSeccionRepository adminSeccionRepository;
    private final ImportDocumentoElectoralRepository adminDocumentoElectoralRepository;
    private final ImportDetalleTipoEleccionDocumentoElectoralHistorialRepository admDtedehRepository;

    private final ImportOmisoMiembroMesaRepository omisoMiembroMesaRepository;
    private final ImportPadronElectoralRepository padronElectoralRepository;
    private final ImportOmisoVotanteRepository omisoVotanteRepository;
    private final ImportMiembroMesaColaRepository miembroMesaColaRepository;
    private final ImportDetActaPreferencialHistorialRepository detActaPreferencialHistorialRepository;
    private final ImportDetActaAccionRepository detActaAccionRepository;
    private final ImportDetActaResolucionRepository detActaResolucionRepository;
    private final ImportMesaDocumentoRepository mesaDocumentoRepository;
    private final ImportDetalleTipoEleccionDocumentoElectoralEscrutinioRepository detalleTipoEleccionDocumentoElectoralEscrutinioRepository;
    private final ImportTransmisionEnvioRepository transmisionEnvioRepository;
    private final ImportTransmisionRecepcionRepository transmisionRecepcionRepository;
    private final ImportPersoneroRepository personeroRepository;
    private final ImportTabResolucionRepository resolucionRepository;
    private final ImportDetActaOpcionRepository detActaOpcionRepository;
    private final ImportOpcionVotoRepository opcionVotoRepository;
    private final ImportCabParametroRepository cabParametroRepository;
    private final ImportDetParametroRepository detParametroRepository;
    private final ImportMiembroMesaEscrutinioRepository escrutinioRepository;
    private final ImportDetDistritoElectoralEleccionRepository detDistritoElectoralEleccionRepository;
    private final ImportDetOtroDocumentoRepository detOtroDocumentoRepository;
    private final GenerarSecuenciaService generarSecuenciaService;
    private final GestionarConstraintService gestionarConstraintService;
    private final ImportImportadorProgresoService importadorService;
    private final ImportCierreCentroComputoRepository importCierreCentroComputoRepository;
    private final ImportAccesoPcRepository importAccesoPcRepository;
    private final ImportCabOtroDocumentoRepository cabOtroDocumentoRepository;
    private final ImportOficioRepository oficioRepository;
    private final ImportDetActaOficioRepository detActaOficioRepository;
    private final ImportTabJuradoElectoralEspecialRepository importCabJuradoElectoralEspecialRepository;
    private final ImportVersionModeloRepository importVersionModeloRepository;

    private final RestTemplate clientExport;

    @Value("${sce.nacion.url}")
    private String urlNacion;
    
    @Value("${importar.acta.tamanio}")
    private int tamanioActa;
    
    @Value("${importar.mesa.tamanio}")
    private int tamanioMesa;


    private enum PasoMigracion {
    	ELIMINAR_DATA(1, "la eliminación de datos"),
        OPCION_VOTO(1, "la migración de opciones de voto"),
        CENTRO_COMPUTO(1, "la migración de centros de cómputo"),
        JURADO_ESPECIAL(1, "la migración de jurado electoral especial"),
        DISTRITOS_ELECTORALES(1, "la migración de distritos electorales"),
        AMBITOS_ELECTORALES(1, "la migración de ámbitos electorales"),
        UBIGEOS(2, "la migración de ubigeos"),
        AGRUPACIONES_POLITICAS(1, "la migración de agrupaciones políticas"),
        AGRUPACIONES_FICTICIAS(1, "la migración de agrupaciones políticas ficticias"),
        AGRUPACIONES_REALES(1, "la migración de agrupaciones políticas reales"),
        PROCESOS_ELECTORALES(1, "la migración de procesos electorales"),
        ELECCIONES(2, "la migración de elecciones"),
        LOCALES(2, "la migración de locales de votación"),
        CATALOGO(1, "la migración de catálogo del proceso"),
        CATALOGO_ESTRUCTURA(1, "la migración de catálogo de estructura"),
        CATALOGO_REFERENCIA(1, "la migración de catálogo de referencia"),
        CANDIDATOS(3, "la migración de candidatos"),
        CANDIDATOS_FICTICIOS(1, "la migración de candidatos ficticios"),
        CANDIDATOS_REALES(1, "la migración de candidatos reales"),
        UBIGEOS_ELECCION(2, "la migración de ubigeos por elección"),
        ELECCIONES_AGRUPACIONES(2, "la migración de elecciones por agrupaciones políticas"),
        VERSION(1, "la migración de versión"),
        VERSION_MODELO(1, "la migración de versión modelo"),
        USUARIOS(1, "la migración de usuarios"),
        MESA(5, "la migración de mesas de votación"),
        ACTA(5, "la migración de actas electorales"),
        DETALLES_ACTA(3, "la migración de detalles de actas"),
        ACTAS_PREFERENCIALES(2, "la migración de actas preferenciales"),
        MIEMBROS_MESA(2, "la migración de miembros de mesa sorteados"),
        SECCION(1, "la migración de secciones"),
        DOCUMENTO_ELECTORAL(2, "la migración de documentos electorales"),
        TIPO_ELECCION_DOCUMENTO(1, "la migración de tipo de elección por documento electoral"),
        CONFIGURACION_DOCUMENTO(1, "la migración de configuración de documentos electorales"),
        DET_ACTA_OPCION(2, "la migración de detalles de acta - opción"),
        CAB_PARAMETRO(1, "la migración de parámetros"),
        DET_PARAMETRO(2, "la migración de detalles de parámetros"),
        DISTRITO_ELECCION(1, "la migración de detalles de distrito electoral por elección"),
    	REINICIAR_SECUENCIAS(1, "el reinicio de secuencias");
        
        private final int peso;
        private final String descripcion;
        
        PasoMigracion(int peso, String descripcion) {
            this.peso = peso;
            this.descripcion = descripcion;
        }
        
        public int getPeso() {
            return peso;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public ImportarBdAsyncServiceImpl(BroadcastWebsocketServiceImpl broadcastWebsocketService,
            ObjectMapper objectMapper, ImportActaRepository cabActaRepository,
            ImportActaHistorialRepository actaHistorialRepository, ImportDetActaRepository detActaRepository,
            ImportDetActaHistorialRepository detActaHistorialRepository, ImportCatalogoRepository catalogoRepository,
            ImportDetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository,
            ImportDetalleCatalogoReferenciaRepository detalleCatalogoReferenciaRepository,
            ImportDetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository,
            ImportUbigeoEleccionRepository ubigeoEleccionRepository,
            ImportAgrupacionPoliticaRepository agrupacionPoliticaRepository,
            ImportAgrupacionPoliticaFicticiaRepository agrupacionPoliticaFicticioRepository,
            ImportAgrupacionPoliticaRealRepository agrupacionPoliticaRealRepository,
            ImportAmbitoElectoralRepository ambitoElectoralRepository,
            ImportCentroComputoRepository centroComputoRepository, ImportMesaRepository mesaRepository,
            ImportProcesoElectoralRepository procesoElectoralRepository, ImportEleccionRepository eleccionRepository,
            ImportUbigeoRepository ubigeoRepository, ImportLocalVotacionRepository localVotacionRepository,
            ImportFormatoRepository formatoRepository, ImportDetActaFormatoRepository detActaFormatoRepository,
            ImportCandidatoRepository candidatoRepository,
            ImportCandidatoFicticioRepository candidatoFicticioRepository,
            ImportCandidatoRealRepository candidatoRealRepository,
            ImportDistritoElectoralRepository distritoElectoralRepository,
            ImportPuestaCeroRepository puestaCeroRepository, ImportUsuarioRepository usuarioRepository,
            ImportDetActaPreferencialRepository detActaPreferencialRepository,
            ImportMiembroMesaSorteadoRepository miembroMesaSorteadoRepository,
            ImportArchivoRepository archivoRepository,
            ImportDetalleConfiguracionDocumentoElectoralHistorialRepository admDcdehRepository,
            ImportSeccionRepository adminSeccionRepository,
            ImportDocumentoElectoralRepository adminDocumentoElectoralRepository,
            ImportDetalleTipoEleccionDocumentoElectoralHistorialRepository admDtedehRepository,
            ImportVersionRepository versionRepository, ImportOmisoMiembroMesaRepository omisoMiembroMesaRepository,
            ImportPadronElectoralRepository padronElectoralRepository,
            ImportOmisoVotanteRepository omisoVotanteRepository,
            ImportMiembroMesaColaRepository miembroMesaColaRepository,
            ImportDetActaPreferencialHistorialRepository detActaPreferencialHistorialRepository,
            ImportDetActaAccionRepository detActaAccionRepository,
            ImportDetActaResolucionRepository detActaResolucionRepository,
            ImportMesaDocumentoRepository mesaDocumentoRepository,
            ImportDetalleTipoEleccionDocumentoElectoralEscrutinioRepository detalleTipoEleccionDocumentoElectoralEscrutinioRepository,
            ImportTransmisionRecepcionRepository transmisionRecepcionRepository,
            ImportTransmisionEnvioRepository transmisionEnvioRepository,
            ImportTabResolucionRepository resolucionRepository, ImportCabActaFormatoRepository cabActaFormatoRepository,
            ImportDetActaOpcionRepository detActaOpcionRepository, ImportOpcionVotoRepository opcionVotoRepository,
            ImportCabParametroRepository cabParametroRepository, ImportDetParametroRepository detParametroRepository,
            ImportPersoneroRepository personeroRepository, ImportMiembroMesaEscrutinioRepository escrutinioRepository,
            GenerarSecuenciaService generarSecuenciaService, GestionarConstraintService gestionarConstraintService,
            ImportImportadorProgresoService importadorService,
            ImportDetDistritoElectoralEleccionRepository detDistritoElectoralEleccionRepository,
            ImportCierreCentroComputoRepository importCierreCentroComputoRepository,
            ImportAccesoPcRepository importAccesoPcRepository,
            ImportCabOtroDocumentoRepository cabOtroDocumentoRepository,
            ImportDetOtroDocumentoRepository importDetOtroDocumentoRepository,
            ImportCabActaCelesteRepository cabActaCelesteRepository, ImportOficioRepository oficioRepository,
            ImportDetActaOficioRepository detActaOficioRepository,
            ImportTabJuradoElectoralEspecialRepository importCabJuradoElectoralEspecialRepository,
            ImportVersionModeloRepository importVersionModeloRepository, RestTemplate clientExport) {
        this.broadcastWebsocketService = broadcastWebsocketService;
        this.objectMapper = objectMapper;
        this.cabActaRepository = cabActaRepository;
        this.actaHistorialRepository = actaHistorialRepository;
        this.detActaRepository = detActaRepository;
        this.detActaHistorialRepository = detActaHistorialRepository;
        this.detUbigeoEleccionAgrupacionPoliticaRepository = detUbigeoEleccionAgrupacionPoliticaRepository;
        this.ubigeoEleccionRepository = ubigeoEleccionRepository;
        this.agrupacionPoliticaRepository = agrupacionPoliticaRepository;
        this.agrupacionPoliticaFicticioRepository = agrupacionPoliticaFicticioRepository;
        this.agrupacionPoliticaRealRepository = agrupacionPoliticaRealRepository;
        this.ambitoElectoralRepository = ambitoElectoralRepository;
        this.centroComputoRepository = centroComputoRepository;
        this.mesaRepository = mesaRepository;
        this.procesoElectoralRepository = procesoElectoralRepository;
        this.eleccionRepository = eleccionRepository;
        this.ubigeoRepository = ubigeoRepository;
        this.localVotacionRepository = localVotacionRepository;
        this.catalogoRepository = catalogoRepository;
        this.detalleCatalogoEstructuraRepository = detalleCatalogoEstructuraRepository;
        this.detalleCatalogoReferenciaRepository = detalleCatalogoReferenciaRepository;
        this.formatoRepository = formatoRepository;
        this.detActaFormatoRepository = detActaFormatoRepository;
        this.candidatoRepository = candidatoRepository;
        this.candidatoFicticioRepository = candidatoFicticioRepository;
        this.candidatoRealRepository = candidatoRealRepository;
        this.distritoElectoralRepository = distritoElectoralRepository;
        this.puestaCeroRepository = puestaCeroRepository;
        this.usuarioRepository = usuarioRepository;
        this.detActaPreferencialRepository = detActaPreferencialRepository;
        this.miembroMesaSorteadoRepository = miembroMesaSorteadoRepository;
        this.archivoRepository = archivoRepository;
        this.versionRepository = versionRepository;
        this.admDcdehRepository = admDcdehRepository;
        this.adminSeccionRepository = adminSeccionRepository;
        this.adminDocumentoElectoralRepository = adminDocumentoElectoralRepository;
        this.admDtedehRepository = admDtedehRepository;
        this.omisoMiembroMesaRepository = omisoMiembroMesaRepository;
        this.padronElectoralRepository = padronElectoralRepository;
        this.omisoVotanteRepository = omisoVotanteRepository;
        this.miembroMesaColaRepository = miembroMesaColaRepository;
        this.detActaPreferencialHistorialRepository = detActaPreferencialHistorialRepository;
        this.detActaAccionRepository = detActaAccionRepository;
        this.detActaResolucionRepository = detActaResolucionRepository;
        this.mesaDocumentoRepository = mesaDocumentoRepository;
        this.detalleTipoEleccionDocumentoElectoralEscrutinioRepository = detalleTipoEleccionDocumentoElectoralEscrutinioRepository;
        this.transmisionRecepcionRepository = transmisionRecepcionRepository;
        this.transmisionEnvioRepository = transmisionEnvioRepository;
        this.resolucionRepository = resolucionRepository;
        this.cabActaFormatoRepository = cabActaFormatoRepository;
        this.detActaOpcionRepository = detActaOpcionRepository;
        this.opcionVotoRepository = opcionVotoRepository;
        this.cabParametroRepository = cabParametroRepository;
        this.detParametroRepository = detParametroRepository;
        this.personeroRepository = personeroRepository;
        this.escrutinioRepository = escrutinioRepository;
        this.detDistritoElectoralEleccionRepository = detDistritoElectoralEleccionRepository;
        this.generarSecuenciaService = generarSecuenciaService;
        this.gestionarConstraintService = gestionarConstraintService;
        this.importadorService = importadorService;
        this.importCierreCentroComputoRepository = importCierreCentroComputoRepository;
        this.importAccesoPcRepository = importAccesoPcRepository;
        this.cabOtroDocumentoRepository = cabOtroDocumentoRepository;
        this.detOtroDocumentoRepository = importDetOtroDocumentoRepository;
        this.cabActaCelesteRepository = cabActaCelesteRepository;
        this.oficioRepository = oficioRepository;
        this.detActaOficioRepository = detActaOficioRepository;
        this.importCabJuradoElectoralEspecialRepository = importCabJuradoElectoralEspecialRepository;
        this.importVersionModeloRepository = importVersionModeloRepository;
        this.clientExport = clientExport;
    }

    private void reportProgress(double percent, String message, String estado) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("porcentaje", percent);
        payload.put("texto", message);
        payload.put("estado", estado);

        try {
            this.broadcastWebsocketService.broadcastProgressUpdate(BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS,
                    this.objectMapper.writeValueAsString(payload));
            Thread.sleep(100);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.error("Error: {}", e.getMessage());
        }
    }

    private int current = 0;
    private int totalSteps = 38;

    private double getPercent() {
        return (this.current++) * 100 / (float) totalSteps;
    }
    
    // Calcular peso total de todos los pasos
    private int calcularPesoTotal() {
        return Arrays.stream(PasoMigracion.values())
                .mapToInt(PasoMigracion::getPeso)
                .sum();
    }
    
    // Calcular porcentaje base para un paso específico
    private double calcularPorcentajePaso(PasoMigracion paso) {
        int pesoTotal = calcularPesoTotal();
        return (paso.getPeso() * 100.0) / pesoTotal;
    }
    
    // Calcular progreso durante paginación
    private double calcularProgresoPaginado(double startPercent, double endPercent, 
                                            int procesados, int total) {
        if (total == 0) return endPercent;
        double rango = endPercent - startPercent;
        double avance = (procesados * 1.0 / total) * rango;
        return startPercent + avance;
    }
    
    // Interface funcional para tareas de migración
    @FunctionalInterface
    private interface MigracionTask {
        void ejecutar();
    }
    
    // Método auxiliar para migrar con rango de porcentaje
    private double migrarConRango(PasoMigracion paso, double startPercent, MigracionTask task) {
    	logger.info("Migrando.....{}%",startPercent);
        double endPercent = startPercent + calcularPorcentajePaso(paso);
        String mensajeInicio = String.format("Iniciando %s", paso.getDescripcion());
        this.reportProgress(startPercent, mensajeInicio, SceConstantes.ESTADO_PROGRESO_CONTINUA);
        
        task.ejecutar();
        
        if(endPercent>=100){
        	this.reportProgress(endPercent, 
                    String.format("Se finalizó %s", paso.getDescripcion()), 
                    SceConstantes.ESTADO_PROGRESO_CONTINUA);
        	
        	this.reportProgress(100, 
                    "Se culminó con la migración de datos", 
                    SceConstantes.ESTADO_PROGRESO_FINALIZA);
        } else {
        	this.reportProgress(endPercent, 
                    String.format("Se finalizó %s", paso.getDescripcion()), 
                    SceConstantes.ESTADO_PROGRESO_CONTINUA);
        }
        
        
        return endPercent;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<Void> migrar(String cc, String proceso, String usuario) {
        ImportarDto request = this.importarTablas(cc, proceso);
        this.current = 0;
        ImportImportadorProgreso importador = null;
        double currentPercent = 0;
        try {

            if (request == null || request.getCentroComputo() == null
                    || (request.getCentroComputo() != null && request.getCentroComputo().isEmpty())) {
                throw new NoDataImportException("Falló la carga de datos, no hay datos para importar");
            }

            currentPercent = this.importar(request, importador, usuario, cc, proceso);
            
        } catch (NoDataImportException e) {
            logger.error("Error en la importacion: ", e);
            this.reportProgress(0, e.getMessage(), SceConstantes.ESTADO_PROGRESO_FALLO);
        } catch (Exception e) {
            logger.error("Error en la importacion: ", e);
            this.reportProgress(0, "Falló la carga de datos", SceConstantes.ESTADO_PROGRESO_FALLO);
        } finally {
            this.finalizar(currentPercent);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    private void finalizar(double currentPercent){
    	migrarConRango(PasoMigracion.REINICIAR_SECUENCIAS, currentPercent,
                () -> this.resetearSecuencias());
    }

    private double importar(ImportarDto request, ImportImportadorProgreso importador, String usuario, String cc,
            String proceso) {
      
        
        double currentPercent = 0;
        
        currentPercent = migrarConRango(PasoMigracion.ELIMINAR_DATA, currentPercent,
                () -> this.eliminarColecciones());
        
        // Migrar cada tabla con su rango de porcentaje
        currentPercent = migrarConRango(PasoMigracion.OPCION_VOTO, currentPercent,
            () -> this.migrarOpcionVoto(request.getOpcionesVoto(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CENTRO_COMPUTO, currentPercent,
            () -> this.migrarCentroComputo(request.getCentroComputo(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.JURADO_ESPECIAL, currentPercent,
            () -> this.migrarJuradoElectoralEspecial(request.getJuradoElectoralEspecial(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.DISTRITOS_ELECTORALES, currentPercent,
            () -> this.migrarDistritosElectorales(request.getDistritoElectorales(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.AMBITOS_ELECTORALES, currentPercent,
            () -> this.migrarAmbitosElectorales(request.getAmbitoElectoral(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.UBIGEOS, currentPercent,
            () -> this.migrarUbigeos(request.getUbigeo(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.AGRUPACIONES_POLITICAS, currentPercent,
            () -> this.migrarAgrupacionesPoliticas(request.getAgrupacionPolitica(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.AGRUPACIONES_FICTICIAS, currentPercent,
            () -> this.migrarAgrupacionesPoliticasFicticios(request.getAgrupacionPoliticaFicticia(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.AGRUPACIONES_REALES, currentPercent,
            () -> this.migrarAgrupacionesPoliticasReales(request.getAgrupacionPoliticaReal(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.PROCESOS_ELECTORALES, currentPercent,
            () -> this.migrarProcesosElectorales(request.getProcesoElectoral(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.ELECCIONES, currentPercent,
            () -> this.migrarElecciones(request.getEleccion(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.LOCALES, currentPercent,
            () -> this.migrarLocales(request.getLocalVotacion(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CATALOGO, currentPercent,
            () -> this.migrarCatalogo(request.getCatalogo(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CATALOGO_ESTRUCTURA, currentPercent,
            () -> this.migrarCatalogoEstructura(request.getCatalogoEstructura(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CATALOGO_REFERENCIA, currentPercent,
            () -> this.migrarCatalogoReferencia(request.getCatalogoReferencia(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CANDIDATOS, currentPercent,
            () -> this.migrarCandidatos(request.getCandidatos(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CANDIDATOS_FICTICIOS, currentPercent,
            () -> this.migrarCandidatosFicticios(request.getCandidatosFicticios(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CANDIDATOS_REALES, currentPercent,
            () -> this.migrarCandidatosReales(request.getCandidatosReales(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.UBIGEOS_ELECCION, currentPercent,
            () -> this.migrarUbigeosPorEleccion(request.getUbigeoEleccion(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.ELECCIONES_AGRUPACIONES, currentPercent,
            () -> this.migrarEleccionesPorAgrupacionesPoliticas(request.getUbigeoEleccionAgrupacionPolitica(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.VERSION, currentPercent,
            () -> this.migrarVersion(request.getVersion(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.VERSION_MODELO, currentPercent,
            () -> this.migrarVersionModelo(request.getVersionModelos(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.USUARIOS, currentPercent,
            () -> this.migrarUsuarios(request.getUsuario(), importador, usuario));
        
        // Para mesas y actas, pasar los rangos calculados
        double startMesa = currentPercent;
        double endMesa = startMesa + calcularPorcentajePaso(PasoMigracion.MESA);
        this.migrarMesa(cc, proceso, startMesa, endMesa);
        currentPercent = endMesa;
        
        double startActa = currentPercent;
        double endActa = startActa + calcularPorcentajePaso(PasoMigracion.ACTA);
        this.migrarActa(cc, proceso, startActa, endActa);
        currentPercent = endActa;
        
        currentPercent = migrarConRango(PasoMigracion.DETALLES_ACTA, currentPercent,
            () -> this.migrarDetallesActa(request.getDetActa(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.ACTAS_PREFERENCIALES, currentPercent,
            () -> this.migrarActasPreferenciales(request.getDetActaPreferencial(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.MIEMBROS_MESA, currentPercent,
            () -> this.migrarMiembrosMesaSorteados(request.getMiembroMesaSorteado(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.SECCION, currentPercent,
            () -> this.migrarSeccion(request.getSeccion(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.DOCUMENTO_ELECTORAL, currentPercent,
            () -> this.migrarDocumentoElectoral(request.getDocumentoElectoral(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.TIPO_ELECCION_DOCUMENTO, currentPercent,
            () -> this.migrarDetalleTipoEleccionDocumentoElectoral(request.getDetalleTipoEleccionDocumentoElectoral(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CONFIGURACION_DOCUMENTO, currentPercent,
            () -> this.migrarDetalleConfiguracionDocumentoElectoral(request.getDetalleConfiguracionDocumentoElectoral(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.DET_ACTA_OPCION, currentPercent,
            () -> this.migrarDetActaOpcion(request.getDetActaOpcion(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.CAB_PARAMETRO, currentPercent,
            () -> this.migrarCabParametro(request.getCabParametro(), importador, usuario));
        
        currentPercent = migrarConRango(PasoMigracion.DET_PARAMETRO, currentPercent,
            () -> this.migrarDetParametro(request.getDetParametro(), importador, usuario));
        
        currentPercent =  migrarConRango(PasoMigracion.DISTRITO_ELECCION, currentPercent,
            () -> this.migrarDetDistritoElectoralEleccion(request.getDetalleDistritoElectoralEleccion(), importador, usuario));
        
        return currentPercent;
    }

    private void eliminarColecciones() {
        this.importCabJuradoElectoralEspecialRepository.deleteAllInBatch();
        this.detActaOficioRepository.deleteAllInBatch();
        this.oficioRepository.deleteAllInBatch();
        this.detOtroDocumentoRepository.deleteAllInBatch();
        this.mesaDocumentoRepository.deleteAllInBatch();
        this.opcionVotoRepository.deleteAllInBatch();
        this.omisoMiembroMesaRepository.deleteAllInBatch();
        this.miembroMesaSorteadoRepository.deleteAllInBatch();
        this.omisoVotanteRepository.deleteAllInBatch();
        this.miembroMesaColaRepository.deleteAllInBatch();
        this.detActaPreferencialHistorialRepository.deleteAllInBatch();
        this.detActaHistorialRepository.deleteAllInBatch();
        this.actaHistorialRepository.deleteAllInBatch();
        this.detActaPreferencialRepository.deleteAllInBatch();
        this.detActaOpcionRepository.deleteAllInBatch();
        this.detActaRepository.deleteAllInBatch();
        this.detActaAccionRepository.deleteAllInBatch();
        this.detActaFormatoRepository.deleteAllInBatch();
        this.cabActaFormatoRepository.deleteAllInBatch();
        this.detActaResolucionRepository.deleteAllInBatch();
        this.resolucionRepository.deleteAllInBatch();
        this.cabActaCelesteRepository.deleteAllInBatch();
        this.cabActaRepository.deleteAllInBatch();
        this.archivoRepository.deleteAllInBatch();
        this.detalleCatalogoEstructuraRepository.deleteAllInBatch();
        this.detalleCatalogoReferenciaRepository.deleteAllInBatch();
        this.catalogoRepository.deleteAllInBatch();
        this.detUbigeoEleccionAgrupacionPoliticaRepository.deleteAllInBatch();
        this.candidatoRepository.deleteAllInBatch();
        this.candidatoFicticioRepository.deleteAllInBatch();
        this.candidatoRealRepository.deleteAllInBatch();
        this.personeroRepository.deleteAllInBatch();
        this.agrupacionPoliticaRepository.deleteAllInBatch();
        this.agrupacionPoliticaRealRepository.deleteAllInBatch();
        this.agrupacionPoliticaFicticioRepository.deleteAllInBatch();
        this.ubigeoEleccionRepository.deleteAllInBatch();
        this.padronElectoralRepository.deleteAllInBatch();
        this.escrutinioRepository.deleteAllInBatch();
        this.mesaRepository.deleteAllInBatch();
        this.localVotacionRepository.deleteAllInBatch();
        this.ubigeoRepository.deleteAllInBatch();
        this.admDcdehRepository.deleteAllInBatch();
        this.admDtedehRepository.deleteAllInBatch();
        this.detalleTipoEleccionDocumentoElectoralEscrutinioRepository.deleteAllInBatch();
        this.detDistritoElectoralEleccionRepository.deleteAllInBatch();
        this.eleccionRepository.deleteAllInBatch();
        this.ambitoElectoralRepository.deleteAllInBatch();
        this.distritoElectoralRepository.deleteAllInBatch();
        this.usuarioRepository.deleteAllInBatch();
        this.formatoRepository.deleteAllInBatch();
        this.puestaCeroRepository.deleteAllInBatch();
        this.procesoElectoralRepository.deleteAllInBatch();
        this.transmisionRecepcionRepository.deleteAllInBatch();
        this.transmisionEnvioRepository.deleteAllInBatch();
        this.importCierreCentroComputoRepository.deleteAllInBatch();
        this.importAccesoPcRepository.deleteAllInBatch();
        this.cabOtroDocumentoRepository.deleteAllInBatch();
        this.centroComputoRepository.deleteAllInBatch();
        this.adminDocumentoElectoralRepository.deleteAllInBatch();
        this.adminSeccionRepository.deleteAllInBatch();
        this.detParametroRepository.deleteAllInBatch();
        this.cabParametroRepository.deleteAllInBatch();
    }

    private void migrarMesa(String cc, String proceso, double startPercent, double endPercent) {
        String mensaje = "Se inició la migración de mesas";
        this.reportProgress(startPercent, mensaje, SceConstantes.ESTADO_PROGRESO_CONTINUA);
        
        ResponseEntity<PaginaOptDto<MesaDto>> response = null;
        Long lastId = 0L;
        int registrosProcesados = 0;
        int paginaActual = 1;
        PaginaOptDto<MesaDto> body = null;
        Integer totalRegistros = this.contarMesa(cc, proceso);
        
        if (totalRegistros == null || totalRegistros == 0) {
            this.reportProgress(endPercent, "No hay mesas para migrar", SceConstantes.ESTADO_PROGRESO_CONTINUA);
            return;
        }
        
        logger.info("Total de registros de mesas a importar {}", totalRegistros);
        
        do {
            response = this.importarMesa(cc, this.tamanioMesa, lastId, proceso);
            body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Respuesta nula desde servicio de exportación");
            }
            
            registrosProcesados += body.getData().size();
            List<ImportMesa> list = body.getData().stream().map(UtilMapper::convertirMesa).toList();
            this.mesaRepository.saveAll(list);
            lastId = body.getLastId();
            
            double progresoActual = calcularProgresoPaginado(startPercent, endPercent, 
                    registrosProcesados, totalRegistros);
            String mensajeProgreso = String.format("Migrando mesas - Página %d: %d/%d registros procesados", 
                paginaActual, registrosProcesados, totalRegistros);
            this.reportProgress(progresoActual, mensajeProgreso, SceConstantes.ESTADO_PROGRESO_CONTINUA);
            
            paginaActual++;
            
        } while (body.isNext());
        
        this.reportProgress(endPercent, 
            String.format("Finalizada migración de mesas - Total: %d registros", registrosProcesados), 
            SceConstantes.ESTADO_PROGRESO_CONTINUA);
        
        logger.info("total de registros de mesas importadas :{}", registrosProcesados);
    }

    private void migrarActa(String cc, String proceso, double startPercent, double endPercent) {
        String mensaje = "Se inició la migración de actas";
        this.reportProgress(startPercent, mensaje, SceConstantes.ESTADO_PROGRESO_CONTINUA);
        
        ResponseEntity<PaginaOptDto<CabActaDto>> response = null;
        Long lastId = 0L;
        int registrosProcesados = 0;
        int paginaActual = 1;
        PaginaOptDto<CabActaDto> body = null;
        Integer totalRegistros = this.contarActa(cc, proceso);
        
        if (totalRegistros == null || totalRegistros == 0) {
            this.reportProgress(endPercent, "No hay actas para migrar", SceConstantes.ESTADO_PROGRESO_CONTINUA);
            return;
        }
        
        logger.info("Total de registros de actas a importar {}", totalRegistros);
        
        do {
            response = this.importarActa(cc, this.tamanioActa, lastId, proceso);
            body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Respuesta nula desde servicio de exportación");
            }
            
            registrosProcesados += body.getData().size();
            List<ImportActa> list = body.getData().stream().map(UtilMapper::convertirCabActa).toList();
            this.cabActaRepository.saveAll(list);
            lastId = body.getLastId();
            
            double progresoActual = calcularProgresoPaginado(startPercent, endPercent, 
                    registrosProcesados, totalRegistros);
            String mensajeProgreso = String.format("Migrando actas - Página %d: %d/%d registros procesados", 
                paginaActual, registrosProcesados, totalRegistros);
            this.reportProgress(progresoActual, mensajeProgreso, SceConstantes.ESTADO_PROGRESO_CONTINUA);
            
            paginaActual++;
            
        } while (body.isNext());
        
        this.reportProgress(endPercent, 
            String.format("Finalizada migración de actas - Total: %d registros", registrosProcesados), 
            SceConstantes.ESTADO_PROGRESO_CONTINUA);
        
        logger.info("total de registros de actas importadas :{}", registrosProcesados);
    }

    private void migrarAmbitosElectorales(List<AmbitoElectoralDto> ambitosElectorales,
            ImportImportadorProgreso importador, String usuario) {
        if (ambitosElectorales != null && !ambitosElectorales.isEmpty()) {
            List<ImportAmbitoElectoral> entities1 = ambitosElectorales.stream()
                    .map(UtilMapper::convertirAmbitoElectoral).toList();
            this.ambitoElectoralRepository.saveAll(entities1);
            logger.info("Se ejecutó el método para insertar ambitos electorales");
        }
    }

    private void migrarCentroComputo(List<CentroComputoDto> centroComputo, ImportImportadorProgreso importador,
            String usuario) {
        if (centroComputo != null && !centroComputo.isEmpty()) {
            logger.info("Se inicio la migracion de centro de computo");
            List<ImportCentroComputo> list = centroComputo.stream().map(UtilMapper::convertirCentroComputo).toList();
            logger.info("guardando los centros de computo...");
            this.centroComputoRepository.saveAll(list);
        }
    }

    private void migrarAgrupacionesPoliticas(List<AgrupacionPoliticaDto> agrupacionPolitica,
            ImportImportadorProgreso importador, String usuario) {
        if (agrupacionPolitica != null && !agrupacionPolitica.isEmpty()) {
            List<ImportAgrupacionPolitica> list = agrupacionPolitica.stream()
                    .map(UtilMapper::convertirAgrupacionPolitica).toList();
            this.agrupacionPoliticaRepository.saveAll(list);
        }
    }

    private void migrarAgrupacionesPoliticasFicticios(List<AgrupacionPoliticaFicticioDto> agrupacionPolitica,
            ImportImportadorProgreso importador, String usuario) {
        if (agrupacionPolitica != null && !agrupacionPolitica.isEmpty()) {
            List<ImportAgrupacionPoliticaFicticia> list = agrupacionPolitica.stream()
                    .map(UtilMapper::convertirAgrupacionPoliticaFicticio).toList();
            this.agrupacionPoliticaFicticioRepository.saveAll(list);
        }
    }

    private void migrarAgrupacionesPoliticasReales(List<AgrupacionPoliticaRealDto> agrupacionPolitica,
            ImportImportadorProgreso importador, String usuario) {
        if (agrupacionPolitica != null && !agrupacionPolitica.isEmpty()) {
            List<ImportAgrupacionPoliticaReal> list = agrupacionPolitica.stream()
                    .map(UtilMapper::convertirAgrupacionPoliticaReal).toList();
            this.agrupacionPoliticaRealRepository.saveAll(list);
        }
    }

    private void migrarEleccionesPorAgrupacionesPoliticas(
            List<DetUbigeoEleccionAgrupacionPoliticaDto> ubigeoEleccionAgrupacionPolitica,
            ImportImportadorProgreso importador, String usuario) {
        if (ubigeoEleccionAgrupacionPolitica != null && !ubigeoEleccionAgrupacionPolitica.isEmpty()) {
            List<ImportDetUbigeoEleccionAgrupacionPolitica> entities = ubigeoEleccionAgrupacionPolitica.stream()
                    .map(UtilMapper::convertirUbigeoEleccionAgrupacionPolitica).toList();
            this.detUbigeoEleccionAgrupacionPoliticaRepository.saveAll(entities);
        }
    }

    private void migrarElecciones(List<EleccionDto> eleccion, ImportImportadorProgreso importador, String usuario) {
        if (eleccion != null && !eleccion.isEmpty()) {
            logger.info("Se procede a guardar las elecciones");
            List<ImportEleccion> list = eleccion.stream().map(UtilMapper::convertirEleccion).toList();
            this.eleccionRepository.saveAll(list);
            logger.info("Se ejecutó el método para insertar elecciones");
        }
    }

    private void migrarLocales(List<LocalVotacionDto> localVotacion, ImportImportadorProgreso importador,
            String usuario) {
        if (localVotacion != null && !localVotacion.isEmpty()) {
            logger.info("Se ejecutó el método para guardar locales de votacion");
            List<ImportLocalVotacion> localesEntity = localVotacion.stream().map(UtilMapper::convertirLocalVotacion)
                    .toList();
            this.localVotacionRepository.saveAll(localesEntity);
            logger.info("Se ejecutó el método para insertar locales");
        }
    }

    private void migrarUbigeosPorEleccion(List<UbigeoEleccionDto> ubigeoEleccion, ImportImportadorProgreso importador,
            String usuario) {
        if (ubigeoEleccion != null && !ubigeoEleccion.isEmpty()) {
            List<ImportUbigeoEleccion> entities10 = ubigeoEleccion.stream().map(UtilMapper::convertirUbigeoEleccion)
                    .toList();
            this.ubigeoEleccionRepository.saveAll(entities10);
        }
    }

    private void migrarDetallesActa(List<DetActaDto> detActa, ImportImportadorProgreso importador, String usuario) {
        if (detActa != null && !detActa.isEmpty()) {
            List<ImportDetActa> entities5 = detActa.stream().map(UtilMapper::convertirDetActa).toList();
            this.detActaRepository.saveAll(entities5);
        }
    }

    private void migrarDistritosElectorales(List<DistritoElectoralDto> distritoElectorales,
            ImportImportadorProgreso importador, String usuario) {
        if (distritoElectorales != null && !distritoElectorales.isEmpty()) {
            List<ImportDistritoElectoral> entities = distritoElectorales.stream()
                    .map(UtilMapper::convertirDistritoElectoral).toList();
            this.distritoElectoralRepository.saveAll(entities);
        }
    }

    private void migrarCandidatos(List<CandidatoDto> candidatos, ImportImportadorProgreso importador, String usuario) {
        if (candidatos != null && !candidatos.isEmpty()) {
            List<ImportCandidato> entitiesC = candidatos.stream().map(UtilMapper::convertirCandidato).toList();
            this.candidatoRepository.saveAll(entitiesC);
        }
    }

    private void migrarCandidatosFicticios(List<CandidatoFicticioDto> candidatos, ImportImportadorProgreso importador,
            String usuario) {
        if (candidatos != null && !candidatos.isEmpty()) {
            List<ImportCandidatoFicticio> entitiesC = candidatos.stream().map(UtilMapper::convertirCandidatoFicticio)
                    .toList();
            this.candidatoFicticioRepository.saveAll(entitiesC);
        }
    }

    private void migrarCandidatosReales(List<CandidatoRealDto> candidatos, ImportImportadorProgreso importador,
            String usuario) {
        if (candidatos != null && !candidatos.isEmpty()) {
            List<ImportCandidatoReal> entitiesC = candidatos.stream().map(UtilMapper::convertirCandidatoReal).toList();
            this.candidatoRealRepository.saveAll(entitiesC);
        }
    }

    private void migrarUbigeos(List<UbigeoDto> ubigeo, ImportImportadorProgreso importador, String usuario) {
        if (ubigeo != null && !ubigeo.isEmpty()) {
            logger.info("Se ejecutó el método para guardar ubigeos");
            List<ImportUbigeo> ubigeosEntity = ubigeo.stream().map(UtilMapper::convertirUbigeo).toList();
            this.ubigeoRepository.saveAll(ubigeosEntity);
        }
    }

    private void migrarCatalogo(List<CatalogoDto> orcCatalogos, ImportImportadorProgreso importador, String usuario) {
        if (orcCatalogos != null && !orcCatalogos.isEmpty()) {
            List<ImportOrcCatalogo> entities = orcCatalogos.stream().map(UtilMapper::convertirCabCatalogo).toList();
            this.catalogoRepository.saveAll(entities);
        }
    }

    private void migrarCatalogoEstructura(List<DetCatalogoEstructuraDto> orcCatalogoEstructura,
            ImportImportadorProgreso importador, String usuario) {
        if (orcCatalogoEstructura != null && !orcCatalogoEstructura.isEmpty()) {
            List<ImportOrcDetalleCatalogoEstructura> entities = orcCatalogoEstructura.stream()
                    .map(UtilMapper::convertirDetCatalogoEstructura).toList();
            this.detalleCatalogoEstructuraRepository.saveAll(entities);
        }
    }

    private void migrarCatalogoReferencia(List<DetCatalogoReferenciaDto> orcCatalogoReferencia,
            ImportImportadorProgreso importador, String usuario) {
        if (orcCatalogoReferencia != null && !orcCatalogoReferencia.isEmpty()) {
            List<ImportOrcDetalleCatalogoReferencia> entities = orcCatalogoReferencia.stream()
                    .map(UtilMapper::convertirDetCatalogoReferencia).toList();
            this.detalleCatalogoReferenciaRepository.saveAll(entities);
        }
    }

    private void migrarUsuarios(List<UsuarioDto> usuarios, ImportImportadorProgreso importador, String usuario) {
        if (usuarios != null && !usuarios.isEmpty()) {
            logger.info("Se ejecutó el método save de usuario");
            List<ImportUsuario> entitiesUsuario = usuarios.stream().map(UtilMapper::convertirUsuario).toList();
            this.usuarioRepository.saveAll(entitiesUsuario);
        }
    }

    private void migrarActasPreferenciales(List<DetActaPreferencialDto> actasPreferenciales,
            ImportImportadorProgreso importador, String usuario) {
        if (actasPreferenciales != null && !actasPreferenciales.isEmpty()) {
            logger.info("Se ejecutó el método save de actas preferenciales");
            List<ImportDetActaPreferencial> entitiesActaPreferenciales = actasPreferenciales.stream()
                    .map(UtilMapper::convertirDetActaPreferencial).toList();
            this.detActaPreferencialRepository.saveAll(entitiesActaPreferenciales);
        }
    }

    private void migrarMiembrosMesaSorteados(List<MiembroMesaSorteadoDto> miembroMesaSorteados,
            ImportImportadorProgreso importador, String usuario) {
        this.gestionarConstraintService.eliminarConstraintMiembroMesaSorteado();
        if (miembroMesaSorteados != null && !miembroMesaSorteados.isEmpty()) {
            logger.info("Se ejecutó el método save de miembros de mesa sorteados");
            List<ImportMiembroMesaSorteado> entitiesMiembroMesaSorteados = miembroMesaSorteados.stream()
                    .map(UtilMapper::convertirTabMiembroMesaSorteado).toList();
            this.miembroMesaSorteadoRepository.saveAll(entitiesMiembroMesaSorteados);
        }
    }

    private void migrarProcesosElectorales(List<ProcesoElectoralDto> procesoElectoral,
            ImportImportadorProgreso importador, String usuario) {
        if (procesoElectoral != null && !procesoElectoral.isEmpty()) {
            List<ImportProcesoElectoral> list = procesoElectoral.stream().map(UtilMapper::convertirProcesoElectoral)
                    .toList();
            this.procesoElectoralRepository.saveAll(list);
        }
    }

    private void migrarSeccion(List<AdmSeccionDto> secciones, ImportImportadorProgreso importador, String usuario) {
        if (secciones != null && !secciones.isEmpty()) {
            List<ImportSeccion> entities = secciones.stream().map(UtilMapper::convertirAdmTabSeccion).toList();
            this.adminSeccionRepository.saveAll(entities);
        }
    }

    private void migrarDocumentoElectoral(List<AdmDocElectoralDto> documentosElectorales,
            ImportImportadorProgreso importador, String usuario) {

        Map<Boolean, List<AdmDocElectoralDto>> particionados = documentosElectorales.stream()
                .collect(Collectors.partitioningBy(dto -> dto.getIdPadre() != null));

        List<AdmDocElectoralDto> padres = particionados.get(false);
        List<AdmDocElectoralDto> hijos = particionados.get(true);
;
        if (padres != null && !padres.isEmpty()) {
            List<ImportDocumentoElectoral> entitiesPadres = padres.stream()
                    .map(UtilMapper::convertirAdmTabDocumentoElectoral).toList();
            this.adminDocumentoElectoralRepository.saveAll(entitiesPadres);
        }

        if (hijos != null && !hijos.isEmpty()) {
            List<ImportDocumentoElectoral> entitiesHijos = hijos.stream()
                    .map(UtilMapper::convertirAdmTabDocumentoElectoral).toList();
            this.adminDocumentoElectoralRepository.saveAll(entitiesHijos);
        }
    }

    private void migrarDetalleTipoEleccionDocumentoElectoral(
            List<AdmDetTipoEleccionDocElectoralHistDto> tiposEleccionesDocumentoElectoral,
            ImportImportadorProgreso importador, String usuario) {
        if (tiposEleccionesDocumentoElectoral != null && !tiposEleccionesDocumentoElectoral.isEmpty()) {
            List<ImportDetalleTipoEleccionDocumentoElectoralHistorial> entities = tiposEleccionesDocumentoElectoral
                    .stream().map(UtilMapper::convertirAdmDetTipoEleccionDocumentoElectoralHistorial).toList();
            this.admDtedehRepository.saveAll(entities);
        }
    }

    private void migrarDetalleConfiguracionDocumentoElectoral(
            List<AdmDetConfigDocElectoralHistDto> detallesConfiguracionesDocumentoElectoral,
            ImportImportadorProgreso importador, String usuario) {
        if (detallesConfiguracionesDocumentoElectoral != null && !detallesConfiguracionesDocumentoElectoral.isEmpty()) {
            List<ImportDetalleConfiguracionDocumentoElectoralHistorial> entities = detallesConfiguracionesDocumentoElectoral
                    .stream().map(UtilMapper::convertirAdmDetConfiguracionDocumentoElectoralHistorial).toList();
            this.admDcdehRepository.saveAll(entities);
        }
    }

    private void migrarVersion(List<VersionDto> versiones, ImportImportadorProgreso importador, String usuario) {
        if (versiones != null && !versiones.isEmpty()) {
            List<ImportVersion> entities = versiones.stream().map(UtilMapper::convertirTabVersion).toList();
            this.versionRepository.saveAll(entities);
        }
    }

    private void migrarOpcionVoto(List<OpcionVotoDto> opcionesVoto, ImportImportadorProgreso importador,
            String usuario) {
        if (opcionesVoto != null && !opcionesVoto.isEmpty()) {
            List<ImportOpcionVoto> entities = opcionesVoto.parallelStream().map(UtilMapper::convertirOpcionVoto)
                    .toList();
            this.opcionVotoRepository.saveAll(entities);
        }
    }

    private void migrarDetActaOpcion(List<DetActaOpcionDto> detActaOpcion, ImportImportadorProgreso importador,
            String usuario) {
        if (detActaOpcion != null && !detActaOpcion.isEmpty()) {
            List<ImportDetActaOpcion> entities = detActaOpcion.stream().map(UtilMapper::convertirDetActaOpcion)
                    .toList();
            this.detActaOpcionRepository.saveAll(entities);
        }
    }

    private void migrarCabParametro(List<CabParametroDto> cabParametro, ImportImportadorProgreso importador,
            String usuario) {
        if (cabParametro != null && !cabParametro.isEmpty()) {
            List<ImportCabParametro> entities = cabParametro.stream().map(UtilMapper::convertirCabParametro).toList();
            this.cabParametroRepository.saveAll(entities);
        }
    }

    private void migrarDetParametro(List<DetParametroDto> detParametro, ImportImportadorProgreso importador,
            String usuario) {
        if (detParametro != null && !detParametro.isEmpty()) {
            List<ImportDetParametro> entities = detParametro.stream().map(UtilMapper::convertirDetParametro).toList();
            this.detParametroRepository.saveAll(entities);
        }
    }

    private void migrarDetDistritoElectoralEleccion(List<DetDistritoElectoralEleccionDto> detDistritoElectoralEleccion,
            ImportImportadorProgreso importador, String usuario) {
        if (detDistritoElectoralEleccion != null && !detDistritoElectoralEleccion.isEmpty()) {
            List<ImportDetDistritoElectoralEleccion> entities = detDistritoElectoralEleccion.stream()
                    .map(UtilMapper::convertirDetDistritoElectoralEleccion).toList();
            this.detDistritoElectoralEleccionRepository.saveAll(entities);
        }
    }

    private void migrarJuradoElectoralEspecial(List<TabJuradoElectoralEspecialDto> juradosElectoralEspecialDto,
            ImportImportadorProgreso importador, String usuario) {
       
        if (juradosElectoralEspecialDto != null && !juradosElectoralEspecialDto.isEmpty()) {
            logger.info("Se inicio la migracion de jurado electoral especial");
            List<ImportTabJuradoElectoralEspecial> list = juradosElectoralEspecialDto.stream()
                    .map(UtilMapper::convertirJuradoElectoralEspecial).toList();
            logger.info("guardando los jurados electorales especiales...");
            this.importCabJuradoElectoralEspecialRepository.saveAll(list);
        }
    }

    private void migrarVersionModelo(List<VersionModeloDto> versiones, ImportImportadorProgreso importador,
            String usuario) {
       
        if (versiones != null && !versiones.isEmpty()) {
            List<ImportVersionModelo> entities = versiones.stream().map(UtilMapper::convertirTabVersionModelo).toList();
            this.importVersionModeloRepository.saveAll(entities);
        }
    }

    private void resetearSecuencias() {
        this.generarSecuenciaService.resetearSecuencias();
    }

    private ImportarDto importarTablas(String centroc, String proceso) {
        ImportarRequest request = new ImportarRequest();
        request.setAcronimo(proceso);
        request.setCc(centroc);

        HttpEntity<ImportarRequest> httpEntity = new HttpEntity<>(request, getHeader(proceso));

        logger.info("Inicio de la exportacion");
        ResponseEntity<ImportarDto> response = this.clientExport.exchange(urlNacion + "exportar/", HttpMethod.POST,
                httpEntity, ImportarDto.class);
        logger.info("Fin de la exportacion");
        return response.getBody();
    }

    private ResponseEntity<PaginaOptDto<CabActaDto>> importarActa(String cc, int tamanoPagina, Long lastId,
            String proceso) {
        String url = urlNacion + "acta-exportacion/cc/" + cc;
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance().uri(URI.create(url));
        urlBuilder.replaceQueryParam("tamanoPagina", tamanoPagina);
        urlBuilder.replaceQueryParam("lastId", lastId);
        HttpEntity<Void> httpEntity = new HttpEntity<>(getHeader(proceso));
        return this.clientExport.exchange(urlBuilder.toUriString(), HttpMethod.GET, httpEntity,
                new ParameterizedTypeReference<PaginaOptDto<CabActaDto>>() {
                });
    }

    private ResponseEntity<PaginaOptDto<MesaDto>> importarMesa(String cc, int tamanoPagina, Long lastId,
            String proceso) {
        String url = urlNacion + "mesa-exportacion/cc/" + cc;
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance().uri(URI.create(url));
        urlBuilder.replaceQueryParam("tamanoPagina", tamanoPagina);
        urlBuilder.replaceQueryParam("lastId", lastId);
        HttpEntity<Void> httpEntity = new HttpEntity<>(getHeader(proceso));
        return this.clientExport.exchange(urlBuilder.toUriString(), HttpMethod.GET, httpEntity,
                new ParameterizedTypeReference<PaginaOptDto<MesaDto>>() {
                });
    }

    private Integer contarActa(String cc, String proceso) {
        HttpEntity<Void> httpEntity = new HttpEntity<>(getHeader(proceso));
        String urlCount = urlNacion + "acta-exportacion/cc/" + cc + "/count";

        ResponseEntity<Integer> countResponse = this.clientExport.exchange(urlCount, HttpMethod.GET, httpEntity,
                Integer.class);

        return countResponse.getBody();
    }

    private Integer contarMesa(String cc, String proceso) {
        HttpEntity<Void> httpEntity = new HttpEntity<>(getHeader(proceso));
        String urlCount = urlNacion + "mesa-exportacion/cc/" + cc + "/count";

        ResponseEntity<Integer> countResponse = this.clientExport.exchange(urlCount, HttpMethod.GET, httpEntity,
                Integer.class);

        return countResponse.getBody();
    }

    private HttpHeaders getHeader(String proceso) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
        headers.set(SceConstantes.TENANT_HEADER, proceso);
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}