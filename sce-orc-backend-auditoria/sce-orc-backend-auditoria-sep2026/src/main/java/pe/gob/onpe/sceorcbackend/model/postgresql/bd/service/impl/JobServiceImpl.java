package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.*;
import pe.gob.onpe.sceorcbackend.utils.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl {

  private static final AtomicBoolean pausado = new AtomicBoolean(false);

  private final MesaService mesaService;
  private final ActaRepository actaRepository;
  private final ActaCelesteRepository actaCelesteRepository;
  private final UsuarioRepository usuarioRepository;
  private final ActaTransmisionNacionRepository  transmisionNacionRepository;
  private final TabResolucionRepository  tabResolucionRepository;
  private final CabOtroDocumentoRepository  cabOtroDocumentoRepository;

  public static void pausarJobs() {
    log.info("Jobs pausados");
    pausado.set(true);
  }

  public static void reanudarJobs() {
    log.info("Jobs reanudados");
    pausado.set(false);
  }

  public static boolean isPausado() {
    return pausado.get();
  }

  @Scheduled(cron = "${app.orc.liberar.data.cron.expression}")
  @Transactional
  public void scheduleTaskWithCronExpression() {
    if (pausado.get()) {
      log.info("Job de liberación de mesas omitido (jobs pausados por puesta a cero)");
      return;
    }
    log.info("Se ejecuto el job para liberar mesas en estado editar");
    try {
      List<Mesa> listME = this.mesaService.listLiberarMesasME();
      log.info("Se liberaran {} mesas de escrutinio", listME.size());
      listME.forEach(m -> this.mesaService.actualizarEstadoDigitalizaionMEisEdit(m.getId(), "P"));

      List<Mesa> listPR = this.mesaService.listLiberarMesasPR();
      log.info("Se liberaran {} mesas de personeros", listPR.size());
      listPR.forEach(m -> this.mesaService.actualizarEstadoDigitalizaionPRisEdit(m.getId(), "P"));

      log.info("Se completaron el proceso de liberación de mesas");
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
  }


  @Scheduled(cron = "${app.orc.liberar.data.cron.expression}")
  @Transactional
  public void scheduleTaskWithCronExpressionActasDigitacion() {
    if (pausado.get()) {
      log.info("Job de liberación de actas omitido (jobs pausados por puesta a cero)");
      return;
    }
    log.info("Se ejecuta el job para liberar las actas y/o mesas por tiempo de inactividad");

    try {
        liberarActasEnDigitacion();
        liberarActasPorCorregir();
        liberarActasParaProcesamientoManual();
        liberarActaEnControlDeCalidad();
        liberarActasEnControlDigitalizacion();

        liberarLeEnControlDigitalizacion();
        liberarMmEnControlDigitalizacion();
        liberarVerificacionListaElectores();
        liberarVerificacionMiembrosMesa();

        liberarActasCelestesControlDigitalizacion();
        liberarDenunciasEnControlDigitalizacion();
        liberarResolucionesEnControlDigitalizacion();
        liberarResolucionEnVerificacionDeResoluciones();

      log.info("Se finalizó el job de liberación de actas y/o mesas por tiempo de inactividad.");
    } catch (Exception ex) {
      log.error("Error en el job de liberación de actas: {}", ex.getMessage(), ex);
    }
  }


  private void liberarActasEnDigitacion() {

    // Primer digitación
    List<ActaProjection> listActasTomadasPrimerDigitacion = this.actaRepository
            .findByEstadoActaAndVerificadorIsNotNullAndVerificador2IsNull(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA);

    // Segunda digitación
    List<ActaProjection> listActasTomadasSegundaDigitacion = this.actaRepository
            .findByEstadoActaAndVerificadorIsNotNullAndVerificador2IsNotNull(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);

    log.info("Actas en primera verificación: {} ", listActasTomadasPrimerDigitacion.size());
    log.info("Actas en segunda verificación: {}", listActasTomadasSegundaDigitacion.size());

    // Procesar ambas listas
    listActasTomadasPrimerDigitacion.forEach(m ->
            liberarSiInactivo(m.getId(), m.getVerificador(), Boolean.TRUE)
    );

    listActasTomadasSegundaDigitacion.forEach(m ->
            liberarSiInactivo(m.getId(), m.getVerificador2(), Boolean.FALSE)
    );
  }

  /**
   * Método genérico que valida inactividad y libera el acta.
   * @param idActa id del acta
   * @param verificador usuario que tiene tomada el acta
   * @param esPrimeraDigitacion true si es primera digitación, false si es segunda
   */
  private void liberarSiInactivo(Long idActa, String verificador, boolean esPrimeraDigitacion) {
    if (verificador == null) return;

    Usuario usuario = getUsuarioSiInactivo(verificador);
    if (usuario == null) return;

    if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
      if (esPrimeraDigitacion) {
        // Liberar primera digitación

        log.info("Se libera el acta (Primera Digitación) {}", idActa);

        this.actaRepository.updateEstadoActaAndVerificador(
                idActa, ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA, null
        );

        List<ActaTransmisionProjection> actaTransmisionProjections = this.transmisionNacionRepository.
                buscarTramasParaLiberar("ACTA", TransmisionNacionEnum.PRIMERA_VERI_TRANSMISION.name(),idActa);

        if(!actaTransmisionProjections.isEmpty()){
          ActaTransmisionProjection actaTransmisionProjection = actaTransmisionProjections.getLast();
          this.transmisionNacionRepository.inactivarPorId(actaTransmisionProjection.getId());
        }


      } else {
        // Liberar segunda digitación
        log.info("Se libera el acta (Segunda Digitación) {}", idActa);
        this.actaRepository.updateEstadoActaAndVerificador2(
                idActa, ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION, null
        );

        List<ActaTransmisionProjection> actaTransmisionProjections = this.transmisionNacionRepository.
                buscarTramasParaLiberar("ACTA", TransmisionNacionEnum.A_SEGUNDA_VERI_TRANSMISION.name(),idActa);

        if(!actaTransmisionProjections.isEmpty()){
          ActaTransmisionProjection actaTransmisionProjection = actaTransmisionProjections.getLast();
          this.transmisionNacionRepository.inactivarPorId(actaTransmisionProjection.getId());
        }

      }
    }
  }

  private void liberarActasPorCorregir() {
    List<ActaProjection> listaTomadas = this.actaRepository
            .buscarActasPorCorregirTomadas(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);

    listaTomadas.forEach(acta -> {
      Usuario usuario = getUsuarioSiInactivo(acta.getUsuarioCorreccion());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.actaRepository.liberarActasPorCorregir(acta.getId());
        log.info("Se libera el acta {}, del usuario {}, en el módulo Acta por Corregir.", acta.getId(), usuario.getUsuario());
      }
    });
  }

  private void liberarActasParaProcesamientoManual() {

    List<ActaProjection> listActas = this.actaRepository
            .buscarActaProcesamientoManualTomadas(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA);

    listActas.forEach(acta -> {
      Usuario usuario = getUsuarioSiInactivo(acta.getUsuarioProcesamientoManual());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.actaRepository.liberarActasProcesamientoManual(acta.getId());
        log.info("Se libera el acta {}, del usuario {}, en el módulo Procesamiento manual.",  acta.getId(), usuario.getUsuario());
      }
    });
  }

  private void liberarActaEnControlDeCalidad() {
    List<ActaProjection> listActasTomadas = this.actaRepository
            .buscarActasControlCalidadTomadas(
                    ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA,
                    ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);

    listActasTomadas.forEach(acta -> {

      Usuario usuario = getUsuarioSiInactivo(acta.getUsuarioControlCalidad());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.actaRepository.liberarActasControlCalidad(acta.getId());
        log.info("Se libera el acta {}, del usuario {}, en el módulo Control de Calidad.",  acta.getId(), usuario.getUsuario());
      }

    });
  }

  private void liberarActasEnControlDigitalizacion() {
    List<ActaProjection> listActasTomadas = this.actaRepository
            .buscarActaControlDigitalizacionTomadas(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);

    listActasTomadas.forEach(acta -> {
      Usuario usuario = getUsuarioSiInactivo(acta.getUsuarioAsignado());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.actaRepository.liberarActasControlDigitalizacion(acta.getId());
        log.info("Se libera el acta {}, del usuario {}, en el módulo Control de Digitalizacion de Actas.", acta.getId(), usuario.getUsuario());
      }
    });
  }

  private void liberarLeEnControlDigitalizacion() {

    List<MesaProjection> mesasTomadas = this.mesaService
            .buscarMesaLeControlDigtalTomadas(Arrays.asList(
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL
            ));

    mesasTomadas.forEach(mesa -> {

      Usuario usuario = getUsuarioSiInactivo(mesa.getUsuarioControlLe());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.mesaService.liberarMesaLeControlDigtalTomadas(mesa.getId());
        log.info("Se libera la mesa {}, del usuario {}, en el módulo Control de Digitalizacion de Lista de Electores.", mesa.getId(),usuario.getUsuario());
      }
    });
  }


  private void liberarMmEnControlDigitalizacion() {
    List<MesaProjection> mesasTomadas = this.mesaService
            .buscarMesamMmControlDigtalTomadas(List.of(
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA
            ));

    mesasTomadas.forEach(mesa -> {

      Usuario usuario = getUsuarioSiInactivo(mesa.getUsuarioControlMm());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.mesaService.liberarMesaMmControlDigtalTomadas(mesa.getId());
        log.info("Se libera la mesa {}, del usuario {}, en el módulo Control de Digitalizacion de Miembros de Mesa.", mesa.getId(),usuario.getUsuario());
      }

    });
  }


  private void liberarVerificacionListaElectores() {

    List<MesaProjection> mesasTomadas = this.mesaService
            .buscarMesaLeVerificacionTomadas(List.of(
                    ConstantesEstadoMesa.REPROCESAR,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_CON_PERDIDA_PARCIAL,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA
            ), ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE, ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL, ConstantesEstadoMesa.NO_INSTALADA);

    mesasTomadas.forEach(mesa -> {

      Usuario usuario = getUsuarioSiInactivo(mesa.getUsuarioAsignadoLe());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.mesaService.liberarMesaLeVerificacionTomadas(mesa.getId());
        log.info("Se libera la mesa {}, del usuario {}, en el módulo Registro de Omisos en LE.", mesa.getId(),usuario.getUsuario());
      }
    });
  }


  private void liberarVerificacionMiembrosMesa() {
    List<MesaProjection> mesasTomadas = this.mesaService
            .buscarMesaMmVerificacionTomadas(List.of(
                    ConstantesEstadoMesa.REPROCESAR,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA
            ), ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE,ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL, ConstantesEstadoMesa.NO_INSTALADA);

    mesasTomadas.forEach(mesa -> {
      Usuario usuario = getUsuarioSiInactivo(mesa.getUsuarioAsignadoMm());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        log.info("Se libera la mesa {}, del usuario {}, en el módulo Registro de Omisos en MM.", mesa.getId(),usuario.getUsuario());
        this.mesaService.liberarMesaMmVerificacionTomadas(mesa.getId());
      }
    });
  }

  private void liberarResolucionesEnControlDigitalizacion() {

    List<ResolucionProjection> listTomadas = this.tabResolucionRepository
            .buscarResolucionesControlDigitalizacionTomadas(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA);

    listTomadas.forEach(resolucion -> {
      Usuario usuario = getUsuarioSiInactivo(resolucion.getUsuarioControl());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.tabResolucionRepository.liberarResolucionesControlDigitlaizacion(resolucion.getId());
        log.info("Se libera la resolución {}, del usuario {}, en el módulo Control de Digitalizacion de Resoluciones.", resolucion.getNumeroResolucion(), usuario.getUsuario());
      }
    });

  }

  private void liberarResolucionEnVerificacionDeResoluciones() {


    List<Integer> tipoPermitidos = List.of(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE,
            ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_JNE,
            ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_ONPE);
    List<String> estadosPermitidos = List.of(ConstantesEstadoResolucion.EN_PROCESO);


    List<ResolucionProjection> listTomadas = this.tabResolucionRepository
            .buscarResolucionesVerificacionTomadas(estadosPermitidos, tipoPermitidos);

    listTomadas.forEach(resolucion -> {
      Usuario usuario = getUsuarioSiInactivo(resolucion.getAudUsuarioAsignado());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.tabResolucionRepository.liberarResolucionesVerificacion(resolucion.getId());
        log.info("Se libera la resolución {}, del usuario {}, en el módulo Verificación de resoluciones.", resolucion.getNumeroResolucion(), usuario.getUsuario());
      }
    });
  }


  private void liberarActasCelestesControlDigitalizacion() {

    List<ActaCelesteProjection> listActasTomadas = this.actaCelesteRepository
            .buscarActaCelesteControlDigitalizacionTomadas(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);

    listActasTomadas.forEach(actaCeleste -> {
      Usuario usuario = getUsuarioSiInactivo(actaCeleste.getUsuarioAsignado());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.actaCelesteRepository.liberarActasCelesteControlDigitalizacion(actaCeleste.getId());
        log.info("Se libera el acta celeste {}, del usuario {}, en el módulo Control de Digitalizacion de Actas Celestes.", actaCeleste.getId(), usuario.getUsuario());
      }
    });

  }



  private void liberarDenunciasEnControlDigitalizacion() {

    List<DenunciaProjection> listTomadas = this.cabOtroDocumentoRepository
            .buscarDenunciasControlDigitalizacionTomadas(
                    ConstantesOtrosDocumentos.ESTADO_DIGTAL_DIGITALIZADO,
                    ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_DENUNCIAS);

    listTomadas.forEach(denuncia -> {
      Usuario usuario = getUsuarioSiInactivo(denuncia.getUsuarioControl());
      if (usuario == null) return;

      if (isUsuarioInactivo(usuario.getFechaModificacion(), ConstantesComunes.TIEMPO_INACTIVIDAD_MINUTOS)) {
        this.cabOtroDocumentoRepository.liberarDenunciasControlDigitlaizacion(denuncia.getId());
        log.info("Se libera la denuncia {}, del usuario {}, en el módulo Control de Digitalizacion de Denuncias.", denuncia.getNumeroDocumento(), usuario.getUsuario());
      }
    });

  }





  /**
   * Verifica si un usuario ha estado inactivo por más de los minutos especificados.
   *
   * @param fechaUltimoLogin Fecha del último login o modificación del usuario.
   * @param minutosInactividad Tiempo máximo permitido en minutos.
   * @return true si el usuario ha superado el tiempo de inactividad.
   */
  private boolean isUsuarioInactivo(Date fechaUltimoLogin, int minutosInactividad) {
    if (fechaUltimoLogin == null) return true; // si no hay fecha, consideramos inactivo

    LocalDateTime ultimaFechaLogin = fechaUltimoLogin.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

    long minutosTranscurridos = ChronoUnit.MINUTES.between(ultimaFechaLogin, LocalDateTime.now());

    return minutosTranscurridos >= minutosInactividad;
  }


  private Usuario getUsuarioSiInactivo(String username) {
    Usuario usuario = this.usuarioRepository.findByUsuario(username);

    if (usuario == null) {
      return null; // no existe
    }

    if (Objects.equals(usuario.getSesionActiva(), ConstantesComunes.SESION_ACTIVO)) {
      return null; // está activo, no se usa para procesamiento
    }

    return usuario; // usuario válido e inactivo
  }


}
