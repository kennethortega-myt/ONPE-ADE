package pe.gob.onpe.scebackend.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.transmision.ArchivoTransmisionDto;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesArchivo;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


public class ArchivoUtils {

	static Logger logger = LoggerFactory.getLogger(ArchivoUtils.class);
	
	private static final Map<String, String> mimeTypeToExtensionMap;

	private ArchivoUtils(){
		
	}

    static {
        mimeTypeToExtensionMap = new HashMap<>();
        mimeTypeToExtensionMap.put("image/jpeg", "jpg");
        mimeTypeToExtensionMap.put("image/png", "png");
        mimeTypeToExtensionMap.put("image/gif", "gif");
        mimeTypeToExtensionMap.put("image/bmp", "bmp");
        mimeTypeToExtensionMap.put("image/tiff", "tiff");
        mimeTypeToExtensionMap.put("image/webp", "webp");
        mimeTypeToExtensionMap.put("text/plain", "txt");
        mimeTypeToExtensionMap.put("text/html", "html");
        mimeTypeToExtensionMap.put("text/css", "css");
        mimeTypeToExtensionMap.put("text/javascript", "js");
        mimeTypeToExtensionMap.put("application/json", "json");
        mimeTypeToExtensionMap.put("application/xml", "xml");
        mimeTypeToExtensionMap.put("application/pdf", "pdf");
        mimeTypeToExtensionMap.put("application/zip", "zip");
        mimeTypeToExtensionMap.put("application/gzip", "gz");
        mimeTypeToExtensionMap.put("audio/mpeg", "mp3");
        mimeTypeToExtensionMap.put("audio/ogg", "ogg");
        mimeTypeToExtensionMap.put("video/mp4", "mp4");
        mimeTypeToExtensionMap.put("video/x-msvideo", "avi");
        mimeTypeToExtensionMap.put("video/mpeg", "mpeg");
        mimeTypeToExtensionMap.put("application/vnd.ms-excel", "xls");
        mimeTypeToExtensionMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        mimeTypeToExtensionMap.put("application/msword", "doc");
        mimeTypeToExtensionMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        mimeTypeToExtensionMap.put("application/vnd.ms-powerpoint", "ppt");
        mimeTypeToExtensionMap.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
    }

    private static Archivo creaArchivoBase(String guid, String nombre, byte[] content, String mimeType, String ruta) {
        Archivo archivo = new Archivo();
        archivo.setPeso(SceUtils.formatBytes(content.length));
        archivo.setGuid(guid);
        archivo.setNombre(nombre);
        archivo.setNombreOriginal(nombre);
        archivo.setRuta(ruta);
        archivo.setFormato(mimeType);
        archivo.setFechaCreacion(new Date());
        archivo.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
        return archivo;
    }

    public static Archivo createArchivo(ArchivoTransmisionDto archivoDto, String path, String extension, String mimeType) throws FileNotFoundException {
        byte[] decodedBytes = Base64.getDecoder().decode(archivoDto.getBase64());
        String filename = UUID.randomUUID().toString();
        String filenameExt = String.format("%s.%s", filename, extension);
        String ruta = PathUtils.normalizePath(path, filenameExt);

        try(FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
            fileOutputStream.write(decodedBytes);
        } catch (IOException e) {
        	logger.error("error", e);
            return null;
        }
        return creaArchivoBase(filename, filenameExt,decodedBytes, mimeType,ruta);
    }
	
	public static Archivo createArchivo(ArchivoTransmisionDto archivoDto, String path, boolean generarUuid) throws IOException {
		String mimeType = archivoDto.getMimeType();
		String extension = archivoDto.getExtension();
        byte[] decodedBytes = Base64.getDecoder().decode(archivoDto.getBase64());
        String filename;
        if(generarUuid) {
        	filename = UUID.randomUUID().toString();
        	logger.info("Se genero el archivo con el uuid {}", filename);
        } else {
        	filename = archivoDto.getGuid();
        }
        String filenameExt;
        if(extension==null || extension.trim().isEmpty()) {
        	filenameExt = filename;
        } else {
        	filenameExt = String.format("%s.%s", filename, extension);
        }
         
        String ruta = PathUtils.normalizePath(path, filenameExt);
        
        try(FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
        	fileOutputStream.write(decodedBytes);
        } catch (IOException e) {
        	throw new GenericException("error al crear el archivo ", e.getMessage());
        }
        
        return creaArchivoBase(filename, filenameExt, decodedBytes, mimeType, ruta);
    }

	public static Archivo createArchivoPdf(ArchivoTransmisionDto archivoDto1, ArchivoTransmisionDto archivoDto2, String path)
            throws IOException, DocumentException {
		String extension = ConstantesArchivo.EXTENSION_PDF;
		String mimeType = ConstantesArchivo.MIME_TYPE_PDF;
		String filename = UUID.randomUUID().toString();
		String filenameExt = filename + "." + extension;
		byte[] decodedBytes;
		decodedBytes = PdfUtils.mergeImagesToPdf(archivoDto1.getBase64(),
				archivoDto2 == null ? null : archivoDto2.getBase64(), path, filenameExt);
        String ruta = PathUtils.normalizePath(path, filenameExt);
		return creaArchivoBase(filename, filenameExt, decodedBytes, mimeType, ruta);
	}
	
	
	public static Archivo createArchivoPdf(ArchivoTransmisionDto archivoDto1, String path) throws IOException, DocumentException {
		return createArchivoPdf(archivoDto1, null, path);
    }
	
	public static String convertToBase64(String filePath) throws IOException {
		try (FileInputStream fis = new FileInputStream(filePath);
	             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

	            byte[] buf = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = fis.read(buf)) != -1) {
	                bos.write(buf, 0, bytesRead);
	            }

	            byte[] imageBytes = bos.toByteArray();
	            return Base64.getEncoder().encodeToString(imageBytes);
	        }
	}
	
	public static String getExtension(String mimeType) {
        return mimeTypeToExtensionMap.getOrDefault(mimeType, "unknown");
    }
}
