package pe.gob.onpe.scebackend.model.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.exeption.InternalServerErrorException;
import pe.gob.onpe.scebackend.model.dto.ParametroDto;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroDTO;
import pe.gob.onpe.scebackend.model.dto.TabVersionDTO;
import pe.gob.onpe.scebackend.model.orc.repository.VersionRepository;
import pe.gob.onpe.scebackend.model.repository.Procedures;
import pe.gob.onpe.scebackend.model.service.IParametroService;
import pe.gob.onpe.scebackend.model.service.IVerificaVersionService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesParametros;
import pe.gob.onpe.scebackend.utils.verificaversion.Utilitario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class VerificaVersionImpl implements IVerificaVersionService {

    Logger logger = LoggerFactory.getLogger(VerificaVersionImpl.class);

    @Value("${file.installer.dir}")
    private String dirInstaller;

    @Value("${file.scr.orc.front:orc_front.zip}")
    private String fileScrOrcFront;

    private final Procedures procedures;

    private final VersionRepository versionNacionRepository;


    private final UtilSceService utilSceService;

    private final IParametroService parametroService;



    public VerificaVersionImpl(
            Procedures procedures, VersionRepository versionNacionRepository,
            UtilSceService utilSceService,
            IParametroService parametroService
    ) {
        this.procedures = procedures;
        this.versionNacionRepository = versionNacionRepository;
        this.utilSceService = utilSceService;
        this.parametroService = parametroService;
    }


    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public Boolean puestaCero(String esquema, String usuario) {
        return this.procedures.executeProcedureVerificaVersionPuestaCero(esquema,usuario);
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public Boolean procesar(String usuario, String esquema) throws GenericException {

        AtomicReference<Boolean> resultadoProcedureRef = new AtomicReference<>(false);

        versionNacionRepository.findAll().stream().findFirst().ifPresent(version -> {

            long currentTimeMillis=System.currentTimeMillis();

            resultadoProcedureRef.set(this.procedures.executeProcedureVerificaVersionRegistro(
                esquema,
                usuario,
                obtenerCadenaDesdeArchivo(),
                ConstantesComunes.ACTIVO,
                new Timestamp(currentTimeMillis),
                version.getCodversion(),
                new Timestamp(currentTimeMillis)
            ));
        });

        return  resultadoProcedureRef.get();
    }


    private String obtenerCadenaDesdeArchivo() {
        try {
            String path = Paths.get(dirInstaller).resolve(fileScrOrcFront).toString();
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
    public byte[] reporteVerificaVersion(String nombreProceso, String acronimoProceso, String usuario, String esquema) throws JRException {

        AtomicReference<String> codigoVersionNacionRef = new AtomicReference<>("Hash no generado en nación");
        AtomicReference<String> fechaVersionNacionRef = new AtomicReference<>("-");

        versionNacionRepository.findAll().stream().findFirst().ifPresent(version -> {
            codigoVersionNacionRef.set(version.getCadena());
            fechaVersionNacionRef.set(DateUtil.getDateString(version.getFechaVersion(), SceConstantes.FORMATO_FECHA_REPORTE));
        });

        List<PuestaCeroDTO> puestaCeroDTOS = new ArrayList<>();
        PuestaCeroDTO puestaCeroDTO = new PuestaCeroDTO();
        puestaCeroDTO.setCampo1("1");
        puestaCeroDTO.setCampo2("");
        String fechaAuditoriaoRC = "";
        List<TabVersionDTO> registro = this.procedures.executeFunctionVerificaVersionObtenerRegistros(esquema);
        if (!registro.isEmpty()){
            puestaCeroDTO.setCampo2(registro.getFirst().getCadena());
            Date fechaCreacion2 = registro.getFirst().getFechaCreacion();
            fechaAuditoriaoRC = DateUtil.getDateString(fechaCreacion2 == null ? new Date() : fechaCreacion2, SceConstantes.FORMATO_FECHA_REPORTE);
        }

        puestaCeroDTO.setOmiCampo1(codigoVersionNacionRef.get().equals(puestaCeroDTO.getCampo2())?"IGUALES":"DIFERENTES");
        puestaCeroDTO.setOmiCampo2(fechaAuditoriaoRC);
        puestaCeroDTOS.add(puestaCeroDTO);

        Map<String, Object> parametros = new java.util.HashMap<>();
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe

        parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, imagen);
        parametros.put(ConstantesComunes.REPORT_PARAM_TITULO,nombreProceso);
        parametros.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE, ConstantesComunes.VERIFICA_VERSION_TITULO_REPORTE);
        parametros.put(ConstantesComunes.REPORT_PARAM_DESC_CC, ConstantesComunes.CC_NACION_CODIGO + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(acronimoProceso));
        parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
        parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, usuario);
        parametros.put(ConstantesComunes.REPORT_PARAM_CODIGO_VERSION_NACION, codigoVersionNacionRef.get());
        parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_VERSION_NACION, fechaVersionNacionRef.get());
        parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_CARGA_BDONPE, getFechaCargaBdOnpe());
        parametros.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, ConstantesComunes.VERIFICA_VERSION_JRXML);

        InputStream file = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_REPORT_VERIFICA_VERSION_JRXML + File.separator +
                ConstantesComunes.VERIFICA_VERSION_JRXML + ConstantesComunes.EXTENSION_REPORTES_JASPER);

        return JasperReportUtil.generarByteArray(parametros, puestaCeroDTOS, file);
    }

    private String getFechaCargaBdOnpe() {
        try {
            ParametroDto parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_CARGA_BDONPE);
            if(parametroDto !=null && parametroDto.getValor()!=null){
                String fechaCargaBDONPE = parametroDto.getValor().toString();
                String fechaCorta = fechaCargaBDONPE.substring(0, 19);

                DateTimeFormatter entradaFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(fechaCorta, entradaFormatter);

                DateTimeFormatter salidaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return  "Información según base de datos actualizada al " +  dateTime.format(salidaFormatter)+" horas.";
            }else{
                return "S/F";
            }
        } catch (DateTimeParseException e) {
            logger.error("Error getFechaCargaBdOnpe()", e);
            return "S/F";
        }

    }


}
