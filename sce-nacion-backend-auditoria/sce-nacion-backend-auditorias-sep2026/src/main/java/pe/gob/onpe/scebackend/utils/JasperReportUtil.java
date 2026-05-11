package pe.gob.onpe.scebackend.utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JasperReportUtil {
    private JasperReportUtil() {
        throw new UnsupportedOperationException("JasperReportUtil es una clase utilitaria y no debe ser instanciada");
    }
    public static JasperPrint getJasperPrint(Map<String, Object> parameters,
                                             List values, InputStream is) throws JRException {

    	InputStream pixelTransparente = JasperReportUtil.class.getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "pixeltransparente.png");
    	parameters.put("pixeltransparente", pixelTransparente);
        
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(values);
        JasperReport report = JasperCompileManager.compileReport(is);
        return JasperFillManager.fillReport(report, parameters, beanCollectionDataSource);

    }

  public static <T> byte[] generarByteArray(Map<String, Object> parametros, List<T> dataSource, InputStream templateStream) throws JRException {
    JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametros, dataSource, templateStream);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    JRPdfExporter exporter = new JRPdfExporter();
    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
    exporter.exportReport();

    return byteArrayOutputStream.toByteArray();
  }



    public static Map<String, Object> crearParametrosComunesReportes(
            Class<?> claseContexto,
            String nombreProceso,
            String tituloReporte,
            String descripcionCentroComputo,
            String descripcionOdpe,
            String usuario,
            String nombreReporte,
            String versionSistema,
            String sinValorOficial
    ) {
        Map<String, Object> params = new HashMap<>();

        params.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE,
                claseContexto.getClassLoader().getResourceAsStream(
                        ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE));
        params.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE,
                claseContexto.getClassLoader().getResourceAsStream(
                        ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE));

        params.put(ConstantesComunes.REPORT_PARAM_TITULO, nombreProceso.toUpperCase());
        params.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE, tituloReporte.toUpperCase());
        params.put(ConstantesComunes.REPORT_PARAM_DESC_CC, descripcionCentroComputo);
        params.put(ConstantesComunes.REPORT_PARAM_DESC_ODPE, descripcionOdpe);
        params.put(ConstantesComunes.REPORT_PARAM_USUARIO, usuario);
        params.put(ConstantesComunes.REPORT_PARAM_VERSION, versionSistema);
        params.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, sinValorOficial);
        params.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, nombreReporte);

        return params;
    }


}
