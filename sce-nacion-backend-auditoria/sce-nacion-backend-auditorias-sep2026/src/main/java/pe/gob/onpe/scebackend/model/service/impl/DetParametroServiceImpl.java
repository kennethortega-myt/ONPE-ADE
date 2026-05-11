package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.DetParametroDto;
import pe.gob.onpe.scebackend.model.mapper.IDetParametroMapper;
import pe.gob.onpe.scebackend.model.orc.entities.DetParametro;
import pe.gob.onpe.scebackend.model.orc.repository.DetParametroRepository;
import pe.gob.onpe.scebackend.model.service.IDetParametroService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetParametroServiceImpl implements IDetParametroService {

    private final DetParametroRepository detParametroRepository;

    private final IDetParametroMapper detParametroMapper;

    public DetParametroServiceImpl(DetParametroRepository detParametroRepository, IDetParametroMapper detParametroMapper) {
        this.detParametroRepository = detParametroRepository;
        this.detParametroMapper = detParametroMapper;
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public void save(DetParametroDto detParametroDto) {
        try {
            Optional<DetParametro> detParametro = this.detParametroRepository.findById(detParametroDto.getId());
            if (detParametro.isPresent()) {
                detParametroDto.setUsuarioCreacion(detParametro.get().getUsuarioCreacion());
                detParametroDto.setFechaCreacion(detParametro.get().getFechaCreacion());
                detParametroDto.setFechaModificacion(new Date());
                detParametroDto.setUsuarioModificacion(detParametroDto.getUsuarioModificacion());
            } else {
                detParametroDto.setFechaCreacion(new Date());
                detParametroDto.setUsuarioCreacion(detParametroDto.getUsuarioCreacion());
            }
            this.detParametroRepository.save(this.detParametroMapper.dtoToEntity(detParametroDto));
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    public void saveAll(List<DetParametroDto> k) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<DetParametroDto> findAll() {
        return null;
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public List<DetParametroDto> listDetalleByParametro(Long idParametro) {
        try {
            return this.detParametroRepository.findByParametroId(idParametro).stream().map(this.detParametroMapper::entityToDTO).collect(
                    Collectors.toList());
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    public void actualizarEstado(Integer activo, String usuario, Long idParametro) {
        try {
            this.detParametroRepository.actualizarEstado(activo, new Date(), usuario, idParametro);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    public void actualizarValor(String valor, String usuario, String nombre) {
        try {
            this.detParametroRepository.actualizarValor(valor, new Date(), usuario, nombre);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }
}