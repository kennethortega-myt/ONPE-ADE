package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabLog;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabLogRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UbigeoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.log.LogFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.log.LogSpec;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.LoggingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TabLogServiceImpl implements ITabLogService {


  private final TabLogRepository tabLogRepository;
  private final UbigeoService ubigeoService;
  private final LogSpec spec;

  public TabLogServiceImpl(
      TabLogRepository tabLogRepository,
      UbigeoService ubigeoService,
      LogSpec spec
  ) {
    this.tabLogRepository = tabLogRepository;
    this.ubigeoService = ubigeoService;
    this.spec = spec;
  }

  @Override
  public void save(TabLog tabLog) {
    try {
      this.tabLogRepository.save(tabLog);
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

  @Override
  public void saveAll(List<TabLog> k) {
    this.tabLogRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.tabLogRepository.deleteAll();
  }

  @Override
  public List<TabLog> findAll() {
    return this.tabLogRepository.findAll();
  }


  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void registrarLog(String usuario,
                           String functionName,
                           String serviceName,
                           String message,
                           String centroComputo,
                           Integer autorizacion,
                           Integer accion) {
    try {
      TabLog log = crearTabLog(usuario, functionName, serviceName, message, centroComputo, autorizacion, accion);
      this.tabLogRepository.save(log);
    } catch (Exception e) {
      log.error("Error al registra log en el serviceName {}: ", serviceName, e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void registrarLog(String usuario, String functionName, String message,
                           String centroComputo, Integer autorizacion, Integer accion) {
    try {
      String serviceName = obtenerClaseLlamadora();
      TabLog log = crearTabLog(usuario, functionName, serviceName, message, centroComputo, autorizacion, accion);
      this.tabLogRepository.save(log);
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

  private TabLog crearTabLog(String usuario, String functionName, String serviceName,
                             String message, String centroComputo, Integer autorizacion, Integer accion) {
    TabLog log = new TabLog();
    log.setData(LoggingUtil.logTransactions(functionName, serviceName, usuario, message, "SUCCES"));
    log.setFechaRegistro(new Date());
    log.setUsuario(usuario);
    log.setObservacion(message);
    log.setCentroComputo(centroComputo != null && centroComputo.length() >= 6 ? centroComputo.substring(0, 6) : "");

    Optional<String> codigoAmbito = this.ubigeoService.findCodigoAmbitoByCodigoCentroComputo(log.getCentroComputo());
    codigoAmbito.ifPresent(s -> log.setAmbitoElectoral(s.isEmpty() ? "" : s.substring(0, 6)));

    log.setAutorizacion(autorizacion);
    log.setAccion(accion);

    return log;
  }


  @Override
  public SearchFilterResponse<TabLog> listPaginted(String error, Integer page, Integer size) {
    List<Sort.Order> orderList = new ArrayList<>();
    orderList.add(new Sort.Order(Direction.DESC, "fechaRegistro"));
    Sort sort = Sort.by(orderList);
    final Pageable pageable = PageRequest.of(page, size, sort);
    LogFilter logFilter =
        LogFilter.builder().error(error).build();
    Specification<TabLog> specLog = this.spec.filter(logFilter);

    Page<TabLog> logPage = this.tabLogRepository.findAll(specLog, pageable);
    List<TabLog> logResponse = logPage.getContent();
    return new SearchFilterResponse(logResponse, page <= 1 ? logResponse.size() : (size * (page - 1)) + logResponse.size(), page,
        logPage.getTotalElements(), logPage.getTotalPages());
  }

  @Override
  @Transactional
  public void deleteByFechaRegistroBefore() {
    this.tabLogRepository.deleteByFechaRegistroBefore();
  }

  private String obtenerClaseLlamadora() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (int i = 2; i < stackTrace.length; i++) {
      String clase = stackTrace[i].getClassName();
      if (!clase.equals(this.getClass().getName()) && !clase.startsWith("java.lang.Thread")) {
        try {
          return Class.forName(clase).getSimpleName();
        } catch (ClassNotFoundException e) {
          return clase; // En caso no se pueda cargar la clase
        }
      }
    }
    return "Desconocida";
  }


}
