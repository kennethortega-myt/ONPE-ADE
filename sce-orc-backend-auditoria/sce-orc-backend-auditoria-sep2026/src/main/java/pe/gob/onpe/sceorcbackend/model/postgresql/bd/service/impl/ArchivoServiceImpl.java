package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ArchivoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Service
public class ArchivoServiceImpl implements ArchivoService {

    private static final Logger logger = LoggerFactory.getLogger(ArchivoServiceImpl.class);

    private final ArchivoRepository archivoRepository;
    private final StorageService storageService;

    public ArchivoServiceImpl(ArchivoRepository archivoRepository,StorageService storageService) {
        this.archivoRepository = archivoRepository;
        this.storageService = storageService;
    }

    @Override
    public List<Archivo> findByNombre(String nombre) {
        return this.archivoRepository.findByNombre(nombre);
    }

    @Override
    public List<Archivo> findByNombreAndActivo(String nombre, Integer activo){
        return this.archivoRepository.findByNombreAndActivo(nombre, activo);
    }

    @Override
    public List<Archivo> findByGuidAndActivo(String gui, Integer activo) {
        return this.archivoRepository.findByGuidAndActivo(gui, activo);
    }

    @Override
    public Optional<Archivo> findById(Long id) {
        return this.archivoRepository.findById(id);
    }

    @Override
    public Archivo getArchivoById(Long id) {
        Optional<Archivo> optArchivo = this.archivoRepository.findById(id);
        return optArchivo.orElse(null);
    }

    @Override
    public List<Archivo> findAllById(List<Long> ids) {
        return this.archivoRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        this.archivoRepository.deleteAllInBatch();
    }

    @Override
    @Transactional
    public void deleteAllOptimized() {
        int batchSize = 5000; // Procesa 5000 registros por lote
        int deletedRows;
        do {
            deletedRows = this.archivoRepository.deleteInBatch(batchSize);
        } while (deletedRows > 0);
    }

    @Override
    public Archivo guardarArchivo(MultipartFile file, String usuario, String codigoCentroComputo, Optional<Integer> codigoDocumentoElectoralPr) {
        try {
            // Leer el archivo una sola vez en memoria
            byte[] fileBytes = file.getBytes();
            
            // Calcular hash del contenido en memoria (sin leer el stream múltiples veces)
            String sha256Hash = DigestUtils.sha256Hex(fileBytes);
            String guid = codigoCentroComputo.concat(ConstantesComunes.GUION_MEDIO).concat(sha256Hash);
            
            // Detectar tipo de archivo
            String detectedType = this.storageService.detectFileType(file);
            
            // Crear y guardar entidad en BD primero (rápido)
            Archivo archivo = new Archivo();
            archivo.setNombre(file.getOriginalFilename());
            archivo.setFormato(detectedType);
            archivo.setPeso(String.valueOf(fileBytes.length));
            archivo.setActivo(ConstantesComunes.ACTIVO);
            archivo.setGuid(guid);
            archivo.setUsuarioCreacion(usuario);
            archivo.setRuta(this.storageService.getPathUpload());
            archivo.setCodigoDocumentoElectoral(codigoDocumentoElectoralPr.orElse(null));
            archivo.setFechaCreacion(new Date());

            // Extraer metadata del archivo
            Map<String, Object> metadata = extraerMetadata(fileBytes, detectedType);
            if (metadata != null && !metadata.isEmpty()) {
                metadata.put("guid", guid);
                metadata.put("nombreArchivo", file.getOriginalFilename());
                archivo.setMetadata(metadata);
                logger.info("[ARCHIVO] Metadata extraída para guid={}, keys={}", guid, metadata.keySet());
            } else {
                logger.warn("[ARCHIVO] No se pudo extraer metadata para guid={}, detectedType={}, fileSize={}", guid, detectedType, fileBytes.length);
            }

            this.archivoRepository.save(archivo);
            
            // Guardar archivo en NFS usando los bytes en memoria (evita releer el MultipartFile)
            this.storageService.storeFile(fileBytes, archivo.getGuid());
            
            return archivo;
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public void save(Archivo archivo) {
        this.archivoRepository.save(archivo);
    }

    @Override
    public void saveAll(List<Archivo> k) {
        this.archivoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.archivoRepository.deleteAll();
    }

    @Override
    public List<Archivo> findAll() {
        return this.archivoRepository.findAll();
    }


    @Override
    public String getPathUpload() {
        return this.storageService.getPathUpload();
    }


    public void storeFile(MultipartFile multipartFile, String fileName){
        try {
            this.storageService.storeFile(multipartFile, fileName);
        } catch (IOException e) {
            throw new BadRequestException(
                    "No se puede guardar el archivo en el repositorio de imagenes."
            );
        }
    }

    @Override
    public void storeFile(File file, String fileName) {
        try {
            this.storageService.storeFile(file, fileName);
        } catch (IOException e) {
            throw new BadRequestException(
                    "No se puede guardar el archivo en el repositorio de imagenes."
            );
        }
    }


    public Archivo saveArchivo(String fileName, Long sizeFile, String guid, String usuario, String detectedType) {
        Archivo archivo = new Archivo();
        archivo.setNombre(fileName);
        archivo.setFormato(detectedType);
        archivo.setPeso(String.valueOf(sizeFile));
        archivo.setActivo(ConstantesComunes.ACTIVO);
        archivo.setGuid(guid);
        archivo.setRuta(this.storageService.getPathUpload());
        archivo.setUsuarioCreacion(usuario);
        archivo.setFechaCreacion(new Date());
        this.archivoRepository.save(archivo);
        return archivo;
    }

    @Override
    public boolean validarExtraerContenidoZipLe(Mesa mesa, String nombreArchivo) {
        return this.storageService.validarContenidoZipLe(mesa , nombreArchivo);
    }

    @Override
    public void validarExtraerContenidoZipMm(Mesa mesa, String nombreArchivo) {
        this.storageService.validarContenidoZipMm(mesa , nombreArchivo);
    }

    @Override
    public void renameFile(String oldFileName, String newFileName) {
        try {
            this.storageService.renameFile(oldFileName, newFileName);
        } catch (IOException e) {
            throw new BadRequestException("No se puede renombrar el archivo en el repositorio de imágenes.");
        }
    }

    @Override
    public long contarArchivosOld(String guidOriginal) {
        return this.archivoRepository.countByGuidStartingWithAndActivo(guidOriginal + "_old_", ConstantesComunes.INACTIVO);
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            this.storageService.deleteFile(fileName);
        } catch (IOException e) {
            throw new BadRequestException("No se puede eliminar el archivo del repositorio de imágenes.");
        }
    }

    @Override
    public String obtegerUui(MultipartFile file, TokenInfo tokenInfo) {
        String guid;
        try {
            guid = tokenInfo.getCodigoCentroComputo().concat(ConstantesComunes.GUION_MEDIO).concat(DigestUtils.sha256Hex(file.getInputStream()));
        } catch (IOException e) {
            throw new BadRequestException("No se puedo generar el guid del archivo.");
        }
        return guid;
    }

    /**
     * Extrae metadata de un archivo de imagen (TIFF, JPEG, PNG, etc.) usando ImageIO + TwelveMonkeys.
     * Todo queda plano en un solo nivel.
     */
    private Map<String, Object> extraerMetadata(byte[] fileBytes, String detectedType) {
        if (fileBytes == null || fileBytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
             ImageInputStream iis = ImageIO.createImageInputStream(bais)) {

            if (iis == null) {
                return null;
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                return null;
            }

            ImageReader reader = readers.next();
            reader.setInput(iis, false);

            Map<String, Object> metadata = new LinkedHashMap<>();

            // Dimensiones de la imagen
            metadata.put("width", reader.getWidth(0));
            metadata.put("height", reader.getHeight(0));
            metadata.put("numImages", reader.getNumImages(true));
            metadata.put("formatName", reader.getFormatName());

            IIOMetadata imageMetadata = reader.getImageMetadata(0);
            if (imageMetadata != null) {
                // Tags TIFF nativos (plano: nombre del tag -> valor)
                for (String formatName : imageMetadata.getMetadataFormatNames()) {
                    if (formatName.contains("tiff")) {
                        Node root = imageMetadata.getAsTree(formatName);
                        metadata.putAll(extraerTagsTiff(root));
                    }
                }

                // Metadata estándar javax_imageio (plano: Categoria.Elemento -> valor)
                String stdFormat = "javax_imageio_1.0";
                if (Arrays.asList(imageMetadata.getMetadataFormatNames()).contains(stdFormat)) {
                    Node root = imageMetadata.getAsTree(stdFormat);
                    metadata.putAll(extraerMetadataEstandar(root));
                }
            }

            reader.dispose();
            return metadata;

        } catch (Exception e) {
            logger.warn("[ARCHIVO] No se pudo extraer metadata del archivo (tipo={}): {}", detectedType, e.getMessage());
            return null;
        }
    }

    /**
     * Extrae tags TIFF del árbol nativo IIO como mapa plano nombre -> valor.
     */
    private Map<String, String> extraerTagsTiff(Node root) {
        Map<String, String> tags = new LinkedHashMap<>();
        NodeList ifdNodes = root.getChildNodes();
        if (ifdNodes == null) return tags;

        for (int i = 0; i < ifdNodes.getLength(); i++) {
            Node ifdNode = ifdNodes.item(i);
            NodeList fields = ifdNode.getChildNodes();
            if (fields == null) continue;

            for (int j = 0; j < fields.getLength(); j++) {
                Node field = fields.item(j);
                if (!"TIFFField".equals(field.getNodeName())) continue;

                NamedNodeMap fieldAttrs = field.getAttributes();
                if (fieldAttrs == null) continue;

                Node nameAttr = fieldAttrs.getNamedItem("name");
                String tagName = nameAttr != null ? nameAttr.getNodeValue() : "tag_" + getAttrValue(fieldAttrs, "number");

                List<String> valores = new ArrayList<>();
                extraerValoresTiffField(field, valores);

                if (!valores.isEmpty()) {
                    tags.put(tagName, valores.size() == 1 ? valores.get(0) : String.join(", ", valores));
                }
            }
        }
        return tags;
    }

    private void extraerValoresTiffField(Node node, List<String> valores) {
        NodeList children = node.getChildNodes();
        if (children == null) return;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            NamedNodeMap attrs = child.getAttributes();

            if (attrs != null) {
                Node valueAttr = attrs.getNamedItem("value");
                if (valueAttr != null) {
                    valores.add(valueAttr.getNodeValue());
                }
            }
            extraerValoresTiffField(child, valores);
        }
    }

    /**
     * Extrae metadata del formato estándar javax_imageio_1.0 como mapa plano.
     */
    private Map<String, String> extraerMetadataEstandar(Node root) {
        Map<String, String> result = new LinkedHashMap<>();
        NodeList categories = root.getChildNodes();
        if (categories == null) return result;

        for (int i = 0; i < categories.getLength(); i++) {
            Node category = categories.item(i);
            String catName = category.getNodeName();
            NodeList elements = category.getChildNodes();
            if (elements == null) continue;

            for (int j = 0; j < elements.getLength(); j++) {
                Node element = elements.item(j);
                NamedNodeMap attrs = element.getAttributes();
                if (attrs == null) continue;

                String elemName = element.getNodeName();
                Node valueAttr = attrs.getNamedItem("value");
                if (valueAttr != null) {
                    result.put(catName + "." + elemName, valueAttr.getNodeValue());
                } else {
                    Node nameAttr = attrs.getNamedItem("name");
                    if (nameAttr != null) {
                        result.put(catName + "." + elemName, nameAttr.getNodeValue());
                    }
                }
            }
        }
        return result;
    }

    private String getAttrValue(NamedNodeMap attrs, String name) {
        Node attr = attrs.getNamedItem(name);
        return attr != null ? attr.getNodeValue() : "";
    }


}
