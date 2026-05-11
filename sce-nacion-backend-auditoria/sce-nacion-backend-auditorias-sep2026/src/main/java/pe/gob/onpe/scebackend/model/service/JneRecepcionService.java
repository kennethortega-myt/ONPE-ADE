package pe.gob.onpe.scebackend.model.service;

import org.springframework.web.multipart.MultipartFile;

public interface JneRecepcionService {
    public void procesarRecepcion(MultipartFile pdf, String json, String codigoEnvio);

    public void reenviarPendientesJNE();
}
