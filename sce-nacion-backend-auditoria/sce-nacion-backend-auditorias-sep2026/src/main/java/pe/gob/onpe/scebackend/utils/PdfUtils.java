package pe.gob.onpe.scebackend.utils;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;


public class PdfUtils {

    static Logger logger = LoggerFactory.getLogger(PdfUtils.class);
    
    private PdfUtils(){
    	
    }

    public static byte[] mergeImagesToPdf(String base64Image1, String base64Image2, String outputPdfLocation, String outputPdfName) 
    		throws DocumentException, MalformedURLException, IOException {
    	
    	Rectangle pageSize = PageSize.A3; // Usa tamaño A4 o el que necesites
    	Document document = new Document(pageSize, 0, 0, 0, 0); // Márgenes en cero
        String outputPdf = PathUtils.normalizePath(outputPdfLocation, outputPdfName);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPdf));
            document.open();
            
            // Add first image
            Image image1 = Image.getInstance(Base64.getDecoder().decode(base64Image1));
            image1.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight());
            image1.setAbsolutePosition(0, 0);
            document.add(image1);

            // Add second image
            
            if(base64Image2!=null) {
                // New page
                document.newPage();
            	Image image2 = Image.getInstance(Base64.getDecoder().decode(base64Image2));
            	image2.scaleAbsolute(pageSize.getWidth(), pageSize.getHeight());
            	image2.setAbsolutePosition(0, 0);
                document.add(image2);
            }
            
            document.close();
            
            Path pdfPath = Paths.get(outputPdf);
            return Files.readAllBytes(pdfPath);
            
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

    }
}