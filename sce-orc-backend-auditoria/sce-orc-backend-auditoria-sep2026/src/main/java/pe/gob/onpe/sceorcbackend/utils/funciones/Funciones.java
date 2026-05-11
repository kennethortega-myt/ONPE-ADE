package pe.gob.onpe.sceorcbackend.utils.funciones;

import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.JasperReportUtil;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Log4j2
public class Funciones {

	private Funciones() {

	}

	public static byte[] generarReporte(Class<?> classe, List<?> lista, String nombreReporte, Map<String, Object> parametrosReporte) throws JRException {
		InputStream file = classe.getClassLoader()
				.getResourceAsStream(ConstantesComunes.PATH_REPORT_JRXML + File.separator + nombreReporte);
		log.info("NOMBRE DE REPORTE ==> {}", nombreReporte);
		if ((lista != null) && !lista.isEmpty()) {
			try {
				JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametrosReporte, lista, file);
				JRPdfExporter jrPdfExporter = new JRPdfExporter();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
				jrPdfExporter.exportReport();
				return modificarMetadataPdf(byteArrayOutputStream.toByteArray(), parametrosReporte, nombreReporte);

			} catch (Exception e) {
				log.error("ERROR GENERANDO REPORTE:", e);
				return null;
			}
		} else {
			return null;
		}
	}

	public static byte[] generarReporteSinValidacionDTD(Class<?> clazz, List<?> listaData, String nombreReporte,
			Map<String, Object> parametros) {

		String originalAccessDTD = System.getProperty(ConstantesComunes.ORIGINAL_ACCESS_DTO);
		String originalAccessSchema = System.getProperty(ConstantesComunes.ORIGINAL_ACCESS_SCHEMA);
		try{
			InputStream reportStream = clazz.getClassLoader().getResourceAsStream(nombreReporte);

			if (reportStream == null) throw new InternalServerErrorException("No se pudo encontrar el archivo de reporte: " + nombreReporte);

			System.setProperty(ConstantesComunes.ORIGINAL_ACCESS_DTO, "");
			System.setProperty(ConstantesComunes.ORIGINAL_ACCESS_SCHEMA, "");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setNamespaceAware(false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaData);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

			String nombre = Paths.get(nombreReporte).getFileName().toString();

			return modificarMetadataPdf(JasperExportManager.exportReportToPdf(jasperPrint), parametros, nombre);

		} catch (Exception e) {
			throw new InternalServerErrorException("Error al generar el reporte: " + e.getMessage(), e);
		}finally {
			if (originalAccessDTD != null) {
				System.setProperty("javax.xml.accessExternalDTD", originalAccessDTD);
			} else {
				System.clearProperty("javax.xml.accessExternalDTD");
			}
			if (originalAccessSchema != null) {
				System.setProperty("javax.xml.accessExternalSchema", originalAccessSchema);
			} else {
				System.clearProperty("javax.xml.accessExternalSchema");
			}
		}
	}


	public static byte[] modificarMetadataPdf(byte[] pdfBytes, Map<String, Object> parametrosReporte, String nombreReporte) throws IOException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDDocumentInformation info = doc.getDocumentInformation();
            info.setProducer(ConstantesComunes.NOMBRE_SISTEMA);
            info.setTitle(ConstantesComunes.NOMBRE_SISTEMA);
            String versionSistema = getFirstValueFromParam(parametrosReporte, ConstantesComunes.REPORT_PARAM_VERSION, "p_version", "versionSuite");
            info.setAuthor(ConstantesComunes.ABREV_NOMBRE_SISTEMA.concat(" ").concat(versionSistema));
            info.setSubject(quitarExtension(nombreReporte));
            info.setCreator(ConstantesComunes.NOMBRE_SISTEMA);
            doc.setDocumentInformation(info);
            doc.save(out);
            return out.toByteArray();
        }
    }

	private static String quitarExtension(String nombre) {
		if (nombre == null || nombre.isEmpty()) {
			return nombre;
		}

		int index = nombre.lastIndexOf('.');
		if (index > 0) {
			return nombre.substring(0, index);
		}

		return nombre; // no tiene extensión
	}


	private static String getFirstValueFromParam(Map<String, Object> map, String... keys) {
		for (String key : keys) {
			if (map.containsKey(key) && map.get(key) != null) {
				return String.valueOf(map.get(key));
			}
		}
		return "";
	}
}