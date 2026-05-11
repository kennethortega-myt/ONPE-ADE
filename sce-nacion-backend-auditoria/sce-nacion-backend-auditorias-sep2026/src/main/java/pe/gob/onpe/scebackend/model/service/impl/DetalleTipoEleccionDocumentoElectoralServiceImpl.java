package pe.gob.onpe.scebackend.model.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.exeption.NotFoundException;
import pe.gob.onpe.scebackend.model.dto.request.DetalleTipoEleccionDocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.DescargaResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralResponseDto;
import pe.gob.onpe.scebackend.model.entities.DetalleTipoEleccionDocumentoElectoral;
import pe.gob.onpe.scebackend.model.entities.DocumentoElectoral;
import pe.gob.onpe.scebackend.model.entities.TipoEleccion;
import pe.gob.onpe.scebackend.model.mapper.IDetalleTipoProcesoDocumentoElectoralMapper;
import pe.gob.onpe.scebackend.model.repository.DetalleTipoEleccionDocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.DocumentoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.IDetalleTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.scebackend.model.service.IFileStorageService;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.SceUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DetalleTipoEleccionDocumentoElectoralServiceImpl implements IDetalleTipoEleccionDocumentoElectoralService {

    private final IDetalleTipoProcesoDocumentoElectoralMapper detalleTipoProcesoDocumentoElectoralMapper;

    private final DetalleTipoEleccionDocumentoElectoralRepository detalleTipoEleccionDocumentoElectoralRepository;

    private final DocumentoElectoralRepository documentoElectoralRepository;

    private final IFileStorageService fileStorageService;

    public DetalleTipoEleccionDocumentoElectoralServiceImpl(IDetalleTipoProcesoDocumentoElectoralMapper detalleTipoProcesoDocumentoElectoralMapper, DetalleTipoEleccionDocumentoElectoralRepository detalleTipoEleccionDocumentoElectoralRepository, DocumentoElectoralRepository documentoElectoralRepository, IFileStorageService fileStorageService) {
        this.detalleTipoProcesoDocumentoElectoralMapper = detalleTipoProcesoDocumentoElectoralMapper;
        this.detalleTipoEleccionDocumentoElectoralRepository = detalleTipoEleccionDocumentoElectoralRepository;
        this.documentoElectoralRepository = documentoElectoralRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional("tenantTransactionManager")
    public void guardarDetalle(DetalleTipoEleccionDocumentoElectoralRequestDto detalle, String usuario) {
        DetalleTipoEleccionDocumentoElectoral detalleTipo =
                this.detalleTipoProcesoDocumentoElectoralMapper.dtoToDetalleTipoProcesoElectoral(detalle);
        detalleTipo.setUsuarioCreacion(usuario);
        detalleTipo.setFechaCreacion(new Date());
        detalleTipo.setEstado(detalleTipo.getRequerido());
        this.detalleTipoEleccionDocumentoElectoralRepository.save(detalleTipo);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<DetalleTipoEleccionDocumentoElectoralResponseDto> obtenerDetalleByTipo(Integer tipoEleccion, boolean isConfigGeneral) {
        /* ============================================================
         * 1. Obtener detalles PERSISTIDOS ACTIVOS
         *    (solo para mostrar)
         * ============================================================ */
        List<DetalleTipoEleccionDocumentoElectoral> detallesPersistidosActivos =
                isConfigGeneral
                        ? detalleTipoEleccionDocumentoElectoralRepository
                        .findByActivoAndTipoEleccionIsNull(SceConstantes.ACTIVO)
                        : detalleTipoEleccionDocumentoElectoralRepository
                        .findByActivoAndTipoEleccionId(SceConstantes.ACTIVO, tipoEleccion);

        /* ============================================================
         * 2. Obtener documentos CONFIGURADOS y ACTIVOS
         * ============================================================ */
        List<DocumentoElectoral> documentosConfigurados = filtrarDocumentoElectoral(
                obtenerConfig(isConfigGeneral
                        ? SceConstantes.IS_CONFIG_GENERAL
                        : SceConstantes.NOT_CONFIG_GENERAL)
        ).stream()
                .filter(doc -> Objects.equals(doc.getActivo(), SceConstantes.ACTIVO))
                .toList();

        Set<Integer> idsDocumentosValidos = documentosConfigurados.stream()
                .map(DocumentoElectoral::getId)
                .collect(Collectors.toSet());

        /* ============================================================
         * 3. Filtrar detalles ACTIVOS solo si su documento sigue válido
         * ============================================================ */
        List<DetalleTipoEleccionDocumentoElectoral> detallesValidos =
                detallesPersistidosActivos.stream()
                        .filter(det ->
                                det.getDocumentoElectoral() != null &&
                                        idsDocumentosValidos.contains(det.getDocumentoElectoral().getId())
                        )
                        .toList();

        /* ============================================================
         * 4. Resultado inicial
         * ============================================================ */
        List<DetalleTipoEleccionDocumentoElectoral> resultado =
                new ArrayList<>(detallesValidos);

        /* ============================================================
         * 5. Obtener TODOS los detalles (activos + inactivos)
         *    → para NO recrear los inactivos
         * ============================================================ */
        List<DetalleTipoEleccionDocumentoElectoral> detallesPersistidosTodos =
                isConfigGeneral
                        ? detalleTipoEleccionDocumentoElectoralRepository.findByTipoEleccionIsNull()
                        : detalleTipoEleccionDocumentoElectoralRepository.findByTipoEleccionId(tipoEleccion);

        Set<Integer> idsDocsConDetalle =
                detallesPersistidosTodos.stream()
                        .filter(d -> d.getDocumentoElectoral() != null)
                        .map(d -> d.getDocumentoElectoral().getId())
                        .collect(Collectors.toSet());

        /* ============================================================
         * 6. Referencia TipoEleccion
         * ============================================================ */
        TipoEleccion tipoEleccionRef = null;
        if (!isConfigGeneral) {
            tipoEleccionRef = new TipoEleccion();
            tipoEleccionRef.setId(tipoEleccion);
        }

        /* ============================================================
         * 7. Agregar SOLO documentos realmente nuevos
         *    (nunca tuvieron detalle)
         * ============================================================ */
        for (DocumentoElectoral documento : documentosConfigurados) {

            if (!idsDocsConDetalle.contains(documento.getId())) {

                DetalleTipoEleccionDocumentoElectoral nuevoDetalle =
                        new DetalleTipoEleccionDocumentoElectoral();

                nuevoDetalle.setDocumentoElectoral(documento);
                nuevoDetalle.setRequerido(SceConstantes.NO_REQUERIDO);
                nuevoDetalle.setActivo(SceConstantes.ACTIVO);
                nuevoDetalle.setTipoEleccion(tipoEleccionRef);

                resultado.add(nuevoDetalle);
            }
        }

        /* ============================================================
         * 8. Mapear a DTO y ordenar
         * ============================================================ */
        return resultado.stream()
                .map(detalleTipoProcesoDocumentoElectoralMapper::detalleTipoProcesoElectoralToDTO)
                .sorted(Comparator.comparing(
                        DetalleTipoEleccionDocumentoElectoralServiceImpl::getNombre))
                .toList();

    }

    private List<DocumentoElectoral> obtenerConfig(Integer config){
        return this.documentoElectoralRepository.findByActivoAndConfiguracionGeneral(SceConstantes.ACTIVO, config);
    }

    private static String getNombre(DetalleTipoEleccionDocumentoElectoralResponseDto det) {
        return det.getDocumentoElectoral().getNombre();
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<DetalleTipoEleccionDocumentoElectoralResponseDto> obtenerDetalleByTipoPaso2(Integer tipoEleccion) {
        List<DetalleTipoEleccionDocumentoElectoral> lista =
                this.detalleTipoEleccionDocumentoElectoralRepository.findByActivoAndRequeridoAndTipoEleccionId(SceConstantes.ACTIVO,
                        SceConstantes.HABILITADO, tipoEleccion);
        return this.listaDetallesOrdenados(lista);
    }

    @Override
    @Transactional("tenantTransactionManager")
    public List<DetalleTipoEleccionDocumentoElectoralResponseDto> obtenerDetalleByTipoPaso2Config() {
        List<DetalleTipoEleccionDocumentoElectoral> lista =
                this.detalleTipoEleccionDocumentoElectoralRepository.findByActivoAndRequeridoAndTipoEleccionIsNull(SceConstantes.ACTIVO,
                        SceConstantes.HABILITADO);
        return this.listaDetallesOrdenados(lista);
    }

    @Transactional("tenantTransactionManager")
    @Override
    public void actualizarArchivo(Integer idDetalle, Long idArchivo) throws IOException {
        try {
            this.detalleTipoEleccionDocumentoElectoralRepository.updateArchivo(idArchivo, idDetalle);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }

    }

    /**
     * Filtrar solo los padres sin hijos e hijos solo
     */
    private List<DocumentoElectoral> filtrarDocumentoElectoral(List<DocumentoElectoral> listaDocumentoElectoral) {
        List<DocumentoElectoral> listaFiltrada = new ArrayList<>();
        List<Integer> listaPadres = listaDocumentoElectoral.stream().filter(x -> Objects.nonNull(x.getDocumentoElectoralPadre()))
                .map(x -> x.getDocumentoElectoralPadre().getId()).distinct().toList();
        listaDocumentoElectoral.forEach(docu -> {
            if (Objects.nonNull(docu.getDocumentoElectoralPadre())) {
                listaFiltrada.add(docu);
            } else {
                if (!listaPadres.contains(docu.getId())) {
                    listaFiltrada.add(docu);
                }
            }
        });
        return listaFiltrada;
    }

    private List<DetalleTipoEleccionDocumentoElectoralResponseDto> listaDetallesOrdenados(List<DetalleTipoEleccionDocumentoElectoral> lista) {
        return lista.stream().map(this.detalleTipoProcesoDocumentoElectoralMapper::detalleTipoProcesoElectoralToDTO)
                .map(det -> {
                    if (Objects.nonNull(det.getArchivo())) {
                        DescargaResponseDTO descarga;
                        try {
                            descarga = this.fileStorageService.get(det.getArchivo().getId());
                            det.setArchivoBase64(SceUtils.imageConverterBase64(descarga.getResource().getFile()));
                        } catch (IOException | NotFoundException e) {
                            det.setArchivoBase64(null);
                        }

                    }
                    return det;
                })
                .sorted(Comparator.comparing(DetalleTipoEleccionDocumentoElectoralServiceImpl::getNombre)).toList();
    }

}
