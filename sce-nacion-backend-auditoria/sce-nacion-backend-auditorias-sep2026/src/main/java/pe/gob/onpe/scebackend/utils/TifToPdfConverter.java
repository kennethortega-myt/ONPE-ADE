package pe.gob.onpe.scebackend.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilidad para convertir archivos TIF/TIFF a PDF.
 * Genera el PDF con el mismo nombre del archivo original.
 */
public class TifToPdfConverter {

    private static final Logger logger = LoggerFactory.getLogger(TifToPdfConverter.class);

    private TifToPdfConverter() {
    }

    /**
     * Convierte un archivo TIF a PDF. El tamaño de página se detecta
     * automáticamente según las dimensiones de la imagen TIF.
     *
     * @param rutaTif ruta completa del archivo TIF
     * @return ruta del PDF generado
     */
    public static String convertir(String rutaTif) throws IOException, DocumentException {
        return convertir(rutaTif, null);
    }

    /**
     * Convierte un archivo TIF a PDF con el tamaño de página indicado.
     * Si tamanioPagina es null, se detecta automáticamente.
     *
     * @param rutaTif       ruta completa del archivo TIF
     * @param tamanioPagina "A3", "A4" o null para autodetección
     * @return ruta del PDF generado
     */
    public static String convertir(String rutaTif, String tamanioPagina) throws IOException, DocumentException {
        Path tifPath = Paths.get(rutaTif);
        validarExistencia(tifPath);

        String nombreSinExtension = quitarExtension(tifPath.getFileName().toString());
        Path pdfPath = tifPath.getParent().resolve(nombreSinExtension + ".pdf");

        Image image = Image.getInstance(rutaTif);
        Rectangle pageSize = resolverTamanioPagina(tamanioPagina, image);

        generarPdf(pdfPath.toString(), pageSize, image);
        logger.info("TIF convertido a PDF [{}]: {} -> {}", describir(pageSize), rutaTif, pdfPath);
        return pdfPath.toString();
    }

    /**
     * Une dos archivos TIF en un solo PDF. El tamaño de cada página
     * se detecta automáticamente según las dimensiones de cada imagen.
     *
     * @param rutaTif1 ruta completa del primer TIF
     * @param rutaTif2 ruta completa del segundo TIF
     * @return ruta del PDF generado
     */
    public static String unirTifs(String rutaTif1, String rutaTif2) throws IOException, DocumentException {
        Path tifPath1 = Paths.get(rutaTif1);
        validarExistencia(tifPath1);
        validarExistencia(Paths.get(rutaTif2));

        String nombreSinExtension = quitarExtension(tifPath1.getFileName().toString());
        Path pdfPath = tifPath1.getParent().resolve(nombreSinExtension + ".pdf");

        Image image1 = Image.getInstance(rutaTif1);
        Image image2 = Image.getInstance(rutaTif2);
        Rectangle pageSize1 = detectarTamanioPagina(image1);
        Rectangle pageSize2 = detectarTamanioPagina(image2);

        // Usar el tamaño de la primera imagen para iniciar el documento
        Document document = new Document(pageSize1, 0, 0, 0, 0);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath.toString()));
            document.open();

            // Página 1
            image1.scaleAbsolute(pageSize1.getWidth(), pageSize1.getHeight());
            image1.setAbsolutePosition(0, 0);
            document.add(image1);

            // Página 2 (puede tener tamaño diferente)
            document.setPageSize(pageSize2);
            document.newPage();
            image2.scaleAbsolute(pageSize2.getWidth(), pageSize2.getHeight());
            image2.setAbsolutePosition(0, 0);
            document.add(image2);

            document.close();
            logger.info("TIFs unidos en PDF [pag1={}, pag2={}]: {}, {} -> {}",
                    describir(pageSize1), describir(pageSize2), rutaTif1, rutaTif2, pdfPath);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return pdfPath.toString();
    }

    private static void validarExistencia(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("El archivo TIF no existe: " + path);
        }
    }

    /**
     * Resuelve el tamaño de página: si se indica "A3" o "A4" usa ese,
     * si es null lo detecta automáticamente de la imagen.
     */
    private static Rectangle resolverTamanioPagina(String tamanioPagina, Image image) {
        if (tamanioPagina != null) {
            if ("A4".equalsIgnoreCase(tamanioPagina)) {
                logger.info("Tamaño forzado: A4");
                return PageSize.A4;
            }
            if ("A3".equalsIgnoreCase(tamanioPagina)) {
                logger.info("Tamaño forzado: A3");
                return PageSize.A3;
            }
            logger.warn("Tamaño '{}' no reconocido, se usará autodetección", tamanioPagina);
        }
        return detectarTamanioPagina(image);
    }

    /**
     * Detecta si la imagen corresponde a A4 o A3 según su relación de aspecto
     * y dimensiones. Si no coincide con ninguno, usa el tamaño real de la imagen.
     */
    private static Rectangle detectarTamanioPagina(Image image) {
        float anchoImg = image.getWidth();
        float altoImg = image.getHeight();

        // Tolerancia del 15% para comparar proporciones
        float tolerancia = 0.15f;

        // A4: 595 x 842 puntos (portrait) o 842 x 595 (landscape)
        if (coincideCon(anchoImg, altoImg, PageSize.A4, tolerancia)) {
            boolean landscape = anchoImg > altoImg;
            logger.info("Tamaño detectado: A4 {} (imagen: {}x{})", landscape ? "landscape" : "portrait", anchoImg, altoImg);
            return landscape ? PageSize.A4.rotate() : PageSize.A4;
        }

        // A3: 842 x 1191 puntos (portrait) o 1191 x 842 (landscape)
        if (coincideCon(anchoImg, altoImg, PageSize.A3, tolerancia)) {
            boolean landscape = anchoImg > altoImg;
            logger.info("Tamaño detectado: A3 {} (imagen: {}x{})", landscape ? "landscape" : "portrait", anchoImg, altoImg);
            return landscape ? PageSize.A3.rotate() : PageSize.A3;
        }

        // Si no coincide, usar tamaño real de la imagen
        logger.info("Tamaño no estándar, usando dimensiones de imagen: {}x{}", anchoImg, altoImg);
        return new Rectangle(anchoImg, altoImg);
    }

    /**
     * Compara si las dimensiones de la imagen coinciden con un tamaño de página
     * estándar (portrait o landscape) dentro de una tolerancia.
     */
    private static boolean coincideCon(float anchoImg, float altoImg, Rectangle pagina, float tolerancia) {
        float pw = pagina.getWidth();
        float ph = pagina.getHeight();
        float ratio = anchoImg / altoImg;

        // Portrait
        float ratioPortrait = pw / ph;
        if (Math.abs(ratio - ratioPortrait) < tolerancia) {
            return true;
        }

        // Landscape
        float ratioLandscape = ph / pw;
        return Math.abs(ratio - ratioLandscape) < tolerancia;
    }

    private static void generarPdf(String rutaPdf, Rectangle pageSize, Image image) throws IOException, DocumentException {
        Document document = new Document(pageSize, 0, 0, 0, 0);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaPdf));
            document.open();

            image.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight());
            image.setAbsolutePosition(0, 0);
            document.add(image);

            document.close();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private static String describir(Rectangle pageSize) {
        if (pageSize.getWidth() == PageSize.A4.getWidth() && pageSize.getHeight() == PageSize.A4.getHeight()) return "A4";
        if (pageSize.getWidth() == PageSize.A4.getHeight() && pageSize.getHeight() == PageSize.A4.getWidth()) return "A4-landscape";
        if (pageSize.getWidth() == PageSize.A3.getWidth() && pageSize.getHeight() == PageSize.A3.getHeight()) return "A3";
        if (pageSize.getWidth() == PageSize.A3.getHeight() && pageSize.getHeight() == PageSize.A3.getWidth()) return "A3-landscape";
        return pageSize.getWidth() + "x" + pageSize.getHeight();
    }

    private static String quitarExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        return punto > 0 ? nombreArchivo.substring(0, punto) : nombreArchivo;
    }

    public static void main(String[] args) {
        String rutaTif = "D:\\convertir\\03580112X.TIF";
        String tamanioPagina = "A3"; // "A3", "A4" o null para autodetección
        //String rutaTif1 = "C:\\Users\\ncoqchi\\Downloads\\aHA\\00036802H.tif";
        try {
            String pdf = convertir(rutaTif, tamanioPagina);
            //String pdf = unirTifs(rutaTif, rutaTif1);
            logger.info("PDF generado: {}", pdf);
        } catch (IOException | DocumentException e) {
            logger.error("Error al convertir TIF a PDF: {}", e.getMessage(), e);
        }
    }

}
