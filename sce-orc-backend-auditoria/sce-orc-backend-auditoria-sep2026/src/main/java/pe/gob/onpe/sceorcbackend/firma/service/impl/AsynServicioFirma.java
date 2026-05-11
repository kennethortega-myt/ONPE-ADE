package pe.gob.onpe.sceorcbackend.firma.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.JobServiceImpl;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConvierteTIFFaPDF;
import pe.gob.onpe.sceorcbackend.utils.FirmaCripto;

@Service
public class AsynServicioFirma {
    
    Logger logger = LoggerFactory.getLogger(AsynServicioFirma.class);

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.server}")
    private String fileserver;

    @Value("${carpeta.local}")
    private String carpetaLocal;

    @Value("${ruta.jre}")
    private String rutaJre;

    @Value("${ruta.scebackend.firma}")
    private String rutaScebackendFirma;

    @Value("${certificate}")
    private String certificate;

    @Value("${key.store.password}")
    private String keyStorePassword;

    @Value("${alias}")
    private String alias;

    @Value("${pin}")
    private String pin;

    @Value("${tsl}")
    private String tsl;

    @Value("${operacion}")
    private String operacion;

    @Value("${reason}")
    private String reason;

    @Value("${level}")
    private String level;

    @Value("${location}")
    private String location;

    @Value("${style}")
    private String style;

    @Value("${gloss}")
    private String gloss;
    
    private final CentroComputoService ccService;
    private final ArchivoService archivoService;
    private final StorageService storageService;
    private final ConvierteTIFFaPDF convierteTIFFaPDF;
    private final ActaRepository cabActaRepository;
    
    public AsynServicioFirma(
    		CentroComputoService ccService,
    		ArchivoService archivoService,
    		StorageService storageService,
                ConvierteTIFFaPDF convierteTIFFaPDF,
                ActaRepository cabActaRepository) {
    	this.ccService = ccService;
    	this.archivoService = archivoService;
    	this.storageService = storageService;
        this.convierteTIFFaPDF = convierteTIFFaPDF; 
        this.cabActaRepository = cabActaRepository; 
    }
    
    @Async
    @Transactional
    public void procesarArchivoAsync(Long actaId, Long archivoId, String usuario, int tipo) throws IOException {
        if (JobServiceImpl.isPausado()) {
            logger.warn("Firma digital omitida para acta {} (puesta a cero en curso)", actaId);
            return;
        }

        logger.info("INICIANDO El FIRMADO");
    
        Optional<Acta> actaOpt = this.cabActaRepository.findById(actaId);
        if (actaOpt.isEmpty()) {
            logger.warn("Firma digital cancelada: acta {} no encontrada en BD", actaId);
            return;
        }
        Acta acta = actaOpt.get();

        Archivo archivo = null;
        if(tipo==ConstantesComunes.ACTA_INSTALACION_SUFRAGIO_FIRMA){
            archivo = acta.getArchivoInstalacionSufragio();
        }
        if(tipo==ConstantesComunes.ACTA_ESCRUTINIO_FIRMA){
            archivo= acta.getArchivoEscrutinio();
        }

        if (archivo == null) {
            logger.warn("Firma digital cancelada: archivo es null para acta {} tipo {}", actaId, tipo);
            return;
        }
        
        logger.info("Iniciando el firmado de {}", archivo.getNombre());
        logger.info("Archivo {}", archivo.getGuid());
        
        File fileTiff = this.getFileTiff(archivo);
        String pathPdf = this.getPathPdf(archivo);

        File pdf = this.convierteTIFFaPDF.convertTIFFToPDF(fileTiff, pathPdf);
        
        FirmaCripto firmaCripto = new FirmaCripto(carpetaLocal, rutaJre, rutaScebackendFirma, certificate, keyStorePassword, alias, pin, tsl, operacion, reason, level, location, style, gloss);
        firmaCripto.procesoFirma(pdf.getAbsolutePath());
     
        String pdfFirmado = this.getPathPdfSigned(pdf);
        logger.info("PDF firmado {}", pdfFirmado);
        
        logger.info("Esperando el firmado del documento.");
        File filePdfFirmado = null;
        for(int i=0;i<30;i++)
        {
            logger.info("Esperando el firmado del documento: {} - {}", pdfFirmado, i);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
                Thread.currentThread().interrupt();
            }
            filePdfFirmado= new File(pdfFirmado);
            if(filePdfFirmado.exists()){
                logger.info("Documento {} firmado.", pdfFirmado);
                break;
            }else{
                logger.info("Aun no existe el documento firmado {}", pdfFirmado);
            }
        }

        if(filePdfFirmado != null && filePdfFirmado.exists()){
            filePdfFirmado = new File(pdfFirmado);

            Tika tika = new Tika();
            String detectedType;
            try (InputStream targetStream = new FileInputStream(filePdfFirmado);) {
                detectedType = tika.detect(targetStream);
                
                Archivo archivoPdf = new Archivo();
                archivoPdf.setFormato(detectedType);
                Optional<CentroComputo> optionalCentroComputo = this.ccService.getCentroComputoActual();
                String prefix = "";
                if(optionalCentroComputo.isPresent())
                    prefix = optionalCentroComputo.get().getCodigo();

                archivoPdf.setPeso(String.valueOf(filePdfFirmado.length()));
                archivoPdf.setActivo(1);
                archivoPdf.setGuid(prefix.concat(ConstantesComunes.GUION_MEDIO).concat(DigestUtils.sha256Hex(targetStream)));
                String archivoFirmado = getFilenameSignedFinal(archivo);
                
                archivoPdf.setNombre(archivoFirmado);
                archivoPdf.setUsuarioCreacion(usuario);
                archivoPdf.setFechaCreacion(new Date());
                archivoPdf.setRuta(archivo.getRuta());
                archivoPdf.setCodigoDocumentoElectoral(tipo);

                this.archivoService.save(archivoPdf);
                this.storageService.storeFile(filePdfFirmado, archivoPdf.getGuid());

                if(tipo==ConstantesComunes.ACTA_INSTALACION_SUFRAGIO_FIRMA){
                    acta.setArchivoInstalacionSufragioFirmado(archivoPdf);
                }
                if(tipo==ConstantesComunes.ACTA_ESCRUTINIO_FIRMA){
                    acta.setArchivoEscrutinioFirmado(archivoPdf);
                } 
                this.cabActaRepository.save(acta);
            }
        }else{
            logger.info("No se pudo realizar la firma digital.");
        }

    }

    private File getFileTiff(Archivo file) {
        Path filePath = Paths.get(fileserver).resolve(file.getGuid());
        return new File(filePath.toFile().getAbsolutePath());
    }

    private String getPathPdf(Archivo file) {
        String filenameExt = String.format("%s.%s", file.getNombre().substring(0,file.getNombre().length()-4), "pdf");
        Path pathPdf = Paths.get(carpetaLocal).resolve(filenameExt);
        return pathPdf.toString();
    }
    
    private String getPathPdfSigned(File file) {
        return String.format("%s[F].%s", file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4), "pdf");
    }

    private String getFilenameSignedFinal(Archivo file) {   
        return String.format("%s[F].%s", file.getNombre().substring(0,file.getNombre().length()-4), "pdf");
    }
}

