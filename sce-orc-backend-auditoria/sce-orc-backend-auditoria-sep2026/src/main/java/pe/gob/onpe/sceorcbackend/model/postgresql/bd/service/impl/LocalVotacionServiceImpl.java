package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.dto.LocalVotacionDTO;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.NivelUbigeoDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.LocalVotacionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.LocalVotacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.LocalVotacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.LocalVotacionService;

import java.util.List;

@Service
public class LocalVotacionServiceImpl implements LocalVotacionService {

    private final LocalVotacionRepository maeLocalVotacionRepository;

    public LocalVotacionServiceImpl(LocalVotacionRepository maeLocalVotacionRepository) {
        this.maeLocalVotacionRepository = maeLocalVotacionRepository;
    }

    @Override
    public void save(LocalVotacion k) {
        this.maeLocalVotacionRepository.save(k);
    }

    @Override
    public void saveAll(List<LocalVotacion> k) {
        this.maeLocalVotacionRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.maeLocalVotacionRepository.deleteAll();

    }


    @Override
    public List<LocalVotacion> findAll() {
        return this.maeLocalVotacionRepository.findAll();
    }

    @Override
    public List<LocalVotacionDTO> listarLocalesPorUbigeo(Long idUbigeo) {
        return this.maeLocalVotacionRepository.findLocalVotacionByUbigeo_Id(idUbigeo)
            .stream()
            .map(this::toLocalVotacionDto)
            .toList();

    }


    private LocalVotacionDTO toLocalVotacionDto(LocalVotacion localVotacion) {
        return LocalVotacionDTO.builder()
            .nombre(localVotacion.getNombre())
            .id(localVotacion.getId())
            .estado(localVotacion.getEstado())
            .build();
    }
}
