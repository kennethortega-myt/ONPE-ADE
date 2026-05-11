package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.repository.ConfiguracionProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.RespaldoService;
import pe.gob.onpe.scebackend.model.service.UsuarioService;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesMensajes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RespaldoServiceImpl implements RespaldoService {

    private static final Logger logger = LoggerFactory.getLogger(RespaldoServiceImpl.class);
    private final ITabLogTransaccionalService logService;
    private final UsuarioService usuarioService;
    private final ConfiguracionProcesoElectoralRepository configuracionProcesoElectoralRepository;


    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${util.respaldo.path}")
    private String respaldoPath;

    @Value("${fileserver.path}")
    private String rootFolder;

    private String dbSchema = "";

    @Override
    public ResponseEntity<GenericResponse<byte[]>> backupDatabase(String apr, String ccc, String usr) {
        GenericResponse<byte[]> response = new GenericResponse<>();
        try {
            this.dbSchema = configuracionProcesoElectoralRepository.getEsquema(apr);
            if (Objects.equals(dbSchema, "")){
                logger.error("No existe un esquema para dump en principal para el acronimo {}", apr);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            logger.error("Error al obtener el proceso para dump del acronimo {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            String nombreBackup = obtenerNombreBackup(ccc, apr, response);
            if (nombreBackup == null) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String backupFile = rootFolder+"/"+nombreBackup;
            String[] command = {respaldoPath, "-h", getHost(), "-p", getPort(), "-U", dbUsername, "-n", dbSchema, "-F", "c", "-b", "-v", "-f", backupFile};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().put("PGPASSWORD", dbPassword);

            // Agregar registros para depuración
            if (logger.isInfoEnabled()) {
                logger.info("Para depuracion: {}", String.join(" ", command));
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            // Leer la salida del proceso
            String output = new String(process.getInputStream().readAllBytes());
            String errorOutput = new String(process.getErrorStream().readAllBytes());

            logger.info("Process output: {}",output);
            logger.info("Process error output: {}", errorOutput);
            logger.info("Process exit code: {}", exitCode);

            if (exitCode != 0) {
                this.logService.registrarLog(usr, "BackupDatabase", ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_BACKUP_FAIL,
                        apr, ccc, 0, 1);
                response.setSuccess(false);
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_BACKUP_FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            File file = new File(backupFile);
            if (!file.exists() || file.length() == 0) {
                response.setSuccess(false);
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_ARCHIVO_NO_ENCONTRADO);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            byte[] contents = Files.readAllBytes(file.toPath());

            response.setSuccess(true);
            response.setMessage(nombreBackup);
            response.setData(contents);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup.sql");

            this.logService.registrarLog(usr, "BackupDatabase", ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_BACKUP_DONE,
                    apr, ccc, 0, 1);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage());
            response.setMessage("Ocurrió un error al generar el archivo de respaldo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("El hilo fue interrumpido al intentar generar el respaldo: {}", e.getMessage());
            response.setMessage("La operación de generacion de respaldo fue interrumpida");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GenericResponse<String>> restoreDatabase(String apr, String ccc, String usr, MultipartFile file) {
        GenericResponse<String> response = new GenericResponse<>();
        response.setSuccess(false);
        try {
            this.dbSchema = configuracionProcesoElectoralRepository.getEsquema(apr);
            if (Objects.equals(dbSchema, "")){
                logger.error("No existe un esquema principal para el acronimo {}", apr);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            logger.error("Error al obtener el proceso del acronimo {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            if (file.isEmpty()) {
                response.setMessage("File is empty");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Guardar el archivo subido en la ruta /app/restore_backup.sql
            Path restoreFilePath = Path.of("/app/restore_backup.sql");
            Files.write(restoreFilePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String[] command = {respaldoPath+"pg_restore", "--clean", "-h", getHost(), "-p", getPort(), "-U", dbUsername, "-d", getDatabaseName(), "-n", dbSchema, "-v", restoreFilePath.toString()};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().put("PGPASSWORD", dbPassword);

            // Agregar registros para depuraciónl
            if (logger.isInfoEnabled()) {
                logger.info("Executing command: {}", String.join(" ", command));
            }

            Process process = processBuilder.start();
            logger.info("processBuilder.start()");

            // Leer la salida del proceso en un hilo separado para evitar bloqueos
            Thread outputThread = new Thread(() -> {
                try {
                    String output = new String(process.getInputStream().readAllBytes());
                    logger.info("Salida del proceso: {}", output);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });

            Thread errorThread = new Thread(() -> {
                this.logService.registrarLog(usr, "RestoreDatabase", ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_FAIL,
                        apr, ccc, 0, 1);
                try {
                    String errorOutput = new String(process.getErrorStream().readAllBytes());
                    logger.info("ErrorOutput: {}", errorOutput);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });

            outputThread.start();
            errorThread.start();

            int exitCode = process.waitFor();
            outputThread.join();
            errorThread.join();

            logger.info("Salida del proceso restore con el codigo: {}", exitCode);

            if (exitCode != 0) {
                String errorOutput = new String(process.getErrorStream().readAllBytes());
                logger.info("PSalida de restore error al procesar: {}", errorOutput);
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            response.setSuccess(true);
            response.setMessage("Database restored successfully");
            this.logService.registrarLog(usr, "RestoreDatabase", ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_DONE,
                    apr, ccc, 0, 1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage());
            response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("El hilo para restaurar el archivo fue interrumpido: {}", e.getMessage());
            response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_FAIL);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GenericResponse<String>> validarUsuarioConectados() {
        GenericResponse<String> response = new GenericResponse<>();
        String mensaje = "";
        StringBuilder usuariosConectados = new StringBuilder();
        List<Usuario> tabUsuarioList = this.usuarioService.findAll();
        for (Usuario tabUsuario : tabUsuarioList) {
            if (tabUsuario.getSesionActiva() == 1) {
                usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
            }
        }
        if (!usuariosConectados.isEmpty()) {
            usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
            usuariosConectados.append(".");
            mensaje = "Se encontraron usuarios conectados al sistema: ";
            mensaje += usuariosConectados;
            response.setSuccess(false);
            response.setMessage(mensaje);
        }else{
            response.setSuccess(true);
            response.setMessage("");
        }
        return new ResponseEntity<>(response, HttpStatus.OK );
    }

    private String getNombreBackup (String codigoCentroComputo, String acronimoProceso){
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        String fechaHora = ahora.format(formatter);
        return codigoCentroComputo+fechaHora+acronimoProceso+".sql";
    }

    private String getHost() {
        return dbUrl.split("/")[2].split(":")[0];
    }

    private String getPort() {
        return dbUrl.split("/")[2].split(":")[1];
    }

    private String getDatabaseName() {
        return dbUrl.split("/")[3];
    }

    private String obtenerNombreBackup(String ccc, String apr, GenericResponse<byte[]> response) {
        try {
            return this.getNombreBackup(ccc, apr);
        } catch (Exception e) {
            logger.error("Error al obtener el nombre del archivo: {}", e.getMessage());
            response.setSuccess(false);
            response.setMessage("No se pudo obtener el nombre del archivo");
            return null;
        }
    }
}

