package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.InstaladorService;

@Service
public class InstaladorServiceImpl implements InstaladorService {

	@Value("${file.installer.dir}")
    private String dictorioInstalacion;
	
	@Value("${file.installer.name}")
    private String archivoInstalacion;
	
	
	public ByteArrayResource descargar() throws IOException {
        Resource resource = loadFile(archivoInstalacion);
        InputStream inputStream = resource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        return new ByteArrayResource(bytes);
    }

	@Override
	public ByteArrayResource descargar(String subPath) throws IOException {
        Resource resource;
        if (subPath != null && !subPath.isBlank()) {
            resource = loadFile(subPath, archivoInstalacion);
        } else {
            resource = loadFile(archivoInstalacion);
        }
        InputStream inputStream = resource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        return new ByteArrayResource(bytes);
    }
	
	private Resource loadFile(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(dictorioInstalacion).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new MalformedURLException("No se pudo cargar el archivo: " + fileName);
        }

        return resource;
    }

	private Resource loadFile(String subPath, String fileName) throws MalformedURLException {
        Path filePath = Paths.get(dictorioInstalacion).resolve(subPath).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new MalformedURLException("No se pudo cargar el archivo: " + subPath + "/" + fileName);
        }

        return resource;
    }
	
}
