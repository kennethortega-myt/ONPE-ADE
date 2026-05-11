package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DatosGeneralesResponseDto;
import pe.gob.onpe.scebackend.model.entities.Seccion;
import pe.gob.onpe.scebackend.model.mapper.ISeccionMapper;
import pe.gob.onpe.scebackend.model.repository.SeccionRepository;
import pe.gob.onpe.scebackend.model.service.ISeccionService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.List;

@Service
public class SeccionServiceImpl implements ISeccionService {

    private final ISeccionMapper seccionMapper;

    private final SeccionRepository seccionRepository;

    public SeccionServiceImpl(ISeccionMapper seccionMapper, SeccionRepository seccionRepository) {
        this.seccionMapper = seccionMapper;
        this.seccionRepository = seccionRepository;
    }


    @Transactional("tenantTransactionManager")
    @Override
    public void save(DatosGeneralesRequestDto datosGeneralesRequestDto) {
        this.seccionRepository.save(this.seccionMapper.dtoToSeccion(datosGeneralesRequestDto));
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<DatosGeneralesResponseDto> listAll() {
        return this.seccionRepository.findByActivo(SceConstantes.ACTIVO).stream().map(this.seccionMapper::seccionToDTO).toList();
    }

    @Transactional("tenantTransactionManager")
    @Override
    public void updateStatus(Integer status, Integer id) {
        this.seccionRepository.updateEstado(status, id);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public DatosGeneralesResponseDto save2(DatosGeneralesRequestDto seccion) throws GenericException {
        List<Seccion> secciones = seccionRepository.findAll();
        boolean nameExists = secciones.stream()
                .anyMatch(s -> s.getNombre().equalsIgnoreCase(seccion.getNombre()));

        if (nameExists) {
            throw new GenericException("Ya existe una sección con el mismo nombre: " + seccion.getNombre());
        }
        Seccion secci = seccionRepository.save(this.seccionMapper.dtoToSeccion(seccion));
        return this.seccionMapper.seccionToDTO(secci);
    }

}
