package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.DetParametroDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IDetParametroMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetParametroService;

import org.springframework.stereotype.Service;

@Service
public class DetParametroServiceImpl implements DetParametroService {

  private final DetParametroRepository detParametroRepository;

  private final IDetParametroMapper detParametroMapper;

  public DetParametroServiceImpl(DetParametroRepository detParametroRepository, IDetParametroMapper detParametroMapper) {
    this.detParametroRepository = detParametroRepository;
    this.detParametroMapper = detParametroMapper;
  }

  @Override
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
  public List<DetParametroDto> listDetalleByParametro(Long idParametro) {
    try {
      return this.detParametroRepository.findByParametroId(idParametro).stream().map(this.detParametroMapper::entityToDTO).collect(
          Collectors.toList());
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

    @Override
    public void actualizarEstado(Integer activo, String usuario,Long idParametro) {
        try {
            this.detParametroRepository.actualizarEstado(activo, new Date(), usuario, idParametro);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }
}
