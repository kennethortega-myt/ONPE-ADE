package pe.gob.onpe.sceorcbackend.model.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.dto.RespaldoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;

import java.util.List;

public interface RespaldoService {
    ResponseEntity<GenericResponse<byte[]>> backupDatabase(String apr, String ccc, String usr);
    ResponseEntity<GenericResponse<String>> restoreDatabase(String apr, String ccc, String usr, MultipartFile file);
    ResponseEntity<GenericResponse<String>> validarUsuarioConectados();
    ResponseEntity<GenericResponse<List<RespaldoDTO>>> listarArchivosSql();
    void backupDatabaseAutomatico(String apr, String ccc, String usr);
    void eliminarBackupsMasAntiguos(int cantidad);
    void eliminarPuestaCero();
}
