package pe.gob.onpe.sceorcbackend.model.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ActaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetActaResolucionPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ResolucionPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaAccionService;
import pe.gob.onpe.sceorcbackend.model.service.TrazabilidadService;
import pe.gob.onpe.sceorcbackend.utils.*;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.ItemHistory;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TrazabilidadServiceImpl implements TrazabilidadService {

  Logger logger = LoggerFactory.getLogger(TrazabilidadServiceImpl.class);


   private final DetActaAccionService detActaAccionService;

  public TrazabilidadServiceImpl(DetActaAccionService detActaAccionService){
    this.detActaAccionService = detActaAccionService;
  }


  @Override
  public ItemHistory switchItemHistoryByEstado(String estadoEvaluar, ActaTransmisionNacion atn, ActaPorTransmitirDto acta, int iRecibida, int iAprobada, int iVeri1, int iVeri2, int iPorCorregir) {
    return switch (estadoEvaluar) {
      case ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA ->
          createItemHistoryActaRecibidaItemHistory(atn, acta, estadoEvaluar, iRecibida);
      case ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA ->
          createItemHistoryRechazada(atn, acta, estadoEvaluar);
      case ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
          createItemHistoryAceptada(atn, acta, estadoEvaluar, iAprobada);
      case ConstantesEstadoActa.ESTADO_ACTA_DIGITADA + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
          createItemHistoryDigitada(atn, acta, estadoEvaluar, iVeri1);
      case ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE + ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA ->
          createItemHistoryRechazadaPrimeraDigitacion(atn, acta, estadoEvaluar);
      case ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
          createItemHistory2daVerificacion(atn, acta, estadoEvaluar, iVeri2);
      case ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO + ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
          createItemHistoryActaPorCorregir(atn, acta, estadoEvaluar, iPorCorregir);

      case ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO +
           ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA +
           ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->

          createItemHistoryEnvioAlJurado(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE +
           ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA +
           ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->

           createItemHistoryActaEnviadaAlJurado(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA +
           ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA +
           ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA ->
          createItemHistoryActaDuvuelta(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION +
           ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA +
           ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA -> //KOC

           createItemHistoryA(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_PROCESADA +
            ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA +
            ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA ->

           createItemHistoryB(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO +
            ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA +
            ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA ->

           createItemHistoryC(atn, acta, estadoEvaluar);

      case String s when s.startsWith(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA) ->
          createItemHistoryProcesadaNormal(atn, acta, estadoEvaluar);

      case String s when s.startsWith(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION + ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA) ->
          createItemHistoryProcesadaPorResolucion(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_NORMAL +
                   ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE +
                   ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION ->
          createItemHistoryF(atn, acta, estadoEvaluar);

      case "KNP" ->
          createItemHistoryG(atn, acta, estadoEvaluar);

      case "JPP" ->
          createItemHistoryH(atn, acta, estadoEvaluar);

      case String s when s.equals("JPC") && acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("R") ->
          createItemHistoryI(atn, acta, estadoEvaluar);

      case String s when s.equals("JPB") && acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("R") ->
              createItemHistoryCalidadReprocesada(atn, acta, estadoEvaluar);

      case "KPP" ->
          createItemHistoryJ(atn, acta, estadoEvaluar);

      case String s when s.equals("KPC") && acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("R") ->
          createItemHistoryK(atn, acta, estadoEvaluar);

      case String s when s.equals("KPB") && acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("R") ->
              createItemHistoryCalidadReprocesoAsociada(atn, acta, estadoEvaluar);

      case String s when Set.of("OOP", "RSP", "QSC","QSB", "RSC","RSB", "SOP").contains(s) && atn.getTransmite() == 1 ->
          switch (estadoEvaluar) {
            case "OOP" -> createItemHistoryL(atn, acta, estadoEvaluar);
            case "RSP" -> createItemHistoryM(atn, acta, estadoEvaluar);
            case "QSC" -> createItemHistoryN(atn, acta, estadoEvaluar);
            case "QSB" -> createItemHistoryNCalidad(atn, acta, estadoEvaluar);
            case "RSC" -> createItemHistoryO(atn, acta, estadoEvaluar);
            case "RSB" -> createItemHistoryO(atn, acta, estadoEvaluar);
            case "SOP" -> createItemHistoryP(atn, acta, estadoEvaluar);
            default -> null;
          };

      case "KOP", "KOD", "OOD", "TOC" ->
          dispatchItemHistory(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA +
          ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA +
          ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION ->
          createItemHistoryU(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA +
          ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA +
          ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION ->
          createItemHistoryV(atn, acta, estadoEvaluar);

      case ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA +
          ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA +
          ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION ->
          createItemHistoryW(atn, acta, estadoEvaluar);

      case String s when s.startsWith(ConstantesEstadoActa.ESTADO_ACTA_ANULADA +
              ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA) ->  createItemHistoryAnulada(atn, acta, estadoEvaluar);

      default -> {
        logger.info("Estado no mapeado, acta {}, estados{}",acta.getIdActa() ,estadoEvaluar);
        yield null;
      }

    };
  }

  private ItemHistory dispatchItemHistory(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return switch (estadoEvaluar) {
      case "KOP" -> createItemHistoryQ(atn, acta, estadoEvaluar);
      case "KOD" -> createItemHistoryR(atn, acta, estadoEvaluar);
      case "OOD" -> createItemHistoryS(atn, acta, estadoEvaluar);
      case "TOC" -> createItemHistoryT(atn, acta, estadoEvaluar);
      default -> null;
    };
  }

  private ItemHistory createItemHistoryN(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA CONTABILIZADA REPROCESADA",
        "El acta se encuentra en un estado de acta contabilizada por reprocesamiento por el usuario " + acta.getAudUsuarioModificacion() + ".",
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryNCalidad(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA CONTABILIZADA REPROCESADA - APROBADA EN CONTROL CALIDAD",
            "El acta se encuentra aprobada en control de calidad por el usuario " + acta.getAudUsuarioModificacion() + ".",
            ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryO(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {

    String estadoDigtal = obtenerEstadoDigitalizacion(estadoEvaluar);

    if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
      return  createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA ANULADA POR REPROCESAMIENTO",
              "El acta se encuentra en un estado de acta anulada por reprocesamiento por el usuario " + acta.getAudUsuarioModificacion() + ".",
              ConstantesComunes.VACIO);
    } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)) {
      return  createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA ANULADA POR REPROCESAMIENTO - APROBADA EN CONTROL DE CALIDAD",
              "El acta se aprobada en control de calidad por el usuario " + acta.getAudUsuarioModificacion() + ".",
              ConstantesComunes.VACIO);
    }else return null;
  }

  private ItemHistory createItemHistoryP(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA SINIESTRADA",
        "El acta fue declarada siniestrada por el usuario " + acta.getAudUsuarioModificacion() + ".",
        ConstantesComunes.VACIO);

  }

  private ItemHistory createItemHistoryQ(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {

    String textoResolucion = getNombreUltimaResolucion(acta, false);
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, ConstantesComunes.DESC_ACTA_ASOCIADA_RESOLUCION,
        String.format("El acta extraviada/siniestrada fue asociada a la resolución %S.", textoResolucion),
        ConstantesComunes.VACIO);

  }

  private ItemHistory createItemHistoryR(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, ConstantesComunes.DESC_ACTA_RECIBIDA,
        String.format("El acta extraviada/siniestrada fue encontrada, fue digitalizada por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryS(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("X")){
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, ConstantesComunes.DESC_ACTA_RECIBIDA,
          String.format("El acta declarada extraviada, fue digitalizada por el usuario %s.", acta.getAudUsuarioModificacion()),
          ConstantesComunes.VACIO);
    }else  if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("Y")){
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, ConstantesComunes.DESC_ACTA_RECIBIDA,
          String.format("El acta declarada siniestrada, fue digitalizada por el usuario %s.", acta.getAudUsuarioModificacion()),
          ConstantesComunes.VACIO);
    }else return null;
  }

  private ItemHistory createItemHistoryT(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().equals("X"))
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA APROBADA",
          "El acta declarada extraviada, fue aprobada por el usuario " + acta.getAudUsuarioModificacion() + ".",
          ConstantesComunes.VACIO);
    else if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().equals("Y"))
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA APROBADA",
          "El acta declarada siniestrada, fue aprobada por el usuario " + acta.getAudUsuarioModificacion() + ".",
          ConstantesComunes.VACIO);
    else return null;
  }

  private ItemHistory createItemHistoryU(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "MESA NO INSTALADA",
        String.format("El acta fue declarada como no instalada, por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryV(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA CONTABILIZADA ANULADA",
        String.format("El acta se encuentra contablizada anulada por extravío, por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryW(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA CONTABILIZADA ANULADA",
        String.format("El acta se encuentra contablizada anulada por siniestro, por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryG(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {

    String textoResolucion = getNombreUltimaResolucion(acta, false);

    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, ConstantesComunes.DESC_ACTA_ASOCIADA_RESOLUCION,
        String.format("El acta se encuentra asociada a la resolución %s.", textoResolucion),
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryH(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("X")){
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA EXTRAVIADA PARA REPROCESAMIENTO",
          "El acta extraviada, fue marcada para reprocesamiento.",
          ConstantesComunes.VACIO);
    }else if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("Y")){
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA SINIESTRADA PARA REPROCESAMIENTO",
          "El acta siniestrada, fue marcada para reprocesamiento.",
          ConstantesComunes.VACIO);
    }else
      return null;
  }

  private ItemHistory createItemHistoryI(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA PARA REPROCESAMIENTO",
        "El acta, fue marcada para reprocesamiento, por el usuario " +acta.getAudUsuarioModificacion() + ".",
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryCalidadReprocesada(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA PARA REPROCESAMIENTO",
            "El acta, fue marcada para reprocesamiento, por el usuario " +acta.getAudUsuarioModificacion() + ".",
            ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryJ(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("X")){
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA EXTRAVIADA PARA REPROCESAMIENTO",
          "El acta extraviada, marcada para reprocesamiento, se encuentra asociada a una resolución.",
          ConstantesComunes.VACIO);
    }else if(acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains("Y")){
      return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA SINIESTRADA PARA REPROCESAMIENTO",
          "El acta siniestrada, marcada para reprocesamiento, se encuentra asociada a una resolución.",
          ConstantesComunes.VACIO);
    }else return null;
  }

  private ItemHistory createItemHistoryK(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA ASOCIADA PARA REPROCESAMIENTO",
        "El acta, marcada para reprocesamiento, se encuentra asociada a una resolución "+getNombreUltimaResolucion(acta, false)+".",
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryCalidadReprocesoAsociada(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA ASOCIADA PARA REPROCESAMIENTO",
            "El acta, marcada para reprocesamiento, se encuentra asociada a la resolución "+getNombreUltimaResolucion(acta, false)+".",
            ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryL(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA EXTRAVIADA",
        "El acta fue declarada extraviada por el usuario " + acta.getAudUsuarioModificacion() + ".",
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryM(ActaTransmisionNacion actaTransmision, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(actaTransmision.getId(), acta, estadoEvaluar, "ACTA REPROCESADA ANULADA",
        "El acta se encuentra anulada por reprocesamiento por el usuario " + acta.getAudUsuarioModificacion() + ".",
        ConstantesComunes.VACIO);
  }


  private ItemHistory createItemHistoryA(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {
    String mensaje;

    String textoResolucion = getNombreUltimaResolucion(acta, false);

    if (acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion().contains("X")) {
      mensaje = String.format("El acta declarada extraviada, se encuentra asociada a la resolución %s.",textoResolucion);
    } else if (acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion().contains("Y")) {
      mensaje = String.format("El acta declarada siniestrada, se encuentra asociada a la resolución %s.",textoResolucion);
    } else {
      mensaje = String.format("El acta declarada, se encuentra asociada a la resolución %s.",textoResolucion);
    }

    return createItemHistory(atn.getId(), acta, estado, ConstantesComunes.DESC_ACTA_ASOCIADA_RESOLUCION, mensaje, ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryB(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {
    return createItemHistory(atn.getId(), acta, estado, "ACTA STAE INTEGRADA",
        "El acta STAE se ha integrado, se encuentra en estado Procesada Normal.", ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryC(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {
    return createItemHistory(atn.getId(), acta, estado, "ACTA STAE INTEGRADA",
        "El acta STAE se ha integrado, se encuentra en estado Para Envío al Jurado.", ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryProcesadaNormal(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {

    String estadoDigtal = obtenerEstadoDigitalizacion(estado);

    String textoStae = "";
    if(ConstantesComunes.TIPO_HOJA_STAE_TRANSMITIDA.toString().equals(atn.getTipoTransmision())){
      textoStae = ConstantesComunes.GUION_MEDIO + ConstantesComunes.SOLUCION_TECNOLOGICA_TEXT_STAE;
    }

    String textoResolucion = getNombreUltimaResolucion(acta, true);

    if(textoResolucion != null && !textoResolucion.isEmpty()) {
      if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
        return createItemHistory(atn.getId(),acta,estado, "ACTA ASOCIADA A RESOLUCIÓN",
                textoResolucion, ConstantesComunes.VACIO);
      } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)) {
        return createItemHistory(atn.getId(),acta,estado, "ACTA CONTABILIZADA NORMAL - APROBADA EN CONTROL DE CALIDAD - ASOCIADA A RESOLUCIÓN",
                textoResolucion, ConstantesComunes.VACIO);
      } else return null;

    } else {
      if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
        return createItemHistory(atn.getId(),acta,estado, "ACTA CONTABILIZADA NORMAL "+textoStae,
                String.format("El acta se encuentra contabilizada, fue digitada por el usuario %s.", acta.getAudUsuarioModificacion()), ConstantesComunes.VACIO);
      } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)) {
        return createItemHistory(atn.getId(),acta,estado, "ACTA CONTABILIZADA NORMAL - APROBADA EN CONTROL DE CALIDAD",
                String.format("El acta contabilizada fue aprobada en control de calidad por el usuario %s.", acta.getAudUsuarioModificacion()), ConstantesComunes.VACIO);
      } else return null;
    }
  }


  public String obtenerEstadoDigitalizacion(String juegoEstados) {

    if (juegoEstados == null) {
      return null;
    }

    if (juegoEstados.length() < 3) {
      return null;
    }

    return juegoEstados.substring(2, 3);

  }

  private ItemHistory createItemHistoryProcesadaPorResolucion(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {

    String estadoDigtal = obtenerEstadoDigitalizacion(estado);

    String textoResolucion = getNombreUltimaResolucion(acta, true);

    if(textoResolucion != null && !textoResolucion.isEmpty()) {
      if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
        return construirItemHistory(atn.getId(),estado, "ACTA ASOCIADA A RESOLUCIÓN",
                textoResolucion,
                acta.getAudFechaModificacion(), ConstantesComunes.VACIO);
      } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)) {
        return construirItemHistory(atn.getId(),estado, "ACTA CONTABILIZADA POR RESOLUCIÓN - APROBADA EN CONTROL DE CALIDAD - ASOCIADA A RESOLUCIÓN",
                textoResolucion,
                acta.getAudFechaModificacion(), ConstantesComunes.VACIO);
      } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION)) {
        return construirItemHistory(atn.getId(),estado, "ACTA CONTABILIZADA POR RESOLUCIÓN - ASOCIADA A RESOLUCIÓN",
            textoResolucion,
            acta.getAudFechaModificacion(), ConstantesComunes.VACIO);
      } else return null;


    } else {

      textoResolucion = getNombreUltimaResolucion(acta, false);

      if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
        return construirItemHistory(atn.getId(),estado, "ACTA CONTABILIZADA POR RESOLUCIÓN",
                String.format("El acta contabilizada por resolución, fue procesada con la resolución %s", textoResolucion),
                acta.getAudFechaModificacion(), ConstantesComunes.VACIO);
      } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)) {
        return construirItemHistory(atn.getId(),estado, "ACTA CONTABILIZADA POR RESOLUCIÓN - APROBADA EN CONTROL DE CALIDAD",
                String.format("El acta contabilizada por resolución, fue procesada con la resolución %s",textoResolucion),
                acta.getAudFechaModificacion(), ConstantesComunes.VACIO);
      } else if(estadoDigtal.equals(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION)) {
        return construirItemHistory(atn.getId(),estado, "ACTA CONTABILIZADA POR RESOLUCIÓN",
            String.format("El acta contabilizada por resolución, fue procesada con la resolución %s",textoResolucion),
            acta.getAudFechaModificacion(), ConstantesComunes.VACIO);
      } else return null;
    }

  }

  private ItemHistory createItemHistoryF(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {
    return createItemHistory(atn.getId(), acta, estado, "ACTA REPROCESADA",
        String.format("El acta fue reprocesada por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }


  private ItemHistory createItemHistoryActaDuvuelta(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(atn.getId(), acta, estadoEvaluar, "ACTA DEVUELTA",
        String.format("Se ha generado cargo de entrega de actas devueltas, por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);


  }

  private ItemHistory createItemHistoryActaEnviadaAlJurado(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(atn.getId(), acta, estadoEvaluar, "ACTA ENVIADA AL JEE",
        String.format("Se ha generado el cargo de entrega para envío al JEE, por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryEnvioAlJurado(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estadoEvaluar) {
    return createItemHistory(atn.getId(), acta, estadoEvaluar, "ACTA OBSERVADA",
        String.format("El acta se encuentra en un estado para envío al JEE. Fue digitada por el usuario %s.", acta.getAudUsuarioModificacion()),
        ConstantesComunes.VACIO);
  }


  private ItemHistory createItemHistoryActaPorCorregir(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado,  int iteracionCorregir) {
    return createItemHistory(atn.getId(), acta, estado,"ACTA POR CORREGIR",
        String.format("El acta se encuentra en un estado por corregir. Las digitaciones del primer verificador (%s) y segundo verificador (%s), no coinciden.",acta.getVerificador(), acta.getVerificador2()),
            this.fechaInicioFinProceso(acta, ConstantesComunes.DET_ACTA_ACCION_PROCESO_POR_CORREGIR, iteracionCorregir));
  }

  private ItemHistory createItemHistory2daVerificacion(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado, int iteracionSegundaVerificacion) {

    String detalle = "";
    String titulo = "ACTA EN SEGUNDA DIGITACIÓN";

    if(acta.getVerificador2()==null) {
      //primer caso
      detalle = "El acta se encuentra en un estado para realizar segunda digitación.";
      titulo = "ACTA PARA SEGUNDA DIGITACIÓN";
      return createItemHistory(atn.getId(), acta, estado, titulo, detalle, ConstantesComunes.VACIO);
    } else {
      detalle = "El acta se encuentra en segunda digitación, esta siendo procesado por el usuario "+acta.getVerificador2();
      return createItemHistory(atn.getId(), acta, estado, titulo, detalle,
              this.fechaInicioFinProceso(acta, ConstantesComunes.DET_ACTA_ACCION_PROCESO_2DA_VERI, iteracionSegundaVerificacion));
    }

  }

  private ItemHistory createItemHistoryRechazadaPrimeraDigitacion(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {
    return createItemHistory(atn.getId(), acta, estado, "ACTA RECHAZADA EN DIGITACIÓN DE ACTAS.", String.format("El acta fue rechazada por el usuario %s.", acta.getAudUsuarioModificacion()), ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryDigitada(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado, int iteracionPrimeraVerificacion) {
    return createItemHistory(atn.getId(), acta, estado, "ACTA EN PRIMERA DIGITACIÓN",
        "El acta se encuentra en primera digitación, está siendo procesada por el usuario " + acta.getVerificador()+".", this.fechaInicioFinProceso(acta, ConstantesComunes.DET_ACTA_ACCION_PROCESO_1ERA_VERI, iteracionPrimeraVerificacion));
  }


  private ItemHistory createItemHistoryAceptada(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado, int iteracionActaAprobada) {
    return createItemHistory(atn.getId(), acta, estado, ConstantesComunes.MSG_TRAZABILIDAD_PROCESO_ACTA_APROBADA_CTRL_DIGTAL,
        String.format("El acta fue aprobada por el usuario %s.", acta.getAudUsuarioModificacion()), this.fechaInicioFinProceso(acta, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_DIGTAL, iteracionActaAprobada));
  }

  private ItemHistory createItemHistoryRechazada(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {
    return createItemHistory(atn.getId(), acta, estado, ConstantesComunes.MSG_TRAZABILIDAD_PROCESO_ACTA_RECHAZADA_CTRL_DIGTAL,
        String.format("El acta fue rechazada por el usuario %s.", acta.getAudUsuarioModificacion()), ConstantesComunes.VACIO);
  }

  private ItemHistory createItemHistoryActaRecibidaItemHistory(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado, int iteracion) {
    return createItemHistory(atn.getId(), acta, estado, ConstantesComunes.DESC_ACTA_RECIBIDA, String.format("El acta se encuentra digitalizada en el centro de cómputo, por el usuario %s.", acta.getAudUsuarioModificacion()),
        this.fechaInicioFinProceso(acta, ConstantesComunes.DET_ACTA_ACCION_PROCESO_RECIBIDA, iteracion));
  }
  


  private ItemHistory createItemHistoryAnulada(ActaTransmisionNacion atn, ActaPorTransmitirDto acta, String estado) {

    String textoResolucion = getNombreUltimaResolucion(acta, false);

    if(textoResolucion != null && !textoResolucion.isEmpty()) {

      return createItemHistory(atn.getId(), acta, estado, "ACTA ANULADA POR RESOLUCIÓN", String.format("El acta fue asociada a la resolución %s.",textoResolucion), ConstantesComunes.VACIO);

    } else {
      return createItemHistory(atn.getId(), acta, estado, "ACTA ANULADA",
              "El acta se encuentra en un estado ANULADO.", ConstantesComunes.VACIO);
    }
  }


  public String fechaInicioFinProceso(ActaPorTransmitirDto acta, String proceso, Integer iteracion){
    List<DetActaAccion> detActaAccionList = this.detActaAccionService.findByActa_IdAndAccionAndIteracion(acta.getIdActa(), proceso, iteracion);
    //filtro los dos INICIO - FINA
    Optional<DetActaAccion> accionInicio = detActaAccionList.stream()
        .filter(detActaAccion -> detActaAccion.getTiempo().equals(ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI))
        .findFirst();

    Optional<DetActaAccion> accionFinal = detActaAccionList.stream()
        .filter(detActaAccion -> detActaAccion.getTiempo().equals(ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN))
        .findFirst();

    if (accionInicio.isPresent() && accionFinal.isPresent()) {

      Instant instant1 = accionInicio.get().getFechaAccion().toInstant();
      Instant instant2 = accionFinal.get().getFechaAccion().toInstant();
      Duration duration = Duration.between(instant1, instant2);
      long hours = duration.toHours();
      long minutes = duration.toMinutes() % 60;
      long seconds = duration.getSeconds() % 60;

      return String.format("<i><strong>Inicio:</strong> %s, <strong>Fin:</strong> %s, <strong>Duración:</strong>  %d horas, %d minutos, %d segundos.</i>",
          DateUtil.getDateString(accionInicio.get().getFechaAccion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH),
          DateUtil.getDateString(accionFinal.get().getFechaAccion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH),
          hours, minutes, seconds);

    } else if (accionInicio.isPresent()) {
      return String.format("<i><strong>Inicio:</strong> %s, <strong>Fin:</strong> %s.</i>", DateUtil.getDateString(accionInicio.get().getFechaAccion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH), " - ");

    } else if (accionFinal.isPresent()) {
      return String.format("<i><strong>Inicio:</strong> %s, <strong>Fin:</strong> %s.</i>", " - ", DateUtil.getDateString(accionFinal.get().getFechaAccion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
    } else {
      return String.format("<i><strong>Inicio:</strong> %s, <strong>Fin:</strong> %s.</i>", " - ", " - ");
    }
  }

  private ItemHistory createItemHistory(Long idTransmision, ActaPorTransmitirDto acta, String codEstadoActa,
                                        String descripcionEstado, String detalle, String fechaInicioFin) {

    String fechaItem = acta.getAudFechaModificacion() == null ? acta.getAudFechaCreacion() : acta.getAudFechaModificacion();
    return construirItemHistory(idTransmision, codEstadoActa, descripcionEstado, detalle, fechaItem, fechaInicioFin);

  }

  private String getNombreUltimaResolucion( ActaPorTransmitirDto acta, boolean soloInfundadaAnuladaUbigeo) {

    if (acta.getResoluciones() == null || acta.getResoluciones().isEmpty()) {
      return ConstantesComunes.VACIO;
    }

    Optional<DetActaResolucionPorTransmitirDto> optUltima = acta.getResoluciones()
            .stream()
            .max(Comparator.comparing(DetActaResolucionPorTransmitirDto::getCorrelativo));

    if(optUltima.isEmpty()) return null;

    if (optUltima.get().getResolucionDto() == null) {
      return ConstantesComunes.VACIO;
    }

    ResolucionPorTransmitirDto resolucion = optUltima.get().getResolucionDto();
    Integer tipo = resolucion.getTipoResolucion();
    String tipoResolucion = ConstantesCatalogo
            .getMapTiposResoluciones()
            .getOrDefault(tipo, "Desconocido");
    String numeroResolucion = resolucion.getNumeroResolucion();

    // Si solo queremos tipos específicos (infundadas/anuladas)
    if (soloInfundadaAnuladaUbigeo) {

      Set<Integer> tiposValidos = Set.of(
              ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS,
              ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO,
              ConstantesCatalogo.CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO
      );

      if (!tiposValidos.contains(tipo)) {
        return ConstantesComunes.VACIO;
      }

      return String.format(
              "<p>El acta fue asociada a la resolución <strong>%s, de tipo %s</strong>, por el usuario %s.</p>",
              numeroResolucion,
              tipoResolucion,
              resolucion.getAudUsuarioModificacion()
             // acta.getAudUsuarioModificacion()
      );
    }

    // Caso general
    return String.format(
            "<strong>%s, de tipo %s</strong>, por el usuario %s.",
            numeroResolucion,
            tipoResolucion,
            acta.getAudUsuarioModificacion()
    );
  }


  private String getResolucionRechazadaAprobada( ActaPorTransmitirDto acta) {

    if (acta.getResoluciones() == null || acta.getResoluciones().isEmpty()) {
      return ConstantesComunes.VACIO;
    }

    Optional<DetActaResolucionPorTransmitirDto> optUltima = acta.getResoluciones()
            .stream()
            .max(Comparator.comparing(DetActaResolucionPorTransmitirDto::getCorrelativo));

    if(optUltima.isEmpty()) return null;

    if (optUltima.get().getResolucionDto() == null) {
      return ConstantesComunes.VACIO;
    }

    ResolucionPorTransmitirDto resolucion = optUltima.get().getResolucionDto();
    Integer tipo = resolucion.getTipoResolucion();
    String tipoResolucion = ConstantesCatalogo
            .getMapTiposResoluciones()
            .getOrDefault(tipo, "Desconocido");
    String numeroResolucion = resolucion.getNumeroResolucion();

    if(resolucion.getEstadoDigitalizacion().equals(ConstantesEstadoResolucion.RECHAZADA_2DO_CC)){
      return String.format(
              "<strong>La resolución %s, de tipo %s</strong>, fue rechazada en control de calidad por el usuario %s.",
              numeroResolucion,
              tipoResolucion,
              acta.getAudUsuarioModificacion()
      );
    }else   if(resolucion.getEstadoDigitalizacion().equals(ConstantesEstadoResolucion.SEGUNDO_CC_ACEPTADA)){
      return String.format(
              "<strong>La resolución %s, de tipo %s</strong>, fue aprobada en control de calidad por el usuario %s.",
              numeroResolucion,
              tipoResolucion,
              acta.getAudUsuarioModificacion()
      );
    }else return ConstantesComunes.VACIO;

  }


  private ItemHistory construirItemHistory(Long idTransmision, String codEstadoActa, String descripcionEstado, String detalle, String fecha, String fechaInicioFin) {
    return ItemHistory.builder()
        .id(idTransmision)
        .codEstadoActa(codEstadoActa)
        .descripcionEstado(descripcionEstado)
        .detalle(detalle)
        .fecha(fecha)
        .fechaInicioFin(fechaInicioFin)
        .build();
  }

}
