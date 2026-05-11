package pe.gob.onpe.sceorcbackend.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirmaCripto {
    Logger logger = LoggerFactory.getLogger(FirmaCripto.class);
    
    private String carpetaLocal;
    private String rutaJre;
    private String rutaScebackendFirma;
    private String certificate;
    private String keyStorePassword;
    private String alias;
    private String pin;
    private String tsl;
    private String operacion;
    private String reason;
    private String level;
    private String location;
    private String style;
    private String gloss;
    
    public FirmaCripto(String carpetaLocal, 
                        String rutaJre, 
                        String rutaScebackendFirma, 
                        String certificate, 
                        String keyStorePassword, 
                        String alias, 
                        String pin, 
                        String tsl, 
                        String operacion, 
                        String reason, 
                        String level, 
                        String location, 
                        String style, 
                        String gloss){
    
        this.carpetaLocal = carpetaLocal;
        this.rutaJre = rutaJre;
        this.rutaScebackendFirma = rutaScebackendFirma;
        this.certificate = certificate;
        this.keyStorePassword = keyStorePassword;
        this.alias = alias;
        this.pin = pin;
        this.tsl = tsl;
        this.operacion = operacion;
        this.reason = reason;
        this.level = level;
        this.location = location;
        this.style = style;
        this.gloss = gloss;
    }
    
public void procesoFirma(String documento) {
    String carpeta = carpetaLocal;

    // Validar parámetros críticos antes de ejecutar
    if (documento == null || documento.isBlank()) {
        logger.warn("El documento a firmar es nulo o vacío.");
        return;
    }

    File dir = new File(carpeta);
    if (!dir.exists() || !dir.isDirectory()) {
        logger.error("La carpeta de trabajo '{}' no existe o no es un directorio válido.", carpeta);
        return;
    }

    // Construcción segura del comando
    List<String> comando = Arrays.asList(
        "java", "-jar", rutaScebackendFirma,
        "-operacion", operacion,
        "-document", documento,
        "-reason", reason,
        "-certificate", certificate,
        "-keyStorePassword", keyStorePassword,
        "-alias", alias,
        "-pin", pin,
        "-location", location,
        "-style", style,
        "-gloss", gloss
    );

    ProcessBuilder pb = new ProcessBuilder(comando);
    pb.directory(dir);
    pb.redirectErrorStream(true); // Combina stdout y stderr

    logger.info("Iniciando proceso de firma para el documento: {}", documento);
    try {
        Process proceso = pb.start();

        // Capturar la salida del proceso (importante para depuración)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                logger.debug("[Firma {}] {}", documento, linea);
            }
        }

        int exitCode = proceso.waitFor();
        if (exitCode == 0) {
            logger.info("Proceso de firma completado exitosamente para {}", documento);
        } else {
            logger.error("El proceso de firma terminó con código de salida {} para {}", exitCode, documento);
        }

        } catch (IOException e) {
            logger.error("Error al iniciar el proceso de firma para {}: {}", documento, e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("El proceso de firma fue interrumpido para {}: {}", documento, e.getMessage());
        }
    }
    
}

