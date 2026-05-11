package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.dto.ArchivoDTO;
import pe.gob.onpe.scebackend.model.entities.Archivo;
import pe.gob.onpe.scebackend.model.mapper.IArchivoMapper;
import pe.gob.onpe.scebackend.model.repository.ArchivoRepository;
import pe.gob.onpe.scebackend.model.service.IArchivoService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ArchivoServiceImpl implements IArchivoService {

    private final IArchivoMapper archivoMapper;

    private final ArchivoRepository archivoRepository;

    @Override
    public Archivo guardarArchivo(Archivo archivo) {
        return this.archivoRepository.save(archivo);
    }

    @Override
    public Archivo guardarArchivoDTO(ArchivoDTO archivoDTO) {
        return this.archivoRepository.save(this.archivoMapper.dtoToArchivo(archivoDTO));
    }

    @Override
    public ArchivoDTO getArchivoByGuid(String guid) {
        Optional<Archivo> archivo = this.archivoRepository.findByGuidAndActivo(guid, SceConstantes.ACTIVO);
        return archivo.map(this.archivoMapper::archivoToDTO).orElse(new ArchivoDTO());
    }

    @Override
    public Archivo getArchivoByGuuid(String guid) {
        return this.archivoRepository.findByGuidAndActivo(guid, SceConstantes.ACTIVO).orElse(new Archivo());
    }

    @Override
    public ArchivoDTO getArchivoById(Long id) {
        Optional<Archivo> archivo = this.archivoRepository.findById(id);
        return archivo.map(this.archivoMapper::archivoToDTO).orElse(new ArchivoDTO());
    }

}
