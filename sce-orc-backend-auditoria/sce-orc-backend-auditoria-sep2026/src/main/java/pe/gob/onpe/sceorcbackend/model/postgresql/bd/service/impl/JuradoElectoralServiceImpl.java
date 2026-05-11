package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.JuradoElectoralEspecialDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.JuradoElectoralEspecial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.JuradoElectoralEspecialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.JuradoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.juradoElectoral.JuradoElectoralEspecialFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.juradoElectoral.JuradoElectoralEspecialSpec;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class JuradoElectoralServiceImpl implements JuradoElectoralService {
  
  private final JuradoElectoralEspecialRepository electoralEspecialRepository;
  
  private final JuradoElectoralEspecialSpec electoralEspecialSpec; 
  
  private final CentroComputoRepository centroComputoRepository;
  
  private final OrcDetalleCatalogoEstructuraService catalogoEstructuraService;

  public JuradoElectoralServiceImpl(
          JuradoElectoralEspecialRepository electoralEspecialRepository, 
          JuradoElectoralEspecialSpec electoralEspecialSpec,
          CentroComputoRepository centroComputoRepository,
          OrcDetalleCatalogoEstructuraService catalogoEstructuraService) {
    this.electoralEspecialRepository = electoralEspecialRepository;
    this.electoralEspecialSpec = electoralEspecialSpec;
    this.centroComputoRepository = centroComputoRepository;
    this.catalogoEstructuraService = catalogoEstructuraService;
  }

  @Override
  public SearchFilterResponse<JuradoElectoralEspecialDto> listPaginted(String filter, Integer page, Integer size) {
      
      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
      
      JuradoElectoralEspecialFilter filtro = JuradoElectoralEspecialFilter.builder()
              .texto(filter)
              .build();
      
      Specification<JuradoElectoralEspecial> spec = electoralEspecialSpec.filter(filtro);
      Page<JuradoElectoralEspecial> pageResult = electoralEspecialRepository.findAll(spec, pageable);
      
      List<DetCatalogoEstructuraDTO> jeeCatalogo = catalogoEstructuraService.findByMaestroAndColumna(
              ConstantesCatalogo.CATALOGO_MAE_TIPO_JEE, ConstantesCatalogo.CATALOGO_DET_TIPO_JEE);
      Map<String, String> jeeMap = jeeCatalogo.stream()
              .collect(Collectors.toMap(DetCatalogoEstructuraDTO::getCodigoS, DetCatalogoEstructuraDTO::getNombre));
      
      List<JuradoElectoralEspecialDto> dtoList = pageResult.getContent().stream().map(entity -> {
          JuradoElectoralEspecialDto dto = new JuradoElectoralEspecialDto();

          dto.setId(entity.getId());
          dto.setDireccion(entity.getDireccion());
          dto.setApellidoPaterno(entity.getApellidoPaternoRepresentante());
          dto.setApellidoMaterno(entity.getApellidoMaternoRepresentante());
          dto.setNombreRepresentante(entity.getNombresRepresentante());
          
          if (entity.getCodigoCentroComputo() != null) {
              CentroComputo cc = centroComputoRepository.findByCodigo(entity.getCodigoCentroComputo())
                  .orElseThrow(() -> new IllegalStateException("Centro de Computo no encontrado: " + entity.getCodigoCentroComputo()));

              dto.setIdCentroComputo(cc.getId().intValue());
              dto.setCodigoCentroComputo(cc.getCodigo());
              dto.setNombreCentroComputo(cc.getNombre());
          }
          
          if (entity.getIdJee() != null) {
              dto.setIdJEE(entity.getIdJee());
              dto.setNombreJEE(jeeMap.getOrDefault(entity.getIdJee(), "SIN NOMBRE"));
          }
          
          return dto;
      }).toList();
      
      long totalElements = pageResult.getTotalElements();
      int totalPages = pageResult.getTotalPages();
      int totalCurrent = (page <= 1 ? dtoList.size() : (size * (page - 1)) + dtoList.size());

      return new SearchFilterResponse<>(
              dtoList,
              totalCurrent,
              page,
              totalElements,
              totalPages
      );
  }

  @Override
  public void save(JuradoElectoralEspecialDto dto) {
    try {
        JuradoElectoralEspecial entity = electoralEspecialRepository
                .findById(dto.getId())
                .orElseThrow(() -> new GenericException("No se encontró JEE con ID: " + dto.getId()));
        
        entity.setCodigoCentroComputo(dto.getCodigoCentroComputo());
        entity.setIdJee(dto.getIdJEE());
        entity.setDireccion(dto.getDireccion());
        entity.setApellidoPaternoRepresentante(dto.getApellidoPaterno());
        entity.setApellidoMaternoRepresentante(dto.getApellidoMaterno());
        entity.setNombresRepresentante(dto.getNombreRepresentante());
        entity.setUsuarioModificacion(dto.getUsuarioModificacion());
        entity.setFechaModificacion(new Date());
        electoralEspecialRepository.save(entity);
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

  @Override
  public void saveAll(List<JuradoElectoralEspecialDto> k) {
      throw new UnsupportedOperationException("saveAll no está implementado aún.");
  }

  @Override
  public void deleteAll() {
      throw new UnsupportedOperationException("deleteAll no está implementado aún.");
  }

  @Override
  public List<JuradoElectoralEspecialDto> findAll() {
      return Collections.emptyList();
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
