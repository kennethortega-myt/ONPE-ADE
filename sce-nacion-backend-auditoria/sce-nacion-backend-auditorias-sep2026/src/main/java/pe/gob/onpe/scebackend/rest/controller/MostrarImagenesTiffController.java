package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("/visualizar")
public class MostrarImagenesTiffController {
    @Value("${fileserver.files:/data}")
    private String fileBasePath;
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String filename) {
        try {
            // Rutas posibles (.tiff o .tif)
            Path filePathTiff = Paths.get(fileBasePath).resolve(filename + ".tiff").normalize();
            Path filePathTif = Paths.get(fileBasePath).resolve(filename + ".tif").normalize();

            Path selectedPath = null;
            if (Files.exists(filePathTiff)) {
                selectedPath = filePathTiff;
            } else if (Files.exists(filePathTif)) {
                selectedPath = filePathTif;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado");
            }

            // Cargar la imagen TIFF
            BufferedImage tiffImage = ImageIO.read(selectedPath.toFile());
            if (tiffImage == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo leer la imagen TIFF");
            }

            // Convertir a JPEG
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(tiffImage, "jpeg", outputStream);
            byte[] jpegData = outputStream.toByteArray();

            // Devolver la imagen en formato JPEG
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 📌 Ahora es JPEG
                    .body(jpegData);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL inválida");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error al procesar la imagen");
        }
    }

}
