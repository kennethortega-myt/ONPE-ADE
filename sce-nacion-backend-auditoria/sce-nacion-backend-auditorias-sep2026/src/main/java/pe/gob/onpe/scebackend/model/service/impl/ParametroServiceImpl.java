package pe.gob.onpe.scebackend.model.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ParametroDto;
import pe.gob.onpe.scebackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.scebackend.model.mapper.IDetParametroMapper;
import pe.gob.onpe.scebackend.model.mapper.IParametroMapper;
import pe.gob.onpe.scebackend.model.orc.entities.CabParametro;
import pe.gob.onpe.scebackend.model.orc.entities.DetParametro;
import pe.gob.onpe.scebackend.model.orc.repository.CabParametroRepository;
import pe.gob.onpe.scebackend.model.orc.repository.DetParametroRepository;
import pe.gob.onpe.scebackend.model.service.IParametroService;
import pe.gob.onpe.scebackend.model.service.impl.spec.parametro.ParametroFilter;
import pe.gob.onpe.scebackend.model.service.impl.spec.parametro.ParametroSpec;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.SceUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParametroServiceImpl implements IParametroService {

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
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
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
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public SearchFilterResponse<CabParametro> listPaginted(String parametro, Integer page, Integer size) {
        String perfil;
        List<Sort.Order> orderList = new ArrayList<>();
        UserContext user = this.tokenDecoder.getUsuarioSession2();
        if(!user.getAuthorities().isEmpty()){
            perfil = user.getAuthorities().getFirst().getAuthority();
        } else {
            perfil = "";
        }
        orderList.add(new Sort.Order(Sort.Direction.DESC, "fechaCreacion"));
        Sort sort = Sort.by(orderList);
        final Pageable pageable = PageRequest.of(page, size, sort);
        ParametroFilter parametroFilter =
                ParametroFilter.builder().parametro(parametro).perfil(perfil).build();
        Specification<CabParametro> specPametro = this.spec.filter(parametroFilter);

        Page<CabParametro> parametroPage = this.parametroRepository.findAll(specPametro, pageable);
        List<CabParametro> parametroResponse = parametroPage.getContent();
        return new SearchFilterResponse(parametroResponse.stream().filter(para-> para.getActivo().equals(SceConstantes.ACTIVO)).collect(Collectors.toList()),
                page <= 1 ? parametroResponse.size() : (size * (page - 1)) + parametroResponse.size(), page,
                parametroPage.getTotalElements(), parametroPage.getTotalPages());
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public void save(ParametroDto parametroDto) {
        CabParametro param;
        try {
            Optional<CabParametro> parametro = this.parametroRepository.findById(parametroDto.getId());
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
