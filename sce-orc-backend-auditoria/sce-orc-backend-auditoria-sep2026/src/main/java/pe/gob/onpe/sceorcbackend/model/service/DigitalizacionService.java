package pe.gob.onpe.sceorcbackend.model.service;

import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion.DigtalDTO;

public interface DigitalizacionService {
    void digitalizarListaElectores(MultipartFile file, String nroMesa, TokenInfo tokenInfo);

    void digitalizarMiembrosDeMesa(MultipartFile file, String nroMesa, TokenInfo tokenInfo);

    DigtalDTO digitalizarActa(MultipartFile file, String codeBar, TokenInfo tokenInfo);
    
    DigtalDTO digitalizarActaCeleste(MultipartFile file, String codeBar, TokenInfo tokenInfo);
    
    DigtalDTO digitalizarActaExtranjero(MultipartFile tif, MultipartFile pdf, String codeBar, TokenInfo tokenInfo);
}
