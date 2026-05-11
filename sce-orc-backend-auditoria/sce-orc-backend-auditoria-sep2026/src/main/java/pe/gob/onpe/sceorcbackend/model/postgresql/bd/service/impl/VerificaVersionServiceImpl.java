package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.puestacero.PuestaCeroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.util.HashModelo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.VersionCC;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.VersionModelo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.VersionCCRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.VersionModeloRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.VersionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificaVersionService;
import pe.gob.onpe.sceorcbackend.model.service.IExternalApiService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;
import pe.gob.onpe.sceorcbackend.utils.verificaversion.Utilitario;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class VerificaVersionServiceImpl implements VerificaVersionService {

    Logger logger = LoggerFactory.getLogger(VerificaVersionServiceImpl.class);

    @Value("${file.installer.dir}")
    private String dirInstaller;

    @Value("${file.scr.orc.front:orc_front.zip}")
    private String fileScrOrcFront;

    private final MaeProcesoElectoralService maeProcesoElectoralService;
    private final VersionRepository versionNacionRepository;
    private final VersionCCRepository versionCCRepository;
    private final UtilSceService utilSceService;
    private final IExternalApiService  externalApiService;
    private final ParametroService parametroService;
    private final VersionModeloRepository versionModeloRepository;
    private final DetParametroRepository detParametroRepository;

    public VerificaVersionServiceImpl(
            MaeProcesoElectoralService maeProcesoElectoralService,
            VersionRepository versionNacionRepository,
            VersionCCRepository versionCCRepository,
            UtilSceService utilSceService,ParametroService parametroService,
            IExternalApiService  externalApiService,
            VersionModeloRepository versionModeloRepository,
            DetParametroRepository detParametroRepository)
    {
        this.maeProcesoElectoralService = maeProcesoElectoralService;
        this.versionNacionRepository = versionNacionRepository;
        this.versionCCRepository = versionCCRepository;
        this.utilSceService = utilSceService;
        this.parametroService = parametroService;
        this.externalApiService = externalApiService;
        this.versionModeloRepository = versionModeloRepository;
        this.detParametroRepository = detParametroRepository;
    }

    @Override
    public void puestaCero(String usuario) {
        this.versionCCRepository.deleteAll();
    }

    @Override
    public String procesarOrc(String usuario, String centroComputo) {
        return procesarOrc(usuario, centroComputo, null);
    }

    @Override
    public String procesarOrc(String usuario, String centroComputo, String abrevProceso) {

        if (!versionCCRepository.findAll().isEmpty()) {
            return "Ya se ha procesado la verificación de versión.";
        }

        String cadena = obtenerCadenaDesdeArchivo(abrevProceso);

        VersionCC verificaCC = new VersionCC();
        verificaCC.setCadena(cadena);
        verificaCC.setActivo(ConstantesComunes.ACTIVO);
        verificaCC.setUsuarioCreacion(usuario);
        verificaCC.setFechaCreacion(new Date());

        versionNacionRepository.findAll().stream().findFirst().ifPresent(version -> {
            verificaCC.setCodversion(version.getCodversion());
            verificaCC.setFechaVersion(new Date());
        });

        versionCCRepository.save(verificaCC);
        return "Se procesó la verificación de versión correctamente.";
    }


    private String obtenerCadenaDesdeArchivo(String abrevProceso) {
        try {
            Path basePath = Paths.get(dirInstaller);
            if (abrevProceso != null && abrevProceso.contains(ConstantesComunes.PROCESO_SEP_ABREV)) {
                basePath = basePath.resolve(abrevProceso);
            }
            String path = basePath.resolve(fileScrOrcFront).toString();
            return Utilitario.getFileHashSHA256(path);

        } catch (InvalidPathException e) {
            throw new InternalServerErrorException(
                String.format("La ruta '%s/%s' no es válida. Detalles: %s", dirInstaller, fileScrOrcFront, e.getMessage())
            );
        } catch (FileNotFoundException e) {
            throw new InternalServerErrorException(
                String.format("El archivo '%s' no se encontró en el directorio '%s'. Detalles: %s", fileScrOrcFront, dirInstaller, e.getMessage())
            );
        } catch (NullPointerException e) {
            throw new InternalServerErrorException(
                String.format("Uno de los valores es nulo. Verifique 'dirInstaller': %s y 'fileScrOrcFront': %s. Detalles: %s",
                    dirInstaller, fileScrOrcFront, e.getMessage())
            );
        } catch (Exception e) {
            throw new InternalServerErrorException(
                String.format("Error inesperado al procesar el archivo '%s' en el directorio '%s'. Detalles: %s",
                    fileScrOrcFront, dirInstaller, e.getMessage())
            );
        }
    }

    @Override
    public byte[] reporteVerificaVersion(String acronimoProceso, String centroComputo, String ncc, String usuario) throws JRException {

        AtomicReference<String> codigoVersionNacionRef = new AtomicReference<>("Hash no generado en nación");
        AtomicReference<String> fechaAuditoriaNacionRef = new AtomicReference<>("-");

        versionNacionRepository.findAll().stream().findFirst().ifPresent(version -> {
            codigoVersionNacionRef.set(version.getCadena());
            fechaAuditoriaNacionRef.set(DateUtil.getDateString(version.getFechaVersion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_SLASHED));
        });

        AtomicReference<String> codigoVersionCcRef = new AtomicReference<>("");
        AtomicReference<String> fechaAuditoriaCcRef = new AtomicReference<>("-");

        versionCCRepository.findAll().stream().findFirst().ifPresent(versionCC -> {
            codigoVersionCcRef.set(versionCC.getCadena());
            Date fechaCreacion = versionCC.getFechaCreacion() != null ? versionCC.getFechaCreacion() : new Date();
            fechaAuditoriaCcRef.set(DateUtil.getDateString(fechaCreacion, SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_SLASHED));
        });

        String codigoVersionNacion = codigoVersionNacionRef.get();
        String fechaAuditoriaNacion = fechaAuditoriaNacionRef.get();
        String codigoVersionCc = codigoVersionCcRef.get();
        String fechaAuditoriaCc = fechaAuditoriaCcRef.get();

        PuestaCeroDTO puestaCeroDTO = new PuestaCeroDTO();
        puestaCeroDTO.setCampo1("1");
        puestaCeroDTO.setCampo2(codigoVersionCc);
        puestaCeroDTO.setOmiCampo1(codigoVersionNacion.equals(codigoVersionCc) ? 
            ConstantesComunes.ESTADO_COMPARACION_IGUALES : ConstantesComunes.ESTADO_COMPARACION_DIFERENTES);
        puestaCeroDTO.setOmiCampo2(fechaAuditoriaCc);

        List<PuestaCeroDTO> puestaCeroDTOS = Collections.singletonList(puestaCeroDTO);

        ModeloIntegridadResult modeloResult = procesarModeloIntegridad(usuario);

        Map<String, Object> parametros = new HashMap<>();
        ProcesoElectoral procesoElectoral = maeProcesoElectoralService.findByActivo();
        InputStream imagen = getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.NOMBRE_LOGO_ONPE);

        parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, imagen);
        parametros.put(ConstantesComunes.REPORT_PARAM_TITULO, procesoElectoral.getNombre());
        parametros.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE, ConstantesComunes.VERIFICA_VERSION_TITULO_REPORTE);
        parametros.put(ConstantesComunes.REPORT_PARAM_DESC_CC, centroComputo + "-" + ncc);
        parametros.put(ConstantesComunes.REPORT_PARAM_DESC_ODPE, centroComputo);
        parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());
        parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
        parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, usuario);
        parametros.put(ConstantesComunes.REPORT_PARAM_CODIGO_VERSION_NACION, codigoVersionNacion);
        parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_VERSION_NACION, fechaAuditoriaNacion);
        parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_CARGA_BDONPE, getFechaCargaBdOnpe());
        parametros.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, ConstantesComunes.VERIFICA_VERSION_JRXML);
        parametros.put(ConstantesComunes.REPORT_PARAM_MODELO_HASH_NACION, modeloResult.hashNacionModelo);
        parametros.put(ConstantesComunes.REPORT_PARAM_MODELO_NRO, "2");
        parametros.put(ConstantesComunes.REPORT_PARAM_MODELO_HASH, modeloResult.hashCcModelo);
        parametros.put(ConstantesComunes.REPORT_PARAM_MODELO_ESTADO, modeloResult.comparacionModelo);


        return Funciones.generarReporte(this.getClass(), puestaCeroDTOS,ConstantesComunes.VERIFICA_VERSION_JRXML + ConstantesComunes.EXTENSION_REPORTES_JASPER, parametros);
    }


    private String getFechaCargaBdOnpe(){
        try {
            ParametroDto parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_CARGA_BDONPE);
            if(parametroDto !=null && parametroDto.getValor()!=null){
                String fechaCargaBDONPE = parametroDto.getValor().toString();
                String fechaCorta = fechaCargaBDONPE.substring(0, 19);

                DateTimeFormatter entradaFormatter = DateTimeFormatter.ofPattern(SceConstantes.PATTERN_YYYY_MM_DD_MM_HH_MM_SS_DASH);
                LocalDateTime dateTime = LocalDateTime.parse(fechaCorta, entradaFormatter);

                DateTimeFormatter salidaFormatter = DateTimeFormatter.ofPattern(SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SLASHED);
                return  "Información según base de datos actualizada al " +  dateTime.format(salidaFormatter)+" horas.";
            }else{
                return "S/F";
            }
        } catch (DateTimeParseException e) {
            logger.error("Error getFechaCargaBdOnpe()", e);
            return "S/F";
        }

    }

    private ModeloIntegridadResult procesarModeloIntegridad(String usuario) {
        ModeloIntegridadResult result = new ModeloIntegridadResult();
        
        try {
            HashModelo hashModelo = this.externalApiService.modeloIntegrityHash();
            logger.info("hashModelo {}", hashModelo);

            if (hashModelo != null && hashModelo.getSha256() != null) {
                VersionModelo versionModeloNacion = this.versionModeloRepository.findTopByOrderByIdDesc();
                
                if (versionModeloNacion != null && versionModeloNacion.getCadena() != null) {
                    result.hashNacionModelo = versionModeloNacion.getCadena();
                    result.hashCcModelo = hashModelo.getSha256();
                    boolean sonIguales = result.hashNacionModelo.equals(result.hashCcModelo);
                    result.comparacionModelo = sonIguales ? 
                        ConstantesComunes.ESTADO_COMPARACION_IGUALES : ConstantesComunes.ESTADO_COMPARACION_DIFERENTES;
                    
                    actualizarParametroIntegridad(sonIguales, usuario);
                }
            }
        } catch (Exception ex) {
            logger.error("Error al obtener hash del modelo: {}", ex.getMessage(), ex);
        }
        
        return result;
    }

    private void actualizarParametroIntegridad(boolean sonIguales, String usuario) {
        try {
            String valorParametro = sonIguales ? ConstantesComunes.TEXT_TRUE : ConstantesComunes.TEXT_FALSE;
            this.detParametroRepository.actualizarValorPorParametroYNombre(
                valorParametro, new Date(), usuario,
                ConstantesParametros.CAB_PARAM_MODELO_INTEGRIDAD,
                ConstantesParametros.DET_PARAM_MODELO_INTEGRIDAD
            );
            logger.info("Parámetro de integridad del modelo actualizado a: {}", valorParametro);
        } catch (Exception ex) {
            logger.error("Error al actualizar parámetro de integridad del modelo: {}", ex.getMessage(), ex);
        }
    }

    private static class ModeloIntegridadResult {
        String hashNacionModelo = "N/A";
        String hashCcModelo = "N/A";
        String comparacionModelo = ConstantesComunes.ESTADO_COMPARACION_NO_DISPONIBLE;
    }
}
