package pe.gob.onpe.scebackend.utils;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaDto;
import pe.gob.onpe.scebackend.model.stae.dto.DetalleActaPreferencialDto;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesEstadoActa;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class SceUtils {
    private SceUtils() {
        throw new UnsupportedOperationException("SceUtils es una clase utilitaria y no debe ser instanciada");
    }

    public static String generarGUID() {
        return UUID.randomUUID().toString();
    }
    
    public static String formatBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static String imageConverterBase64(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return  Base64.getEncoder().encodeToString(bytes);
    }
    
    public static Integer toInteger(Long valorLong) {
    	Integer valorInt = null;
    	try {
            valorInt = Math.toIntExact(valorLong);
        } catch (ArithmeticException e) {
           log.error("No se puede convertir el valor long a int sin pérdida de información.");
        }
    	return valorInt;
    }

    public static Object convertToType(String value) {
        String type = StringTypeDetector.detectType(value);

        switch (type) {
            case "BOOLEAN":
                return "true".equalsIgnoreCase(value) || "1".equals(value);

            case "NUMBER":
                return Double.parseDouble(value); // Devuelve como número decimal

            case "DATE":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(value, formatter);

            case "TEXT":
            default:
                return value; // Lo deja como texto
        }
    }


    public static void agregarEstadoResolucion(ActaElectoralRequestDto actaElectoralRequestDto, String estadoResolucion) {
        if (actaElectoralRequestDto.getEstadoActaResolucion() == null || actaElectoralRequestDto.getEstadoActaResolucion().isEmpty()) {
            actaElectoralRequestDto.setEstadoActaResolucion(estadoResolucion);
        } else if (!actaElectoralRequestDto.getEstadoActaResolucion().contains(estadoResolucion)) {
            actaElectoralRequestDto.setEstadoActaResolucion(
                    actaElectoralRequestDto.getEstadoActaResolucion().concat(ConstantesComunes.SEPARADOR_ERRORES + estadoResolucion)
            );
        }
    }

    public static void removerEstadoResolucion(ActaElectoralRequestDto actaElectoralRequestDto, String estadoAEliminar) {
        if (estadoAEliminar == null || estadoAEliminar.isEmpty()) return;

        if (actaElectoralRequestDto.getEstadoActaResolucion() != null && actaElectoralRequestDto.getEstadoActaResolucion().contains(estadoAEliminar)) {
            if(actaElectoralRequestDto.getEstadoActaResolucion().length()==1){
                actaElectoralRequestDto.setEstadoActaResolucion(null);
            }else{
                if (actaElectoralRequestDto.getEstadoActaResolucion().startsWith(estadoAEliminar+ConstantesComunes.SEPARADOR_ERRORES))
                    actaElectoralRequestDto.setEstadoActaResolucion(actaElectoralRequestDto.getEstadoActaResolucion().replace(estadoAEliminar+ConstantesComunes.SEPARADOR_ERRORES, ConstantesComunes.VACIO));
                else
                    actaElectoralRequestDto.setEstadoActaResolucion(actaElectoralRequestDto.getEstadoActaResolucion().replace(ConstantesComunes.SEPARADOR_ERRORES+estadoAEliminar, ConstantesComunes.VACIO));
            }
        }
    }


    public static void guardarVeriConvencionalEstadoResolucionErrorMaterialAgrupol(ActaElectoralRequestDto acta, List<DetalleActaDto> detActaListToErrores) {

        List<String> estadosErroresMaterialesAgrupol = List.of(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_C, ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_F,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_K,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_M);

        String hashSetErroresMaterialesDetActaTotal = detActaListToErrores.stream()
                .map(DetalleActaDto::getEstadoErrorAritmetico)
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

    public static void guardarVeriConvencionalEstadoResolucionErrorMaterialPreferencial(ActaElectoralRequestDto acta, List<DetalleActaPreferencialDto> detActaPreferencialList) {

        List<String> estadosErroresMaterialesPreferenciales = List.of(
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_G,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_H,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I,
                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_J);

        String hashSetErroresMaterialesPreferenciales = detActaPreferencialList.stream()
                .map(DetalleActaPreferencialDto::getEstadoErrorAritmetico)
                .filter(estado -> estado != null && !estado.isEmpty())
                .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
                .distinct()
                .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

        if (Arrays.stream(hashSetErroresMaterialesPreferenciales.split(ConstantesComunes.SEPARADOR_ERRORES))
                .anyMatch(estadosErroresMaterialesPreferenciales::contains)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_PREFERENCIAL);
        }

    }

}
