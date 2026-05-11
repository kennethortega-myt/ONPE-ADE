package pe.gob.onpe.sceorcbackend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ArchivoTransmisionDto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ArchivoUtils {

    static Logger logger = LoggerFactory.getLogger(ArchivoUtils.class);

    private ArchivoUtils() {

    }

    private static final Map<String, String> mimeTypeToExtensionMap;


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


    public static ArchivoTransmisionDto convertToBase64(Archivo archivo, String path) {
        ArchivoTransmisionDto dto = null;
        try {
            String base64 = convertToBase64(PathUtils.normalizePath(path, archivo.getGuid()));
            dto = ArchivoTransmisionDto.builder()
                    .base64(base64)
                    .mimeType(archivo.getFormato())
                    .guid(archivo.getGuid())
                    .mimeType(archivo.getFormato())
                    .extension(getExtension(archivo.getFormato()))
                    .build();
        } catch (Exception e) {
            logger.error("error", e);
            return null;
        }
        return dto;
    }


    public static String convertToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file);
        		ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buf)) != -1) {
                bos.write(buf, 0, bytesRead);
            }
            byte[] fileBytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }
    
    public static String convertToBase64(byte[] fileByes) {
        return Base64.getEncoder().encodeToString(fileByes);
    }

    public static String getExtension(String mimeType) {
        return mimeTypeToExtensionMap.getOrDefault(mimeType, "unknown");
    }


}
