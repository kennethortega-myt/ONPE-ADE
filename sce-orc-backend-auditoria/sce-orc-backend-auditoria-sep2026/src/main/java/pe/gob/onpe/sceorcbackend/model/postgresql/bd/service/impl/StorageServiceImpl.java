package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import jakarta.validation.constraints.NotNull;
import net.lingala.zip4j.ZipFile;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.utils.ArchivoProcesamientoException;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationGetFilesResponse;
import pe.gob.onpe.sceorcbackend.model.enums.TipoActaPDF;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class StorageServiceImpl implements StorageService {

  static Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

  @Value("${file.upload-dir}")
  private String uploadDir;

  @Value("${file.upload-subcarpeta:}")
  private String uploadSubcarpeta;

  @Value("${file.respaldo-dir}")
  private String respaldoFolder;


  public StorageServiceImpl() {
    super();
  }

  @Override
  public String getPathUpload() {
    if (uploadSubcarpeta == null || uploadSubcarpeta.isEmpty()) {
      return uploadDir;
    }
    Path fullPath = Paths.get(uploadDir).resolve(uploadSubcarpeta);
    if (!Files.exists(fullPath)) {
      try {
        Files.createDirectories(fullPath);
        logger.info("Subcarpeta creada: {}", fullPath);
      } catch (IOException e) {
        logger.error("Error creando subcarpeta {}: {}", fullPath, e.getMessage());
      }
    }
    return fullPath.toString() + File.separator;
  }

  @Override
  public Resource loadFile(String fileName, boolean isRespaldo) throws MalformedURLException {
    String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(fileName, "El nombre del archivo no puede ser null"));

    Path rootPath = Paths.get(isRespaldo ? respaldoFolder+uploadSubcarpeta : getPathUpload()).toAbsolutePath().normalize();
    Path targetPath = rootPath.resolve(cleanFileName).normalize();
    
    // Validación de path traversal
    if (!targetPath.startsWith(rootPath)) {
      throw new SecurityException("Acceso denegado fuera del directorio permitido: " + fileName);
    }

    // Usar UrlResource que es más eficiente para streaming
    Resource resource = new UrlResource(targetPath.toUri());

    // Validación lazy - solo verifica existencia, no lee el archivo
    if (!resource.exists()) {
      throw new SecurityException("No se pudo cargar el archivo: " + fileName);
    }
    
    if (!resource.isReadable()) {
      throw new SecurityException("El archivo no tiene permisos de lectura: " + fileName);
    }

    return resource;
  }

  public File convertTIFFToPDF(File tiffFile, String fileName) {

    File pdfFile = new File(getPathUpload() + fileName);
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
      }finally {
        if (myTiffFile != null) {
          myTiffFile.close();
        }
      }
    } catch (Exception ex) {
      logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, ex.getMessage());
    }
    return pdfFile;
  }

    @Override
    public File convertTIFFToPDFConvExtSTAE(File tiffFile, String fileName, TipoActaPDF tipoActa) {
        Rectangle targetSize = switch (tipoActa) {
            case CONVENCIONAL   -> new Rectangle(792f, 1224f);
            case CONVENCIONAL_EXTRANJERO -> PageSize.A4;
            case STAE                    -> PageSize.A4;
        };

        File pdfFile = new File(getPathUpload() + fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            RandomAccessSource ras = new RandomAccessSourceFactory()
                    .createBestSource(tiffFile.getAbsolutePath());
            RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(ras);

            try {
                int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);

                Document tifftoPDF = new Document(targetSize, 0f, 0f, 0f, 0f);

                try {
                    PdfWriter pdfWriter = PdfWriter.getInstance(tifftoPDF, fileOutputStream);
                    pdfWriter.setStrictImageSequence(true);
                    tifftoPDF.open();

                    for (int i = 1; i <= numberOfPages; i++) {
                        Image tempImage = TiffImage.getTiffImage(myTiffFile, i);

                        tempImage.scaleAbsolute(targetSize.getWidth(), targetSize.getHeight());

                        if (i > 1) {
                            tifftoPDF.setPageSize(targetSize);
                            tifftoPDF.newPage();
                        }

                        tempImage.setAbsolutePosition(0f, 0f);
                        pdfWriter.getDirectContent().addImage(tempImage);
                    }

                } finally {
                    if (tifftoPDF.isOpen()) {
                        tifftoPDF.close();
                    }
                }

            } finally {
                myTiffFile.close();
            }

        } catch (Exception ex) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, ex.getMessage());
        }

        return pdfFile;
    }

    @Override
  public File convertTIFFToPDF2(byte[] tiffBytes, String fileName) {
    if (tiffBytes == null || tiffBytes.length == 0) {
      throw new IllegalArgumentException("El arreglo de bytes TIFF no puede ser nulo o vacío");
    }

    File pdfFile = new File(getPathUpload() + fileName);

    try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(tiffBytes)) {

      RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(inputStream);
      int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);

      Document tifftoPDF = new Document();
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

      tifftoPDF.close();

    } catch (Exception ex) {
      logger.error("Error al convertir TIFF a PDF: {}", ex.getMessage(), ex);
      return null;
    }

    return pdfFile;
  }

  @Override
  public File convertTIFFToPDF3(ByteArrayResource tiffResource, String fileName) {
    if (tiffResource == null || !tiffResource.exists()) {
      throw new IllegalArgumentException("El recurso TIFF no puede ser nulo o inexistente");
    }

    File pdfFile = new File(getPathUpload() + fileName);

    try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(tiffResource.getByteArray())) {

      RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(inputStream);
      int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);

      Document tifftoPDF = new Document();
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

      tifftoPDF.close();

    } catch (Exception ex) {
      logger.error("Error al convertir TIFF a PDF: {}", ex.getMessage(), ex);
      return null;
    }

    return pdfFile;
  }

  public DigitizationGetFilesResponse loadFilesToBase64(Archivo fileId1, Archivo fileId2) throws ExecutionException, InterruptedException {
    CompletableFuture<byte[]> acta1Future = procesarArchivoAsync(fileId1);
    CompletableFuture<byte[]> acta2Future = procesarArchivoAsync(fileId2);
    DigitizationGetFilesResponse dataBase64 = new DigitizationGetFilesResponse();

    // Inicializar la lista de CompletableFuture
    List<CompletableFuture<byte[]>> futures = new ArrayList<>();

    // Añadir sólo las CompletableFuture no nulas
    futures.add(acta1Future);
    futures.add(acta2Future);

    // Convertir la lista a un array
    CompletableFuture<?>[] futuresArray = futures.toArray(new CompletableFuture<?>[0]);

    // Espera a que todas las tareas se completen
    CompletableFuture.allOf(futuresArray).join();

    if (acta1Future != null && acta2Future != null) {
      dataBase64.setActa1File(Base64.getEncoder().encodeToString(acta1Future.get()));
      dataBase64.setActa2File(Base64.getEncoder().encodeToString(acta2Future.get()));
    }
    if (acta1Future != null && acta2Future == null) {
      dataBase64.setActa1File(Base64.getEncoder().encodeToString(acta1Future.get()));

    }
    if (acta1Future == null && acta2Future != null) {
      dataBase64.setActa2File(Base64.getEncoder().encodeToString(acta2Future.get()));
    }

    return dataBase64;
  }

  @Override
  public CompletableFuture<String> loadOnlyFileToBase64(Archivo guid) {

    return procesarArchivoAsync(guid)
        .thenApply(bytesPng -> Base64.getEncoder().encodeToString(bytesPng))
        .exceptionally(ex -> {
          logger.error("Error durante la conversión: {}", ex.getMessage());
          return null;
        });
  }

  @Override
  public Map<Long, String> loadFilesToBase64Batch(List<Archivo> archivos) throws ExecutionException, InterruptedException {
    Map<Long, String> result = new HashMap<>();
    if (archivos == null || archivos.isEmpty()) {
      return result;
    }

    // Lanzar todas las lecturas en paralelo (bytes crudos directo a Base64, sin conversión)
    Map<Long, CompletableFuture<String>> futures = new HashMap<>();
    for (Archivo archivo : archivos) {
      if (archivo != null && archivo.getId() != null) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
              File file = this.obtenerArchivoDirectorio(archivo.getGuid());
              if (file == null) {
                throw new ArchivoProcesamientoException("Archivo no encontrado para el ID: " + archivo.getId());
              }
              try {
                return Files.readAllBytes(file.toPath());
              } catch (IOException e) {
                throw new ArchivoProcesamientoException("Error al leer archivo", e);
              }
            })
            .thenApply(bytes -> Base64.getEncoder().encodeToString(bytes))
            .exceptionally(ex -> {
              logger.error("Error al procesar archivo ID {}: {}", archivo.getId(), ex.getMessage());
              return null;
            });
        futures.put(archivo.getId(), future);
      }
    }

    // Esperar a que todas terminen
    CompletableFuture.allOf(futures.values().toArray(new CompletableFuture<?>[0])).join();

    // Recoger resultados
    for (Map.Entry<Long, CompletableFuture<String>> entry : futures.entrySet()) {
      String base64 = entry.getValue().get();
      if (base64 != null) {
        result.put(entry.getKey(), base64);
      }
    }

    return result;
  }

  public File obtenerArchivoDirectorio(String fileId) {
    Path filePath = Paths.get(getPathUpload()).resolve(fileId);
    Resource resource = null;
    try {
      resource = new UrlResource(filePath.toUri());
    } catch (MalformedURLException e) {
      return null;
    }

    if (!resource.exists() || !resource.isReadable()) {
      return null;
    }

    return new File(filePath.toString());
  }


  private CompletableFuture<byte[]> procesarArchivoAsync(Archivo fileId) {

    File file = this.obtenerArchivoDirectorio(fileId.getGuid());
    if (file == null) {
      return CompletableFuture.failedFuture(new ArchivoProcesamientoException("Archivo no encontrado para el ID: " + fileId));
    }


    return CompletableFuture.supplyAsync(() -> {
      try {
        return this.convertTiffToBytesPng(file);
      } catch (Exception e) {
        throw new ArchivoProcesamientoException("Error al convertir TIFF a PNG", e);
      }
    });
  }

  public byte[] convertTiffToBytesPng(File file) throws IOException {

    try (FileInputStream fis = new FileInputStream(file.getAbsolutePath());
         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      BufferedImage tiff = ImageIO.read(fis);
      ImageIO.write(tiff, "png", baos);
      return baos.toByteArray();
    }
  }

  public byte[] convertTiffToBytesJpeg(File file, float quality) throws IOException {
    try (FileInputStream fis = new FileInputStream(file.getAbsolutePath());
         ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      BufferedImage tiff = ImageIO.read(fis);

      // JPEG no soporta transparencia, convertir a RGB si es necesario
      BufferedImage rgbImage = tiff;
      if (tiff.getType() == BufferedImage.TYPE_BYTE_BINARY
          || tiff.getType() == BufferedImage.TYPE_BYTE_GRAY
          || tiff.getColorModel().hasAlpha()) {
        rgbImage = new BufferedImage(tiff.getWidth(), tiff.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        g.drawImage(tiff, 0, 0, Color.WHITE, null);
        g.dispose();
      }

      ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
      ImageWriteParam param = writer.getDefaultWriteParam();
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(quality);

      try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
        writer.setOutput(ios);
        writer.write(null, new IIOImage(rgbImage, null, null), param);
      }
      writer.dispose();
      return baos.toByteArray();
    }
  }


  /**
   * 6   los tif no tiene la longitud de caracteres de una lista de asistencia -5  No tiene la imagen  TIF de miembros de mesa no sorteados
   * -4  No tiene la imagen TIF de hoja de control de asistencia -3  Los tif deben ser dos -2  No cuenta con pdf -1  Carpeta vacia 0  otro
   * Error 1  Correcto
   */
  @Override
  public void validarContenidoZipMm(Mesa mesa, String nombreArchivo) {
    Path rootPath = Paths.get(getPathUpload()).toAbsolutePath().normalize();
    Path sourcePath = rootPath.resolve(StringUtils.cleanPath(nombreArchivo)).normalize();
    Path destinationPath = rootPath
            .resolve(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA)
            .resolve(mesa.getCodigo())
            .normalize();

    try {
      extraerZipMm(sourcePath, destinationPath);
      String[] archivos = listarArchivos(destinationPath);
      validarArchivosMm(archivos, mesa);
    } catch (Exception e) {
      logger.error(ConstantesComunes.MSJ_ERROR, e);
      throw new BadRequestException("Ocurrió un error al extraer las imágenes del archivo ZIP mm.");
    }
  }

  /* ------------------ Submétodos auxiliares ------------------ */

  private void extraerZipMm(Path source, Path destination) throws IOException {
    try (ZipFile zipFile = new ZipFile(source.toFile())) {
      zipFile.extractAll(destination.toString());
    }
  }

  private String[] listarArchivos(Path carpetaPath) {
    File carpeta = carpetaPath.toFile();
    String[] listado = carpeta.list();
    if (listado == null || listado.length == 0) {
      throw new BadRequestException("No existen elementos dentro del archivo ZIP.");
    }
    return listado;
  }

  private void validarArchivosMm(String[] listado, Mesa mesa) {
    final int cantidadPaginas = 2; // Debe contener 2 TIF
    int contarPdf = 0;
    int contarHojaControlAsistencia = 0;
    int contarRelacionMiembrosNoSorteados = 0;
    int cantidadTifLongitudCorrecta = 0;

    for (String archivo : listado) {
      String upper = archivo.toUpperCase();

      if (upper.endsWith(ConstantesFormatos.EXTENSION_FILE_TIF)
              && archivo.length() == ConstantesComunes.LONGITUD_CADENA_MM) {
        cantidadTifLongitudCorrecta++;
      }
      if (upper.endsWith(ConstantesFormatos.EXTENSION_FILE_PDF)) {
        contarPdf++;
      }
      if (upper.startsWith(mesa.getCodigo() + ConstantesComunes.COD_DOCUMENT_MIEMBROS_DE_MESA)) {
        contarHojaControlAsistencia++;
      }
      if (upper.startsWith(mesa.getCodigo() + ConstantesComunes.COD_DOCUMENT_MIEMBROS_DE_MESA_NO_SORTEADOS)) {
        contarRelacionMiembrosNoSorteados++;
      }
    }

    if (contarPdf != 1) {
      throw new BadRequestException("El zipeado no cuenta con el PDF "
              + mesa.getCodigo() + ConstantesFormatos.EXTENSION_FILE_PDF + ".");
    }
    if (contarHojaControlAsistencia != 1) {
      throw new BadRequestException("No se encontró la imagen TIF de hoja de control de asistencia.");
    }
    if (contarRelacionMiembrosNoSorteados != 1) {
      throw new BadRequestException("No se encontró la imagen TIF de la relación de miembros de mesa no sorteados.");
    }
    if (cantidadTifLongitudCorrecta != cantidadPaginas) {
      throw new BadRequestException("El zipeado debe contar con 2 imágenes TIF: " +
              "Hoja de Control de Asistencia y Relación de Miembros no Sorteados.");
    }
  }









  @Override
  public boolean validarContenidoZipLe(Mesa mesa, String filename) {
    Integer cantidadElectoresHabiles = mesa.getCantidadElectoresHabiles();

    Path rootPath = Paths.get(getPathUpload()).toAbsolutePath().normalize();
    Path sourcePath = buildSafePath(rootPath, filename);
    Path destinationPath = buildSafePath(rootPath, ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);

    try {
      extraerZip(sourcePath, destinationPath);
      File carpetaMesa = validarCarpetaMesa(destinationPath, mesa.getCodigo());
      String[] listado = validarContenidoCarpeta(carpetaMesa);

      return validarArchivosMesa(listado, cantidadElectoresHabiles);

    } catch (IOException e) {
      throw new BadRequestException("Error procesando el archivo ZIP: " + e.getMessage());
    }
  }

  /* ------------------ Submétodos auxiliares ------------------ */

  private Path buildSafePath(Path root, String filename) {
    Path resolved = root.resolve(StringUtils.cleanPath(filename)).normalize();
    if (!resolved.startsWith(root)) {
      throw new SecurityException("Acceso denegado: fuera del directorio permitido");
    }
    return resolved;
  }

  private void extraerZip(Path source, Path destination) throws IOException {
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        Path resolvedPath = destination.resolve(entry.getName()).normalize();
        if (!resolvedPath.startsWith(destination)) {
          throw new SecurityException("Archivo ZIP contiene rutas inválidas: " + entry.getName());
        }
        if (entry.isDirectory()) {
          Files.createDirectories(resolvedPath);
        } else {
          Files.createDirectories(resolvedPath.getParent());
          try (OutputStream os = Files.newOutputStream(resolvedPath)) {
            zis.transferTo(os);
          }
        }
      }
    }
  }

  private File validarCarpetaMesa(Path destinationPath, String codigoMesa) {
    File carpeta = destinationPath.resolve(codigoMesa).toFile();
    if (!carpeta.exists() || !carpeta.isDirectory()) {
      throw new BadRequestException("No se encontró la carpeta de la mesa: " + codigoMesa);
    }
    return carpeta;
  }

  private String[] validarContenidoCarpeta(File carpeta) {
    String[] listado = carpeta.list();
    if (listado == null || listado.length == 0) {
      throw new BadRequestException("No hay elementos dentro de la carpeta de la mesa");
    }
    return listado;
  }

  private boolean validarArchivosMesa(String[] listado, int cantidadElectoresHabiles) {
    int resto = cantidadElectoresHabiles % 10;
    int cantidadPaginas = cantidadElectoresHabiles / 10 + (resto > 0 ? 1 : 0);

    int contarTif = 0;
    int contarTifLongitudIncorrecta = 0;
    int contarPdf = 0;

    for (String nombre : listado) {
      String upper = nombre.toUpperCase();

      if (upper.endsWith(ConstantesFormatos.EXTENSION_FILE_TIF)) {
        contarTif++;
        if (nombre.length() != ConstantesComunes.LONGITUD_CADENA_LE) {
          contarTifLongitudIncorrecta++;
        }
      }
      if (upper.endsWith(ConstantesFormatos.EXTENSION_FILE_PDF)) {
        contarPdf++;
      }
    }

    if (contarPdf != 1) {
      throw new BadRequestException("No se encontró exactamente un archivo PDF dentro del ZIP");
    }
    if (contarTifLongitudIncorrecta != 0) {
      throw new BadRequestException("Los archivos TIFF tienen nombres con longitud incorrecta");
    }
    return contarTif == cantidadPaginas;
  }




  public void deleteAllFilesRepository() throws IOException {
    deleteDirectory(uploadDir);
  }

  @Override
  public void deleteAllFilesRepositoryAsync() throws IOException {
    Path originalDir = Paths.get(uploadDir);

    logger.info("Iniciando eliminación asíncrona del contenido de: {}", originalDir);

    CompletableFuture.runAsync(() -> {
      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
      try {
        if (isWindows) {
          deleteDirectory(originalDir.toString());
          logger.info("Eliminación asíncrona completada (Windows)");
        } else {
          Process process = new ProcessBuilder("find", originalDir.toString(), "-mindepth", "1", "-delete")
              .redirectErrorStream(true)
              .start();
          int exitCode = process.waitFor();
          if (exitCode != 0) {
            logger.warn("find -delete salió con código {}, reintentando con rm -rf", exitCode);
            Process fallback = new ProcessBuilder("bash", "-c",
                "rm -rf " + originalDir + "/*")
                .redirectErrorStream(true)
                .start();
            fallback.waitFor();
          }
          logger.info("Eliminación asíncrona completada (Linux)");
        }
      } catch (Exception e) {
        logger.error("Error en eliminación asíncrona de {}: {}", originalDir, e.getMessage(), e);
      }
    });
  }

  @Override
  public void deleteAllFilesRepositoryAsyncParalelo() throws IOException {
    Path originalDir = Paths.get(uploadDir);

    logger.info("Iniciando eliminación asíncrona paralela del contenido de: {}", originalDir);

    CompletableFuture.runAsync(() -> {
      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
      try {
        if (isWindows) {
          deleteDirectory(originalDir.toString());
          logger.info("Eliminación asíncrona paralela completada (Windows)");
        } else {
          Process process = new ProcessBuilder("bash", "-c",
              "find " + originalDir + " -mindepth 1 -print0 | xargs -0 -n 1000 -P 8 rm -rf")
              .redirectErrorStream(true)
              .start();
          int exitCode = process.waitFor();
          if (exitCode != 0) {
            logger.warn("Eliminación paralela salió con código {}, reintentando secuencial", exitCode);
            Process fallback = new ProcessBuilder("bash", "-c",
                "rm -rf " + originalDir + "/*")
                .redirectErrorStream(true)
                .start();
            fallback.waitFor();
          }
          logger.info("Eliminación asíncrona paralela completada (Linux)");
        }
      } catch (Exception e) {
        logger.error("Error en eliminación asíncrona paralela de {}: {}", originalDir, e.getMessage(), e);
      }
    });
  }

  @Override
  public void deleteAllFilesRepositoryAsyncSubCarpeta() throws IOException {
    if (uploadSubcarpeta == null || uploadSubcarpeta.isEmpty()) {
      logger.warn("No hay subcarpeta configurada, usando eliminación async estándar");
      deleteAllFilesRepositoryAsync();
      return;
    }

    Path subcarpetaPath = Paths.get(uploadDir).resolve(uploadSubcarpeta);
    Path renamedPath = Paths.get(uploadDir).resolve("_old_" + uploadSubcarpeta + "_" + System.currentTimeMillis());

    if (!Files.exists(subcarpetaPath)) {
      logger.info("Subcarpeta no existe, creándola: {}", subcarpetaPath);
      Files.createDirectories(subcarpetaPath);
      return;
    }

    // mv instantáneo (misma partición, solo cambia referencia en directorio padre)
    Files.move(subcarpetaPath, renamedPath);
    logger.info("Subcarpeta renombrada: {} -> {}", subcarpetaPath, renamedPath);

    // Recrear subcarpeta vacía para uso inmediato
    Files.createDirectories(subcarpetaPath);
    logger.info("Subcarpeta recreada: {}", subcarpetaPath);

    // Eliminar la renombrada en segundo plano
    CompletableFuture.runAsync(() -> {
      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
      try {
        logger.info("Eliminando en segundo plano: {}", renamedPath);
        if (isWindows) {
          deleteDirectory(renamedPath.toString());
          Files.deleteIfExists(renamedPath);
        } else {
          Process process = new ProcessBuilder("rm", "-rf", renamedPath.toString())
              .redirectErrorStream(true)
              .start();
          int exitCode = process.waitFor();
          if (exitCode != 0) {
            logger.warn("rm -rf salió con código {} para: {}", exitCode, renamedPath);
          }
        }
        logger.info("Eliminación en segundo plano completada: {}", renamedPath);
      } catch (Exception e) {
        logger.error("Error en eliminación en segundo plano de {}: {}", renamedPath, e.getMessage(), e);
      }
    });
  }


  public void deleteDirectory(String directoryPath) throws IOException {
    Path directory = Paths.get(directoryPath);
    Files.walkFileTree(directory, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(@NotNull Path file,
                                       @NotNull BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(@NotNull Path dir,
                                                IOException exc) throws IOException {
        if (!dir.equals(directory)) { // Evitar eliminar la carpeta raíz
          Files.delete(dir);        // Eliminar subdirectorios
        }
        return FileVisitResult.CONTINUE;
      }
    });
  }

  @Override
  public void storeFile(MultipartFile file, String fileName) throws IOException {
    Path targetPath = construirPathSeguro(fileName);
    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
  }

  @Override
  public void storeFile(File file, String fileName) throws IOException {
    Path targetPath = construirPathSeguro(fileName);
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      Files.copy(fileInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  @Override
  public void storeFile(byte[] file, String fileName) throws IOException {
    Path targetPath = construirPathSeguro(fileName);
    // Escritura directa sin SYNC para mejor rendimiento
    Files.write(targetPath, file);
  }

  @Override
  public void deleteFile(String fileName) throws IOException {
    Path targetPath = construirPathSeguro(fileName);
    Files.delete(targetPath);
  }

  @Override
  public void renameFile(String oldFileName, String newFileName) throws IOException {
    Path oldPath = construirPathSeguro(oldFileName);
    Path newPath = construirPathSeguro(newFileName);
    if (Files.exists(oldPath)) {
      Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Construir ruta segura evitando Path Traversal.
   */
  private Path construirPathSeguro(String fileName) {
    String cleanFileName = StringUtils.cleanPath(Objects.requireNonNull(fileName));

    Path targetPath = Paths.get(getPathUpload()).resolve(cleanFileName).normalize();

    Path rootPath = Paths.get(getPathUpload()).toAbsolutePath().normalize();

    if (!targetPath.startsWith(rootPath)) {
      throw new SecurityException("Intento de acceso fuera del directorio permitido: " + fileName);
    }

    return targetPath;
  }

  @Override
  public String detectFileType(MultipartFile file) throws IOException {
    Tika tika = new Tika();
    return tika.detect(file.getInputStream());
  }

}
