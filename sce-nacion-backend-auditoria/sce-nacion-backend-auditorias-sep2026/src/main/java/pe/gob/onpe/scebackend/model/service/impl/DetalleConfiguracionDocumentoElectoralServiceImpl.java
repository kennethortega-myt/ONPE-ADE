package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.exeption.NotFoundException;
import pe.gob.onpe.scebackend.model.dto.ColorDTO;
import pe.gob.onpe.scebackend.model.dto.request.DetalleConfigRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.DescargaResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.DetalleConfiguracionDocumentoElectoralResponseDTO;
import pe.gob.onpe.scebackend.model.entities.*;
import pe.gob.onpe.scebackend.model.mapper.IDetalleConfiguracionDocumentoElectoralMapper;
import pe.gob.onpe.scebackend.model.repository.DetalleConfiguracionDocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.DetalleTipoEleccionDocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.DocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.SeccionRepository;
import pe.gob.onpe.scebackend.model.service.IDetalleConfiguracionDocumentoElectoralService;
import pe.gob.onpe.scebackend.model.service.IFileStorageService;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.SceUtils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

@Service
public class DetalleConfiguracionDocumentoElectoralServiceImpl implements IDetalleConfiguracionDocumentoElectoralService {

    private final IDetalleConfiguracionDocumentoElectoralMapper detalleConfiguracionDocumentoElectoralMapper;

    private final DetalleConfiguracionDocumentoElectoralRepository detalleConfiguracionDocumentoElectoralRepository;

    private final DocumentoElectoralRepository documentoElectoralRepository;

    private final DetalleTipoEleccionDocumentoElectoralRepository detalleTipoEleccionDocumentoElectoralRepository;

    private final SeccionRepository seccionRepository;

    private final IFileStorageService fileStorageService;

    public DetalleConfiguracionDocumentoElectoralServiceImpl(IDetalleConfiguracionDocumentoElectoralMapper detalleConfiguracionDocumentoElectoralMapper,
     DetalleConfiguracionDocumentoElectoralRepository detalleConfiguracionDocumentoElectoralRepository, DocumentoElectoralRepository documentoElectoralRepository,
     DetalleTipoEleccionDocumentoElectoralRepository detalleTipoEleccionDocumentoElectoralRepository, SeccionRepository seccionRepository, IFileStorageService fileStorageService) {
        this.detalleConfiguracionDocumentoElectoralMapper = detalleConfiguracionDocumentoElectoralMapper;
        this.detalleConfiguracionDocumentoElectoralRepository = detalleConfiguracionDocumentoElectoralRepository;
        this.documentoElectoralRepository = documentoElectoralRepository;
        this.detalleTipoEleccionDocumentoElectoralRepository = detalleTipoEleccionDocumentoElectoralRepository;
        this.seccionRepository = seccionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional("tenantTransactionManager")
    public void guardarDetalleConfiguracion(DetalleConfigRequestDTO detalles, String usuario) {
        detalles.getDetalles().forEach(detalle -> {
            DetalleConfiguracionDocumentoElectoral detalleSeccion = this.detalleConfiguracionDocumentoElectoralMapper.dtoToSeccion(detalle);
            detalleSeccion.setUsuarioCreacion(usuario);
            detalleSeccion.setFechaCreacion(new Date());
            this.detalleConfiguracionDocumentoElectoralRepository.save(detalleSeccion);
        });
        if (Objects.nonNull(detalles.getDocumento())) {
            Integer idDocumentoElectoral = obtenerIdDocumentoElectoral(detalles);
            Integer idTipoEleccion = obtenerIdTipoEleccion(detalles);

            this.documentoElectoralRepository.updateDatos(idDocumentoElectoral, detalles.getDocumento().getAbreviatura(), detalles.getDocumento().getTamanioHoja(),
                    detalles.getDocumento().getMultipagina(), detalles.getDocumento().getCodigoBarraOrientacion());
            if((idTipoEleccion != 0 && idDocumentoElectoral != 0) || Objects.nonNull(detalles.getDetalles().getFirst().getDetalleTipoEleccionDocumentoElectoral().getTipoEleccion())){
                this.detalleTipoEleccionDocumentoElectoralRepository.updateDatos(idTipoEleccion, idDocumentoElectoral, detalles.getDocumento().getRangoInicial(), detalles.getDocumento().getRangoFinal(),
                        detalles.getDocumento().getDigitoChequeo(), detalles.getDocumento().getDigitoError());
            }
        }
    }

    private Integer obtenerIdDocumentoElectoral(DetalleConfigRequestDTO detalles) {
        var detalle = detalles.getDetalles().getFirst().getDetalleTipoEleccionDocumentoElectoral();
        if (detalle.getDocumentoElectoral() == null) {
            return this.detalleTipoEleccionDocumentoElectoralRepository.findById(detalle.getId())
                    .map(det -> det.getDocumentoElectoral() != null ? det.getDocumentoElectoral().getId() : 0)
                    .orElse(0);
        }
        return detalle.getDocumentoElectoral().getId();
    }

    private Integer obtenerIdTipoEleccion(DetalleConfigRequestDTO detalles) {
        var detalle = detalles.getDetalles().getFirst().getDetalleTipoEleccionDocumentoElectoral();
        if (detalle.getTipoEleccion() != null) {
            return detalle.getTipoEleccion().getId();
        }
        return this.detalleTipoEleccionDocumentoElectoralRepository.findById(detalle.getId())
                .map(det -> det.getTipoEleccion() != null ? det.getTipoEleccion().getId() : 0)
                .orElse(0);
    }


    @Override
    @Transactional("tenantTransactionManager")
    public List<DetalleConfiguracionDocumentoElectoralResponseDTO> obtenerDetalleByDetalleTipoEleccion(Integer detalleTipoEleccion) {
        List<DetalleConfiguracionDocumentoElectoral> lista = this.detalleConfiguracionDocumentoElectoralRepository.findByActivoAndDetalleTipoEleccionDocumentoElectoralId(SceConstantes.ACTIVO, detalleTipoEleccion);
        List<Seccion> listaSecciones = this.seccionRepository.findAll();
        listaSecciones.forEach(seccion -> {
            List<DetalleConfiguracionDocumentoElectoral> filtro = lista.stream().
                    filter(det -> Objects.equals(det.getSeccion().getId(), seccion.getId()))
                    .toList();
            if (filtro.isEmpty()) {
                DetalleConfiguracionDocumentoElectoral detalle = new DetalleConfiguracionDocumentoElectoral();
                detalle.setHabilitado(SceConstantes.NO_HABLITADO);
                detalle.setActivo(SceConstantes.ACTIVO);
                DetalleTipoEleccionDocumentoElectoral detTipoEleccion = new DetalleTipoEleccionDocumentoElectoral();
                detTipoEleccion.setId(detalleTipoEleccion);
                detalle.setDetalleTipoEleccionDocumentoElectoral(detTipoEleccion);
                detalle.setSeccion(seccion);
                lista.add(detalle);
            }
        });

        return lista.stream().map(this.detalleConfiguracionDocumentoElectoralMapper::detalleConfiguracionElectoralToDTO)
                .sorted(Comparator.comparing(DetalleConfiguracionDocumentoElectoralServiceImpl::getNombre)).toList();
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<DetalleConfiguracionDocumentoElectoralResponseDTO> obtenerDetalleByDetalleTipoEleccionPaso3(Integer detalleTipoEleccion) {
        SecureRandom random = new SecureRandom();
        List<DetalleConfiguracionDocumentoElectoral> lista = this.detalleConfiguracionDocumentoElectoralRepository.
                findByActivoAndHabilitadoAndDetalleTipoEleccionDocumentoElectoralId(SceConstantes.ACTIVO, SceConstantes.HABILITADO, detalleTipoEleccion);
        List<ColorDTO> colores = new ArrayList<>(SceConstantes.COLORES);
        return lista.stream().map(det -> {
                    DetalleConfiguracionDocumentoElectoralResponseDTO detalleDto = this.detalleConfiguracionDocumentoElectoralMapper.
                            detalleConfiguracionElectoralToDTO(det);
                    int id = random.nextInt(colores.size());
                    if(colores.isEmpty()){
                        detalleDto.setColorHex("#e92228");
                        detalleDto.setIdColor(100);
                    }else{
                        detalleDto.setColorHex(colores.get(id).getColor());
                        detalleDto.setIdColor(colores.get(id).getId());
                        colores.remove(colores.get(id));
                    }

                    if (Objects.nonNull(detalleDto.getDetalleTipoEleccionDocumentoElectoral().getArchivo())) {
                        DescargaResponseDTO descarga;
                        try {
                            descarga = this.fileStorageService.get(detalleDto.getDetalleTipoEleccionDocumentoElectoral().getArchivo().getId());
                            detalleDto.getDetalleTipoEleccionDocumentoElectoral().setArchivoBase64(SceUtils.imageConverterBase64(descarga.getResource().getFile()));
                        } catch (IOException | NotFoundException e) {
                            detalleDto.getDetalleTipoEleccionDocumentoElectoral().setArchivoBase64(null);
                        }

                    }
                    return detalleDto;

                }).sorted(Comparator.comparing(DetalleConfiguracionDocumentoElectoralServiceImpl::getNombre)).
                toList();
    }

    private static String getNombre(DetalleConfiguracionDocumentoElectoralResponseDTO det) {
        return det.getSeccion().getNombre();
    }

}
