package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import org.springframework.web.multipart.MultipartFile;

public interface JneRecepcionService {
    public void procesarRecepcion(MultipartFile pdf, String json, String codigoEnvio);
}
