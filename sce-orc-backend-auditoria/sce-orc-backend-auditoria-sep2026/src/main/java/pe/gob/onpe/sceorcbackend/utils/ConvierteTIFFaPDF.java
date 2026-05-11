package pe.gob.onpe.sceorcbackend.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import org.springframework.stereotype.Service;

@Service
public class ConvierteTIFFaPDF {

    static Logger logger = LoggerFactory.getLogger(ConvierteTIFFaPDF.class);

    public File convertTIFFToPDF(File tiffFile, String pathName) {

        File pdfFile = new File(pathName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            RandomAccessSource ras = new RandomAccessSourceFactory().createBestSource(tiffFile.getAbsolutePath());
            RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(ras);
            try {
                int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);
                Document tifftoPDF = new Document();

                try {
                    PdfWriter pdfWriter = PdfWriter.getInstance(tifftoPDF, fileOutputStream);
                    pdfWriter.setStrictImageSequence(true);
                    tifftoPDF.open();

                    for (int i = 1; i <= numberOfPages; i++) {
                        Image tempImage = TiffImage.getTiffImage(myTiffFile, i);
                        Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
                        tifftoPDF.setPageSize(pageSize);
                        tifftoPDF.newPage();
                        tifftoPDF.add(tempImage);
                    }
                } finally {
                    if (tifftoPDF.isOpen()) {
                        tifftoPDF.close();
                    }
                }
            } finally {
                if (myTiffFile != null) {
                    myTiffFile.close();
                }
            }
        } catch (Exception ex) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, ex.getMessage());
        }
        return pdfFile;
    }
}
