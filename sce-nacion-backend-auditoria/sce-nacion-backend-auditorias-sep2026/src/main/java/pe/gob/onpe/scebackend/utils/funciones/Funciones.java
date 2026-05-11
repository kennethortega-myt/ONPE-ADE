package pe.gob.onpe.scebackend.utils.funciones;

import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Log4j2
public class Funciones {

    private Funciones() {
        throw new IllegalStateException("Utility class");
    }
    public static byte[] generarReporte(Class<?> classe,List<?> lista, String nombreReporte, Map<String, Object> parametrosReporte) throws JRException {
        byte[] pdf = null;
        InputStream file = classe.getClassLoader().getResourceAsStream(ConstantesComunes.PATH_REPORT_JRXML + File.separator + nombreReporte);
        log.info("NOMBRE DE REPORTE ==> {}", nombreReporte);
        if ((lista != null) && !lista.isEmpty()) {
            try{
                JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametrosReporte, lista, file);
                JRPdfExporter jrPdfExporter = new JRPdfExporter();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                jrPdfExporter.exportReport();
                pdf = byteArrayOutputStream.toByteArray();
                return pdf;
            }catch (Exception e){
                log.error("ERROR GENERANDO REPORTE:", e);
                return new byte[0];
            }
        } else {
            return new byte[0];
        }
    }

    public static Map<String, Object> getParametrosBaseReporte(Class<?> classe,
                                                               String sinvaloroficial,
                                                               String version,
                                                               String usuario,
                                                               String proceso,
                                                               String tituloReporte) {
        Map<String, Object> parametros = new java.util.HashMap<>();
        InputStream imagen = classe.getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametros.put("imagen", imagen);
        parametros.put("sinvaloroficial", sinvaloroficial);
        parametros.put("version", version);
        parametros.put("usuario", usuario);
        parametros.put("tituloGeneral", proceso);
        parametros.put("tituloRep", tituloReporte);
        return parametros;
    }
}
