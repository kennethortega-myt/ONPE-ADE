package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.mapper.IDetParametroMapper;
import pe.gob.onpe.sceorcbackend.model.mapper.IParametroMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetParametro;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CabParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetParametroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.parametro.ParametroFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.parametro.ParametroSpec;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ParametroServiceImpl implements ParametroService {

  private final IParametroMapper parametroMapper;

  private final CabParametroRepository parametroRepository;

  private final DetParametroRepository detParametroRepository;

  private final IDetParametroMapper detParametroMapper;

  private final ParametroSpec spec;

  private final TokenDecoder tokenDecoder;

  public ParametroServiceImpl(IParametroMapper parametroMapper, CabParametroRepository parametroRepository,
      DetParametroRepository detParametroRepository, IDetParametroMapper detParametroMapper, ParametroSpec spec, TokenDecoder tokenDecoder) {
    this.parametroMapper = parametroMapper;
    this.parametroRepository = parametroRepository;
    this.detParametroRepository = detParametroRepository;
    this.detParametroMapper = detParametroMapper;
    this.spec = spec;
    this.tokenDecoder = tokenDecoder;
  }

  @Override
  public ParametroDto obtenerParametro(String parametro) {
    ParametroDto paramResponse = new ParametroDto();
    try {
      if (StringUtils.isNotBlank(parametro)) {
        CabParametro cabParametro = this.parametroRepository.findByParametroAndActivo(parametro, SceConstantes.ACTIVO);
        if (Objects.nonNull(cabParametro)) {
          paramResponse = this.parametroMapper.entityToDTO(cabParametro);
          List<DetParametro> detParametro = this.detParametroRepository.findByActivoAndParametroId(SceConstantes.ACTIVO, cabParametro.getId());
          if (!detParametro.isEmpty()) {
            if (detParametro.size() == 1) {
              paramResponse.setValor(SceUtils.convertToType(detParametro.getFirst().getValor()));
            }
            if (detParametro.size() > 1) {
              paramResponse.setDetalles(detParametro.stream().map(this.detParametroMapper::entityToDTO).collect(Collectors.toList()));
            }

          }
        }
        return paramResponse;
      }
      return null;
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }


  @Override
  public SearchFilterResponse<CabParametro> listPaginted(TokenInfo tokenInfo,String parametro, Integer page, Integer size) {
    List<Sort.Order> orderList = new ArrayList<>();
    orderList.add(new Sort.Order(Direction.DESC, "fechaCreacion"));
    Sort sort = Sort.by(orderList);
    final Pageable pageable = PageRequest.of(page, size, sort);
    ParametroFilter parametroFilter =
        ParametroFilter.builder().parametro(parametro).perfil(tokenInfo.getAutority()).build();
    Specification<CabParametro> specPametro = this.spec.filter(parametroFilter);

    Page<CabParametro> parametroPage = this.parametroRepository.findAll(specPametro, pageable);
    List<CabParametro> parametroResponse = parametroPage.getContent();
    return new SearchFilterResponse(parametroResponse.stream().filter(para-> para.getActivo().equals(SceConstantes.ACTIVO)).toList(),
        page <= 1 ? parametroResponse.size() : (size * (page - 1)) + parametroResponse.size(), page,
        parametroPage.getTotalElements(), parametroPage.getTotalPages());
  }

  @Override
  public void save(ParametroDto parametroDto) {
    CabParametro param;
    try {
      Optional<CabParametro> parametro = this.parametroRepository.findById(parametroDto.getId().intValue());
      if (parametro.isPresent()) {
        param = parametro.get();
        param.setParametro(parametroDto.getParametro());
        param.setActivo(parametroDto.getActivo());
        param.setUsuarioModificacion(parametroDto.getUsuario());
        param.setFechaModificacion(new Date());
      } else {
        param = new CabParametro();
        param.setParametro(parametroDto.getParametro());
        param.setActivo(parametroDto.getActivo());
        param.setFechaCreacion(new Date());
        param.setUsuarioCreacion(parametroDto.getUsuario());
      }
      this.parametroRepository.save(param);
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

  @Override
  public void saveAll(List<ParametroDto> k) {

  }

  @Override
  public void deleteAll() {

  }

  @Override
  public List<ParametroDto> findAll() {
    return null;
  }

  public boolean perfilValido(String campo, String valorBuscado) {
    if (Objects.isNull(campo) || campo.trim().isEmpty()) {
      return false;
    }
    String[] valores = campo.split(",");
    for (String valor : valores) {
      if (valor.trim().equalsIgnoreCase(valorBuscado.trim())) {
        return true;
      }
    }
    return false;
  }
}
