package pe.gob.onpe.scebackend.model.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExtranjeroService {

    public boolean enviarActaOrc(MultipartFile filePdf, MultipartFile fileTif, String numeroActa, String copia,
            String digitoChequeo, String usuario, String centroComputo);

}
