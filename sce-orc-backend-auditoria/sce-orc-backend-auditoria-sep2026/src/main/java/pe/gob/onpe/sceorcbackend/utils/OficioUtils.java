package pe.gob.onpe.sceorcbackend.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;

public class OficioUtils {

    private OficioUtils() {
        throw new UnsupportedOperationException("Clase utilitaria, no debe ser instanciada");
    }

    private static final String[] UNIDADES = { "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho",
            "nueve", "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho",
            "diecinueve" };

    private static final String[] DECENAS = { "", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta",
            "setenta", "ochenta", "noventa" };

    public static String convertirNumeroATexto(int numero) {
        if (numero == 0)
            return "cero";
        if (numero < 0)
            return "menos " + convertirNumeroATexto(-numero);
        if (numero < 20)
            return UNIDADES[numero];
        if (numero < 100) {
            int unidad = numero % 10;
            int decena = numero / 10;
            if (unidad == 0)
                return DECENAS[decena];
            if (decena == 2)
                return "veinti" + UNIDADES[unidad];
            return DECENAS[decena] + " y " + UNIDADES[unidad];
        }
        if (numero < 1000) {
            int centenas = numero / 100;
            int resto = numero % 100;
            String centenaTexto;
            switch (centenas) {
            case 1:
                centenaTexto = (resto == 0) ? "cien" : "ciento";
                break;
            case 2:
                centenaTexto = "doscientos";
                break;
            case 3:
                centenaTexto = "trescientos";
                break;
            case 4:
                centenaTexto = "cuatrocientos";
                break;
            case 5:
                centenaTexto = "quinientos";
                break;
            case 6:
                centenaTexto = "seiscientos";
                break;
            case 7:
                centenaTexto = "setecientos";
                break;
            case 8:
                centenaTexto = "ochocientos";
                break;
            case 9:
                centenaTexto = "novecientos";
                break;
            default:
                centenaTexto = "";
                break;
            }
            return centenaTexto + (resto > 0 ? " " + convertirNumeroATexto(resto) : "");
        }

        if (numero < 1_000_000) {
            int miles = numero / 1000;
            int resto = numero % 1000;
            String milesTexto = (miles == 1) ? "mil" : convertirNumeroATexto(miles) + " mil";
            return milesTexto + (resto > 0 ? " " + convertirNumeroATexto(resto) : "");
        }

        return "número demasiado grande";
    }

    public static String generarNumeroOficio(String correlativo, TokenInfo tokenInfo) {
        String anio = String.valueOf(LocalDate.now().getYear());
        String odpe = Optional.ofNullable(tokenInfo.getNombreCentroComputo())
                .map(val -> val.contains("-") ? val.split("-")[1] : val).orElse("").replaceAll("\\s+", "")
                .toUpperCase();
        String proceso = Optional.ofNullable(tokenInfo.getAbrevProceso()).orElse("").toUpperCase();
        return String.format("%s-%s-ODPE%s%s/ONPE", correlativo, anio, odpe, proceso);
    }

    public static String obtenerFechaOficio(Date fecha) {
        if (fecha == null) {
            fecha = new Date();
        }

        Locale locale = Locale.of("es", "ES");
        SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", locale);

        String formateado = sdf.format(fecha);
        return capitalizarMes(formateado);
    }

    private static String capitalizarMes(String fecha) {
        int mesIndex = fecha.indexOf("de ") + 3;
        if (mesIndex >= fecha.length())
            return fecha;

        String mesCapitalizado = fecha.substring(mesIndex, mesIndex + 1).toUpperCase() + fecha.substring(mesIndex + 1);

        return fecha.substring(0, mesIndex) + mesCapitalizado;
    }

    public static String convertToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buf)) != -1) {
                    bos.write(buf, 0, bytesRead);
                }
                byte[] fileBytes = bos.toByteArray();
                return Base64.getEncoder().encodeToString(fileBytes);
            }
        } else {
            throw new IOException("El archivo no existe");
        }
    }

    public static int obtenerNumeroPaginas(File file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new InternalServerErrorException("Error al leer el PDF");
        }
    }

}
