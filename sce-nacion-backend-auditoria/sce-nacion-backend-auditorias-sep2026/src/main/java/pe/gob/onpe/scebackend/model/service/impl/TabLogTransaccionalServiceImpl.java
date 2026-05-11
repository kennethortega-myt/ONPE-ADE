package pe.gob.onpe.scebackend.model.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.scebackend.model.orc.entities.TabLogTransaccional;
import pe.gob.onpe.scebackend.model.orc.repository.TabLogTransaccionalRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.impl.spec.LogFilter;
import pe.gob.onpe.scebackend.model.service.impl.spec.log.LogTransacionalSpec;
import pe.gob.onpe.scebackend.security.dto.LoginRequest;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.LoggingUtil;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TabLogTransaccionalServiceImpl implements ITabLogTransaccionalService {


  private final TabLogTransaccionalRepository tabLogRepository;


  private final TokenDecoder tokenDecoder;


  private final LogTransacionalSpec spec;

    public TabLogTransaccionalServiceImpl(TabLogTransaccionalRepository tabLogRepository, TokenDecoder tokenDecoder, LogTransacionalSpec spec) {
        this.tabLogRepository = tabLogRepository;
        this.tokenDecoder = tokenDecoder;
        this.spec = spec;
    }

    @Override
  public void save(TabLogTransaccional tabLogTransaccional) {
    try {
      this.tabLogRepository.save(tabLogTransaccional);
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

  @Override
  public void saveAll(List<TabLogTransaccional> k) {
    //not implement
  }

  @Override
  public void deleteAll() {
    //not implement
  }

  @Override
  public List<TabLogTransaccional> findAll() {
    return Collections.emptyList();
  }

  @Override
  public void registrarLog(String functionName, String serviceName, String message, String ambitoElectoral, String centroComputo,
      Integer autorizacion, Integer accion) {
    try {
      LoginRequest user = this.tokenDecoder.obtenerInfoUsuario();
      TabLogTransaccional log = new TabLogTransaccional();
      log.setData(LoggingUtil.logTransactions(functionName, serviceName, user.getAdicional(), message, "SUCCES"));
      log.setFechaRegistro(new Date());
      log.setUsuario(user.getUsername());
      log.setObservacion(message);
      log.setAmbitoElectoral(ambitoElectoral);
      log.setCentroComputo(centroComputo);
      log.setAutorizacion(autorizacion);
      log.setAccion(accion);
      this.tabLogRepository.save(log);
    } catch (Exception e) {
    	log.error("Error al registra log en el serviceName {}: ", serviceName, e);
    }
  }

  @Override
  public void registrarLog(String usuario, String functionName, String serviceName, String message, String ambitoElectoral,
      String centroComputo, Integer autorizacion, Integer accion) {
    try {
      TabLogTransaccional log = new TabLogTransaccional();
      log.setData(LoggingUtil.logTransactions(functionName, serviceName, usuario, message, "SUCCES"));
      log.setFechaRegistro(new Date());
      log.setUsuario(usuario);
      log.setObservacion(message);
      log.setAmbitoElectoral(StringUtils.isBlank(ambitoElectoral) ?"":ambitoElectoral.substring(0,6));
      log.setCentroComputo(StringUtils.isNotBlank(centroComputo) ? centroComputo.substring(0,6) : "");
      log.setAutorizacion(autorizacion);
      log.setAccion(accion);
      this.tabLogRepository.save(log);
    } catch (Exception e) {
      log.error("Error al registra log en el serviceName {}: ", serviceName, e);
    }
  }

  @Override
  public SearchFilterResponse<TabLogTransaccional> listPaginted( String error, Integer page, Integer size) {
    List<Sort.Order> orderList = new ArrayList<>();
    orderList.add(new Sort.Order(Direction.DESC, "fechaRegistro"));
    Sort sort = Sort.by(orderList);
    final Pageable pageable = PageRequest.of(page, size, sort);
    LogFilter logFilter =
        LogFilter.builder().error(error).build();
    Specification<TabLogTransaccional> specLog = this.spec.filter(logFilter);

    Page<TabLogTransaccional> logPage = this.tabLogRepository.findAll(specLog, pageable);
    List<TabLogTransaccional> logResponse = logPage.getContent();
    return new SearchFilterResponse<>(logResponse, page <= 1 ? logResponse.size() : (size * (page - 1)) + logResponse.size(), page,
        logPage.getTotalElements(), logPage.getTotalPages());
  }
}
