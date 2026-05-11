package pe.gob.onpe.sceorcbackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.dto.RespaldoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.model.service.RespaldoService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesMensajes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RespaldoServiceImpl implements RespaldoService {

    private static final Logger logger = LoggerFactory.getLogger(RespaldoServiceImpl.class);
    private final ITabLogService logService;
    private final UsuarioService usuarioService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String dbSchema;

    @Value("${util.respaldo.path}")
    private String respaldoPath;

    @Value("${file.upload-dir}")
    private String rootFolder;

    @Value("${file.respaldo-dir}")
    private String respaldoFolder;

    @Value("${file.upload-subcarpeta}")
    private String subcarpeta;

    @Override
    public ResponseEntity<GenericResponse<byte[]>> backupDatabase(String apr, String ccc, String usr) {
        GenericResponse<byte[]> response = new GenericResponse<>();
        response.setSuccess(false);
        try {
            String nombreBackup = obtenerNombreBackup(ccc, apr, response);
            if (nombreBackup == null) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            String backupFile = "/app/"+nombreBackup;
            String[] command = {respaldoPath, "-h", getHost(), "-p", getPort(), "-U", dbUsername, "-n", dbSchema, "-F", "c", "-b", "-v", "-f", backupFile};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().put("PGPASSWORD", dbPassword);

            // Agregar registros para depuraciónl
            if (logger.isInfoEnabled()) {
                logger.info("backupDatabase command: {}", String.join(" ", command));
            }

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            // Leer la salida del proceso
            String output = new String(process.getInputStream().readAllBytes());
            String errorOutput = new String(process.getErrorStream().readAllBytes());

            logger.info("backupDatabase Output: {}",output);
            logger.info("backupDatabase Error output: {}", errorOutput);
            logger.info("backupDatabase Exit code: {}", exitCode);

            if (exitCode != 0) {
                this.logService.registrarLog(usr, Thread.currentThread().getStackTrace()[1].getMethodName(),
                        ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_BACKUP_FAIL, ccc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_BACKUP_FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            File file = new File(backupFile);
            if (!file.exists() || file.length() == 0) {
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_ARCHIVO_NO_ENCONTRADO);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            byte[] contents = Files.readAllBytes(file.toPath());

            response.setSuccess(true);
            response.setMessage(nombreBackup);
            response.setData(contents);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup.sql");

            this.logService.registrarLog(usr, Thread.currentThread().getStackTrace()[1].getMethodName(), ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_BACKUP_DONE, ccc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

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
        try {
            response.setSuccess(false);
            if (file.isEmpty()) {
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_ARCHIVO_NO_ENCONTRADO);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Path restoreFilePath = Path.of("/app/restore_backup.sql");
            Files.write(restoreFilePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String[] command = {respaldoPath+"pg_restore", "-h", getHost(), "-p", getPort(), "-U", dbUsername, "-d", getDatabaseName(), "-n", dbSchema, "-v", restoreFilePath.toString()};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().put("PGPASSWORD", dbPassword);

            // Agregar registros para depuración
            if (logger.isInfoEnabled()) {
                logger.info("Para depuracion: {}", String.join(" ", command));
            }

            Process process = processBuilder.start();
            logger.info("processBuilder.start()");

            // Leer la salida del proceso en un hilo separado para evitar bloqueos
            Thread outputThread = new Thread(() -> {
                try {
                    String output = new String(process.getInputStream().readAllBytes());
                    logger.info("Salida del proceso restore: {}", output);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            });

            Thread errorThread = new Thread(() -> {
                this.logService.registrarLog(usr, Thread.currentThread().getStackTrace()[1].getMethodName(), ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_FAIL, ccc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
                try {
                    String errorOutput = new String(process.getErrorStream().readAllBytes());
                    logger.info("Error restore salida con : {}", errorOutput);
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
                logger.info("Salida de restore error al procesar: {}", errorOutput);
                response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_FAIL);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            response.setSuccess(true);
            response.setMessage(ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_DONE);
            this.logService.registrarLog(usr, Thread.currentThread().getStackTrace()[1].getMethodName(),
                    ConstantesMensajes.MSJ_RESPALDO_NOMBRE_CLASE, ConstantesMensajes.MSJ_RESPALDO_LOG_RESTORE_DONE, ccc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
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
        response.setMessage("");
        StringBuilder usuariosConectados = new StringBuilder();
        List<Usuario> tabUsuarioList = this.usuarioService.findAll();
        for (Usuario tabUsuario : tabUsuarioList) {
            if (tabUsuario.getSesionActiva() == 1 && (
                    tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR) ||
                            tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER) ||
                            tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION))) {
                usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
            }
        }
        if (!usuariosConectados.isEmpty()) {
            usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
            usuariosConectados.append(".");
            String mensaje = "Se encontraron usuarios conectados al sistema: ";
            mensaje += usuariosConectados;
            response.setSuccess(false);
            response.setMessage(mensaje);
        }else{
            response.setSuccess(true);
        }
        return new ResponseEntity<>(response, HttpStatus.OK );
    }

    @Override
    public ResponseEntity<GenericResponse<List<RespaldoDTO>>> listarArchivosSql() {
        Path carpeta = Paths.get(respaldoFolder+subcarpeta+"/");
        GenericResponse<List<RespaldoDTO>> response = new GenericResponse<>();
        response.setMessage("");
        if (!Files.exists(carpeta) || !Files.isDirectory(carpeta)) {
            throw new IllegalArgumentException("Carpeta de respaldos no existe");
        }

        try (Stream<Path> paths = Files.list(carpeta)) {
            response.setSuccess(true);
            response.setData(paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".backup"))
                    .map(this::toDto)
                    .sorted(Comparator.comparing(RespaldoDTO::fechaHora).reversed())
                    .toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la carpeta", e);
        }
    }

    @Override
    public void backupDatabaseAutomatico(String apr, String ccc, String usr) {
        try {
            String nombreBackup = getNombreBackup(ccc, apr);

            Path carpetaRespaldos = Paths.get(respaldoFolder+subcarpeta+"/");

            if (Files.notExists(carpetaRespaldos)) {
                Files.createDirectories(carpetaRespaldos);
                logger.info("Carpeta de respaldos creada: {}", carpetaRespaldos);
            }
            String backupFile = carpetaRespaldos.resolve(nombreBackup).toString();

            String[] command = {
                    respaldoPath,
                    "-h", getHost(),
                    "-p", getPort(),
                    "-U", dbUsername,
                    "-n", dbSchema,
                    "-F", "c",
                    "-f", backupFile
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.environment().put("PGPASSWORD", dbPassword);
            pb.redirectErrorStream(true);
            logger.info("Ejecutando backup automático: {}", String.join(" ", command));
            Process process = pb.start();
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(line ->
                        logger.info("[pg_dump] {}", line)
                );
            }
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IllegalStateException("Error al ejecutar pg_dump");
            }

            File file = new File(backupFile);
            if (!file.exists() || file.length() == 0) {
                throw new IllegalStateException("Archivo de backup no generado");
            }
            logger.info("Backup generado correctamente: {}", nombreBackup);

        } catch (Exception e) {
            logger.error("Error en backup automático", e);
        }
    }


    @Override
    public void eliminarBackupsMasAntiguos(int cantidad) {

        logger.info("Iniciando limpieza de backups. Mantener los {} más recientes", cantidad);

        Path directorio = Paths.get(respaldoFolder+subcarpeta+"/");

        if (!Files.exists(directorio) || !Files.isDirectory(directorio)) {
            logger.warn("El directorio de backups no existe o no es válido: {}", respaldoFolder+subcarpeta);
            return;
        }

        try (Stream<Path> archivos = Files.list(directorio)) {

            List<Path> archivosSql = archivos
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".backup"))
                    .sorted(Comparator.comparingLong(this::lastModified))
                    .toList();

            if (archivosSql.isEmpty()) {
                logger.info("No se encontraron archivos .backup para evaluar limpieza");
                return;
            }

            int total = archivosSql.size();

            if (total <= cantidad) {
                logger.info(
                        "No se eliminan backups. Total existentes: {}, máximo permitido: {}",
                        total, cantidad
                );
                return;
            }

            int cantidadABorrar = total - cantidad;

            List<Path> archivosABorrar = archivosSql.subList(0, cantidadABorrar);

            archivosABorrar.forEach(this::eliminarArchivoSeguro);

            logger.info(
                    "Limpieza de backups finalizada. Eliminados: {}. Restantes: {}",
                    archivosABorrar.size(),
                    total - archivosABorrar.size()
            );

        } catch (IOException e) {
            logger.error("Error accediendo al directorio de backups", e);
        }
    }

    @Override
    public void eliminarPuestaCero() {

        logger.info("Iniciando limpieza de backups. Puesta Cero");

        Path directorio = Paths.get(respaldoFolder+subcarpeta+"/");

        if (!Files.exists(directorio) || !Files.isDirectory(directorio)) {
            logger.warn("El directorio de backups no existe o no es válido: {}", respaldoFolder+subcarpeta);
            return;
        }

        try (Stream<Path> archivos = Files.list(directorio)) {

            List<Path> archivosSql = archivos
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".backup"))
                    .sorted(Comparator.comparingLong(this::lastModified))
                    .toList();

            if (archivosSql.isEmpty()) {
                logger.info("No se encontraron archivos .backup para evaluar limpieza");
                return;
            }
            archivosSql.forEach(this::eliminarArchivoSeguro);
            logger.info("Limpieza de backups finalizada puesta cero. Eliminados: {}", archivosSql.size());
        } catch (IOException e) {
            logger.error("Error accediendo al directorio de backups", e);
        }
    }

    private RespaldoDTO toDto(Path path) {
        try {
            return new RespaldoDTO(
                    path.getFileName().toString(),
                    Date.from(
                            Files.getLastModifiedTime(path).toInstant()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNombreBackup (String codigoCentroComputo, String acronimoProceso){
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        String fechaHora = ahora.format(formatter);
        return codigoCentroComputo+fechaHora+acronimoProceso+".backup";
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

    private long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            logger.warn("No se pudo obtener fecha de modificación de {}", path.getFileName());
            return Long.MAX_VALUE;
        }
    }

    private void eliminarArchivoSeguro(Path path) {
        try {
            Files.deleteIfExists(path);
            logger.info("Archivo eliminado: {}", path.getFileName());
        } catch (IOException e) {
            logger.error("Error eliminando archivo {}", path.getFileName(), e);
        }
    }
}
