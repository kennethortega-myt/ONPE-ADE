package pe.gob.onpe.scebackend.model.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.exeption.NotFoundException;
import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.dto.response.DescargaResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.FileStorageResponseDTO;
import pe.gob.onpe.scebackend.model.entities.Archivo;
import pe.gob.onpe.scebackend.model.service.IArchivoService;
import pe.gob.onpe.scebackend.model.service.IFileStorageService;
import pe.gob.onpe.scebackend.utils.SceUtils;
import pe.gob.onpe.scebackend.utils.TokenUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageServiceImpl implements IFileStorageService {

    @Value("${fileserver.path}")
    private String rootFolder;

    @Value("${security.jwt.tokenSigningKey}")
    private String tokenSigningKey;

    @Autowired
    private IArchivoService archivoService;


    @Override
    public FileStorageResponseDTO save(String guid, MultipartFile file, String acronimo) throws IOException {
        Path root = Paths.get(rootFolder).resolve(acronimo);
        Files.createDirectories(root);

        Archivo archivo = archivoService.getArchivoByGuuid(guid);
        archivo.setNombreOriginal(file.getOriginalFilename());
        archivo.setNombre(file.getName());
        archivo.setFormato(FilenameUtils.getExtension(file.getOriginalFilename()));
        archivo.setPeso(String.valueOf(file.getSize()));
        archivo.setGuid(SceUtils.generarGUID());
        archivo.setRuta(rootFolder+ "//"+acronimo+"//"+archivo.getNombreOriginal());
        archivo.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
        archivo = archivoService.guardarArchivo(archivo);
        FileUtils.copyInputStreamToFile(file.getInputStream(),
                new File(root.resolve(archivo.getGuid() + "." + archivo.getFormato()).toUri()));
        FileStorageResponseDTO response = new FileStorageResponseDTO();
        response.setIdentificador(archivo.getId());
        return response;
    }

    @Override
    public FileStorageResponseDTO save(String guid, MultipartFile file) throws IOException {

        Path root = Paths.get(rootFolder);
        Archivo archivo = archivoService.getArchivoByGuuid(guid);
        archivo.setNombreOriginal(file.getOriginalFilename());
        archivo.setNombre(file.getName());
        archivo.setFormato(FilenameUtils.getExtension(file.getOriginalFilename()));
        archivo.setPeso(String.valueOf(file.getSize()));
        archivo.setGuid(SceUtils.generarGUID());
        archivo.setRuta(rootFolder+"//"+archivo.getNombreOriginal());
        archivo.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
        archivo = archivoService.guardarArchivo(archivo);
        FileUtils.copyInputStreamToFile(file.getInputStream(),
                new File(root.resolve(archivo.getGuid() + "." + archivo.getFormato()).toUri()));
        FileStorageResponseDTO response = new FileStorageResponseDTO();
        response.setIdentificador(archivo.getId());
        return response;
    }

    @Override
    public FileStorageResponseDTO update(String identificador) {
        return null;
    }

    @Override
    public void deleteAllByFolder(String folder) throws IOException {
        Path rootPath = Paths.get(rootFolder).toAbsolutePath().normalize();
        Path original = rootPath.resolve(folder).normalize();
        if (!original.startsWith(rootPath)) {
            throw new SecurityException("Ruta inválida: " + folder);
        }

        if (!Files.exists(original) || !Files.isDirectory(original)) {
            log.warn("La carpeta no existe o no es válida: {}", original);
            return;
        }
        Path backup = rootPath.resolve(folder + "_old_" + System.currentTimeMillis());
        try {
            Files.move(original, backup);
             log.info("Carpeta renombrada para limpieza: {}", backup);
            Files.createDirectories(original);
             log.info("Carpeta recreada: {}", original);

        } catch (IOException e) {
            log.error("Error al preparar limpieza de carpeta: {}", original, e);
            return;
        }
        new Thread(() -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb;
                if (os.contains("win")) {
                    // Windows
                    pb = new ProcessBuilder(
                            "cmd.exe", "/c", "rmdir", "/s", "/q", backup.toString()
                    );
                } else {
                    // Linux / Unix
                    pb = new ProcessBuilder(
                            "rm", "-rf", backup.toString()
                    );
                }
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("Eliminación completada: {}", backup);
                } else {
                    log.error("Error eliminando carpeta: {} (exitCode={})", backup, exitCode);
                }

            } catch (Exception e) {
                 log.error("Error en eliminación asíncrona de carpeta: {}", backup, e);
            }
        }, "delete-folder-thread").start();
    }
    @Override
    public DescargaResponseDTO get(Long idArchivo) throws IOException {

        ArchivoDTO archivo = archivoService.getArchivoById(idArchivo);
        Path root = Paths.get(rootFolder);
        Path file = root.resolve(archivo.getGuid() + "." + archivo.getFormato());
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() || resource.isReadable()) {
            return new DescargaResponseDTO(archivo.getNombreOriginal(), resource);
        } else {
            throw new NotFoundException("archivo no existe");
        }
    }

    @Override
    public ArchivoDTO get(String identificador, Integer tipoSustento) {
        return null;
    }

    @Override
    public FileStorageResponseDTO token(String identificador, String hash) {
        return new FileStorageResponseDTO(TokenUtil.generateSustento(identificador, hash, tokenSigningKey));
    }

    @Override
    public ByteArrayResource getArchivo(Integer identificador, Integer tipoSustento) throws IOException {
        return null;
    }

    @Override
    public ByteArrayResource getAdjunto(String folder, String archivo) throws IOException {
        Path root = Paths.get(rootFolder, folder);
        return new ByteArrayResource(Files.readAllBytes(root.resolve(archivo)));
    }
}
