package pe.gob.onpe.sceorcbackend.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralDto;


@Component
public class StaeUtils {

	Logger logger = LoggerFactory.getLogger(StaeUtils.class);
	
	public Archivo createArchivo(DocumentoElectoralDto archivoDto, String path, boolean generarUuid) throws IOException {
		logger.info("Se procede a crear el archivo STAE");
		String mimeType = archivoDto.getMimeType();
		String extension = archivoDto.getExtension();
        Archivo archivo = new Archivo();
        byte[] decodedBytes = Base64.getDecoder().decode(archivoDto.getBase64());
        String filename = null;
        if(generarUuid) {
        	filename = UUID.randomUUID().toString();
        	logger.info("Se genero el archivo con el uuid {}", filename);
        } else {
        	filename = archivoDto.getGuid();
        }
        String filenameExt = null;
        if(extension==null || extension.trim().isEmpty()) {
        	filenameExt = filename;
        } else {
        	filenameExt = String.format("%s.%s", filename, extension);
        }
         
        String ruta = PathUtils.normalizePath(path, filenameExt);
        archivo.setPeso(StaeUtils.formatBytes(decodedBytes.length));
        archivo.setGuid(filename);
        archivo.setNombre(filenameExt);
        archivo.setNombreOriginal(filenameExt);
        archivo.setRuta(ruta);
        archivo.setFormato(mimeType);
        archivo.setFechaCreacion(new Date());
        
        try(FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
        	fileOutputStream.write(decodedBytes);
        } catch (IOException e) {
        	logger.error("Error al generar el archivo en centro de computo", e);
        	throw new GenericException("error al crear el archivo ", e.getMessage());
        }
        
        return archivo;
    }
	
	private static String formatBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
	
}
