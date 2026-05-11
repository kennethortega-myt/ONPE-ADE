package pe.gob.onpe.sceorcbackend.utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JasperReportUtil {

    private JasperReportUtil() {
    }

    @SuppressWarnings("rawtypes")
    public static JasperPrint getJasperPrint(Map<String, Object> parameters,
                                             List values, InputStream is) throws JRException {

        InputStream pixelTransparente = JasperReportUtil.class.getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE);
        parameters.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);

        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(values);
        JasperReport report = JasperCompileManager.compileReport(is);
        return JasperFillManager.fillReport(report, parameters, beanCollectionDataSource);

    }

}
