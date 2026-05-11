package pe.gob.onpe.sceorcbackend.utils;


import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.AgrupolBean;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProcessActaUtil {

    private ProcessActaUtil(){

    }

    public static long getTotalVotosAgrupacionesPoliticas(List<AgrupolBean> agrupacionesPoliticas) {
        return agrupacionesPoliticas.stream()
                .filter(agrupol -> !agrupol.getVotos().equals(ConstantesComunes.TEXTO_NULL))
                .mapToLong(agrupol -> {
                    String terceraDigitacion = agrupol.getVotos();
                    if (!terceraDigitacion.equals(ConstantesComunes.VACIO) && !terceraDigitacion.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
                        return Long.parseLong(terceraDigitacion);
                    }
                    return 0L;
                })
                .sum();
    }



  public static <T, U> long getTotalVotosCalculadosRevocatoria(
      List<T> agrupolList,
      Long cvas,
      Function<T, List<U>> votosOpcionesSupplier,
      Function<U, String> votosGetter) {

    if (agrupolList == null ||
        agrupolList.isEmpty()) {
      return 0;
    }

    Set<Long> votosCalculados = agrupolList.stream()
        .map(item -> votosOpcionesSupplier.apply(item).stream()
            .mapToLong(revocatoriaItem -> {
              String votosUser = SceUtils.removeZerosLeft(votosGetter.apply(revocatoriaItem));

              if (votosUser == null ||
                  votosUser.equals(ConstantesComunes.VACIO) ||
                  votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
                votosUser = ConstantesComunes.CVALUE_ZERO;
              }

              return Long.parseLong(votosUser);
            })
            .sum()
        )
        .collect(Collectors.toSet());

    if (votosCalculados.isEmpty()) {
      return 0;
    }

    if (votosCalculados.size() > 1) {
      return (cvas == null) ? 0 : cvas;
    } else {
      return votosCalculados.iterator().next();
    }
  }

    public static void guardarVeriConvencionalEstadoResolucionErrorMaterialAgrupol(Acta acta, List<DetActa> detActaListToErrores) {

        List<String> estadosErroresMaterialesAgrupol = List.of(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_C, ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_F,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_K, ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_M);

        String hashSetErroresMaterialesDetActaTotal = detActaListToErrores.stream()
                .map(DetActa::getEstadoErrorMaterial)
                .filter(estado -> estado != null && !estado.isEmpty())
                .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

        if (Arrays.stream(hashSetErroresMaterialesDetActaTotal.split(ConstantesComunes.SEPARADOR_ERRORES))
                .anyMatch(estadosErroresMaterialesAgrupol::contains)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
        }

    }



    public static void guardarVeriConvencionalEstadoResolucionErrorMaterialPreferencial(Acta acta, List<DetActaPreferencial> detActaPreferencialList) {


        List<String> estadosErroresMaterialesPreferenciales = List.of(
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_G,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_H,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_J);

        String hashSetErroresMaterialesPreferenciales = detActaPreferencialList.stream()
                .map(DetActaPreferencial::getEstadoErrorMaterial)
                .filter(estado -> estado != null && !estado.isEmpty())
                .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
                .distinct()
                .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

        if (Arrays.stream(hashSetErroresMaterialesPreferenciales.split(ConstantesComunes.SEPARADOR_ERRORES))
                .anyMatch(estadosErroresMaterialesPreferenciales::contains)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_PREFERENCIAL);
        }

    }


    public static boolean tieneComaAlFinal(String texto) {
        return texto != null && texto.endsWith(ConstantesComunes.SEPARADOR_ERRORES);
    }


}
