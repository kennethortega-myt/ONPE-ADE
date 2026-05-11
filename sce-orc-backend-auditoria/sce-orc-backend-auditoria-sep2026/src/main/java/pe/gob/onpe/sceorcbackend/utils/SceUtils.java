package pe.gob.onpe.sceorcbackend.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.VotoOpcionPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.VotoOpcionBean;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.utils.digitochequeo.ConfigRango;
import pe.gob.onpe.sceorcbackend.utils.digitochequeo.DigitoChequeoModulo11Util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SceUtils {

  private SceUtils() {

  }

  public static String removeZerosLeft(String str) {

    if (str == null) {
      return null;
    }
    if (str.equals(ConstantesComunes.CVALUE_ZERO)) {
      return str;
    }

    return str.replaceFirst("^0+(?!$)", ConstantesComunes.VACIO);
  }

  public static String vacioSiSoloCeros(String valor) {
    if (valor == null) {
      return null;
    }
    return valor.matches("0+") ? ConstantesComunes.VACIO : valor;
  }


  public static String limpiarVotos(String valor){
    String resul1 = removeZerosLeft(valor);
    return vacioSiSoloCeros(resul1);
  }

  public static String quitarComasAlFinal(String input) {

    if (input == null) {
      return null;
    }

    if (input.isEmpty()) {
      return ConstantesComunes.VACIO;
    }

    if (input.equals(ConstantesComunes.SEPARADOR_ERRORES)) {
      return ConstantesComunes.VACIO;
    }

    if (input.endsWith(ConstantesComunes.SEPARADOR_ERRORES)) {
      return input.substring(0, input.length() - 1);
    } else {
      return input;
    }

  }

  public static Object convertToType(String value) {
    String type = StringTypeDetector.detectType(value);

    switch (type) {
      case "BOOLEAN":
        return ConstantesComunes.TEXT_TRUE.equalsIgnoreCase(value) || "1".equals(value);

      case "NUMBER":
        return Double.parseDouble(value); // Devuelve como número decimal

      case "DATE":
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SceConstantes.PATTERN_YYYY_MM_DD_DASH);
        return LocalDate.parse(value, formatter);

      case "TEXT":
      default:
        return value; // Lo deja como texto
    }
  }


  public static String getNumMesaAndCopia(Acta acta) {
    if (acta == null || acta.getMesa() == null || acta.getMesa().getCodigo() == null) {
      return acta != null && acta.getId() != null ? acta.getId().toString() : "";
    }

    String mesa = acta.getMesa().getCodigo();
    String copia = Optional.ofNullable(acta.getNumeroCopia()).orElse(acta.getUbigeoEleccion().getEleccion().getNombre());
    String digito = Optional.ofNullable(acta.getDigitoChequeoEscrutinio()).orElse(ConstantesComunes.VACIO);

    return mesa + ConstantesComunes.GUION_MEDIO + copia + digito;
  }
  
  public static String getNumMesaAndCopia(ActaCeleste acta) {
	    if (acta == null || acta.getActa().getMesa() == null || acta.getActa().getMesa().getCodigo() == null) {
	      return acta != null && acta.getId() != null ? acta.getId().toString() : "";
	    }

	    String mesa = acta.getActa().getMesa().getCodigo();
	    String copia = Optional.ofNullable(acta.getNumeroCopia()).orElse(acta.getActa().getUbigeoEleccion().getEleccion().getNombre());
	    String digito = Optional.ofNullable(acta.getDigitoChequeoEscrutinio()).orElse(ConstantesComunes.VACIO);

	    return mesa + ConstantesComunes.GUION_MEDIO + copia + digito;
	  }

  public static float formatDecimal3Digitos(float numero) {
    BigDecimal bd = BigDecimal.valueOf(numero).setScale(3, RoundingMode.HALF_UP);
    return bd.floatValue();
  }


  public static void agregarEstadoResolucion(Acta cabActa, String estadoResolucion) {
    if (cabActa.getEstadoActaResolucion() == null || cabActa.getEstadoActaResolucion().isEmpty()) {
      cabActa.setEstadoActaResolucion(estadoResolucion);
    } else if (!cabActa.getEstadoActaResolucion().contains(estadoResolucion)) {
      cabActa.setEstadoActaResolucion(
          cabActa.getEstadoActaResolucion().concat(ConstantesComunes.SEPARADOR_ERRORES + estadoResolucion)
      );
    }
  }


  public static void manejarIlegibleVotoOpcion(Acta acta, VotoOpcionBean votoOpcion, DetActaOpcion detActaOpcion) {

    String votoOpcionInput = votoOpcion.getVotos();
    if(votoOpcionInput == null){
      detActaOpcion.setIlegible(null);
      detActaOpcion.setVotos(null);
    }else if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votoOpcionInput)){
      detActaOpcion.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActaOpcion.setVotos(ConstantesComunes.NVALUE_NULL);
      agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
    }else {
      try{
        detActaOpcion.setIlegible(null);
        detActaOpcion.setVotos(Long.parseLong(votoOpcion.getVotos()));
      }catch(Exception e){
        detActaOpcion.setIlegible(null);
        detActaOpcion.setVotos(null);
      }
    }
  }

  public static void manejarIlegibleVotoOpcion(Acta acta, VotoOpcionPorCorregir votoOpcion, DetActaOpcion detActaOpcion) {

    String votoOpcionInput = votoOpcion.getTerceraDigitacion();
    if(votoOpcionInput == null){
      detActaOpcion.setIlegible(null);
      detActaOpcion.setVotos(null);
    }else if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votoOpcionInput)){
      detActaOpcion.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActaOpcion.setVotos(ConstantesComunes.NVALUE_NULL);
      agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
    }else {
      try{
        detActaOpcion.setIlegible(null);
        detActaOpcion.setVotos(Long.parseLong(votoOpcion.getTerceraDigitacion()));
      }catch(Exception e){
        detActaOpcion.setIlegible(null);
        detActaOpcion.setVotos(null);
      }
    }
  }



  public static void removerEstadoResolucion(Acta cabActa, String estadoAEliminar) {
    if (estadoAEliminar == null || estadoAEliminar.isEmpty()) return;

    if (cabActa.getEstadoActaResolucion() != null && cabActa.getEstadoActaResolucion().contains(estadoAEliminar)) {
      if(cabActa.getEstadoActaResolucion().length()==1){
        cabActa.setEstadoActaResolucion(null);
      }else{
        if(cabActa.getEstadoActaResolucion().startsWith(estadoAEliminar+ConstantesComunes.SEPARADOR_ERRORES))
          cabActa.setEstadoActaResolucion(cabActa.getEstadoActaResolucion().replace(estadoAEliminar+ConstantesComunes.SEPARADOR_ERRORES, ConstantesComunes.VACIO));
        else
          cabActa.setEstadoActaResolucion(cabActa.getEstadoActaResolucion().replace(ConstantesComunes.SEPARADOR_ERRORES+estadoAEliminar, ConstantesComunes.VACIO));
      }
    }
  }

  public static boolean isValidCheckDigit(BarCodeInfo barCodeInfo, Long type,
                            DetTipoEleccionDocumentoElectoral documentoElectoralAE,
                            DetTipoEleccionDocumentoElectoral documentoElectoralAIS) {

    if (barCodeInfo == null) return false;

    if (Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE)) {
      return validarDigitoChequeoAE(barCodeInfo.getNroMesa(), barCodeInfo.getNroCopia(), barCodeInfo.getDigitoChequeo(), documentoElectoralAE);
    }

    if (Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS)) {
      return validarDigitoChequeoAIS(barCodeInfo.getDigitoChequeo(), documentoElectoralAIS);
    }

    return false;
  }

  public static boolean validarDigitoChequeoAE(String nroMesa,String copia, String digitoChequeo, DetTipoEleccionDocumentoElectoral docAE) {

    if (!docAE.getDigitoChequeo().contains(digitoChequeo)) {
      return false;
    }

    ConfigRango configRango = new ConfigRango();
    try {
      configRango.setRangoInicial(Integer.parseInt(docAE.getRangoInicial()));
      configRango.setRangoFinal(Integer.parseInt(docAE.getRangoFinal()));
    } catch (NumberFormatException e) {
      return false;
    }
    configRango.setRangoCopias(docAE.getDigitoChequeo());

    char digitoEsperado = DigitoChequeoModulo11Util.obtenerDigitoChequeoAE(nroMesa, copia, configRango);
    return digitoChequeo.charAt(0) == digitoEsperado;
  }


  public static boolean validarDigitoChequeoAIS(String digitoChequeo,
                                                DetTipoEleccionDocumentoElectoral docAIS) {
    return docAIS.getDigitoChequeo().equals(digitoChequeo);
  }



  public static BarCodeInfo parsearCodigoBarra(String codigoBarrasActa) {

    Pattern patternCodeBar = Pattern.compile("^(\\d{6})(\\d{2})([A-Z])");

    if (codigoBarrasActa == null) {
      throw new IllegalArgumentException("El nro de acta no puede ser nula.");
    }

    Matcher matcher = patternCodeBar.matcher(codigoBarrasActa);
    if (!matcher.find()) {
      throw new IllegalArgumentException("Formato inválido para el número de acta: " + codigoBarrasActa);
    }

    return BarCodeInfo.builder()
        .codigoBarra(codigoBarrasActa)
        .nroMesa(matcher.group(1))
        .nroCopia(matcher.group(2))
        .digitoChequeo(matcher.group(3))
        .nroCopiaAndDigito(matcher.group(2).concat(matcher.group(3)))
        .build();
  }

  public static boolean isProcesoCpr(String acronimo) {
    return acronimo.startsWith(ConstantesComunes.PROCESO_CPR_ABREV);
  }
  public static String removerEspaciosEnBlanco(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("\\s+", "");
  }

  public static boolean tieneEspaciosEnExtremos(String input) {
    if (input == null) {
      return false;
    }
    return !input.equals(input.trim());
  }


  public static String getGuid(TokenInfo tokenInfo, MultipartFile multipartFile) {
    try{
    return String.format("%s%s%s",
        tokenInfo.getCodigoCentroComputo(),
        ConstantesComunes.GUION_MEDIO,
        DigestUtils.sha256Hex(multipartFile.getInputStream()));
    } catch (IOException e) {
      throw new InternalServerErrorException(String.format("Error al obtener el GUI del archivo: %s.", e.getMessage()));
    }
  }


  public static List<String> getPaginadoResolucion(int numeroPaginas) {
    List<String> paginas = new ArrayList<>();
    for (int i = 1; i <= numeroPaginas; i++) {
      paginas.add("Pág " + i);
    }
    return paginas;
  }

    public static boolean esMesaExtranjera(Mesa mesa) {
        if (mesa == null
                || mesa.getLocalVotacion() == null
                || mesa.getLocalVotacion().getUbigeo() == null
                || mesa.getLocalVotacion().getUbigeo().getCodigo() == null) {
            return false;
        }

        return mesa.getLocalVotacion()
                .getUbigeo()
                .getCodigo()
                .startsWith("9");
    }

}
