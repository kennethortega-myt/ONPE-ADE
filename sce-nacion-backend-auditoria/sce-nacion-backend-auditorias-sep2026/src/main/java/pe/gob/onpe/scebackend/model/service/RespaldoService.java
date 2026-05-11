package pe.gob.onpe.scebackend.model.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;

public interface RespaldoService {
    ResponseEntity<GenericResponse<byte[]>> backupDatabase(String apr, String ccc, String usr);
    ResponseEntity<GenericResponse<String>> restoreDatabase(String apr, String ccc, String usr, MultipartFile file);
    ResponseEntity<GenericResponse<String>> validarUsuarioConectados();
}
