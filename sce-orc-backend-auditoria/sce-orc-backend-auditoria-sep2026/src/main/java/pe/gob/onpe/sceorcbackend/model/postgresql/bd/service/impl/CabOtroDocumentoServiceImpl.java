package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.DetOtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.OtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.ResumenOtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository.DocumentoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabOtroDocumentoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.MesaInfo;
import pe.gob.onpe.sceorcbackend.utils.*;

import java.time.Instant;
import java.util.*;

@Service
public class CabOtroDocumentoServiceImpl implements CabOtroDocumentoService {

    private final CabOtroDocumentoRepository cabOtroDocumentoRepository;
    private final DetOtroDocumentoRepository  detOtroDocumentoRepository;
    private final DocumentoElectoralRepository documentoElectoralRepository;
    private final ArchivoService archivoService;
    private final CentroComputoRepository  centroComputoRepository;
    private final UtilSceService  utilSceService;
    private final MesaRepository mesaRepository;
    private final OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService;

    public CabOtroDocumentoServiceImpl(CabOtroDocumentoRepository cabOtroDocumentoRepository,
                                       DocumentoElectoralRepository documentoElectoralRepository,
                                       ArchivoService archivoService,CentroComputoRepository  centroComputoRepository,
                                       UtilSceService  utilSceService,
                                       DetOtroDocumentoRepository  detOtroDocumentoRepository,
                                       MesaRepository mesaRepository,
                                       OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService) {
        this.cabOtroDocumentoRepository = cabOtroDocumentoRepository;
        this.documentoElectoralRepository = documentoElectoralRepository;
        this.archivoService = archivoService;
        this.centroComputoRepository = centroComputoRepository;
        this.utilSceService = utilSceService;
        this.detOtroDocumentoRepository = detOtroDocumentoRepository;
        this.mesaRepository = mesaRepository;
        this.detalleCatalogoEstructuraService = detalleCatalogoEstructuraService;
    }

    @Override
    public void deleteAllInBatch() {
        this.cabOtroDocumentoRepository.deleteAllInBatch();
    }

    @Override
    @Transactional
    public void registrarNuevoDocumento(TokenInfo tokenInfo,
    		Long idOtroDocumento ,String numeroDocumento,
                                             String abreviaturaDocumento, Integer numeroPaginas,MultipartFile file) {

        CentroComputo centroComputo = centroComputoRepository.findByCodigo(tokenInfo.getCodigoCentroComputo())
                .orElseThrow(() -> new BadRequestException(String.format("Centro de cómputo %s no se encuentra registrado.", tokenInfo.getCodigoCentroComputo())));

        Optional.ofNullable(documentoElectoralRepository.findByAbreviatura(abreviaturaDocumento))
                .orElseThrow(() -> new BadRequestException(String.format("La abreviatura %s no se encuentra registrada.", abreviaturaDocumento)));

        this.utilSceService.validarArchivoEscaneado(file, ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_DENUNCIAS, ConstantesComunes.VACIO);

        if (idOtroDocumento != null) {
            CabOtroDocumento cabOtroDocumento = this.cabOtroDocumentoRepository.findById(idOtroDocumento)
                    .orElseThrow(() -> new BadRequestException(String.format("El documento con identificador : %d no se encuentra registrado.", idOtroDocumento)));

            if (!Objects.equals(ConstantesOtrosDocumentos.ESTADO_DIGTAL_RECHAZADO, cabOtroDocumento.getEstadoDigitalizacion())) {
                throw new BadRequestException(String.format(
                        "El documento %s debe estar en estado rechazado en control de digitalización o calidad.", cabOtroDocumento.getNumeroDocumento()));
            }
            procesarOtroDocumentoDigitalizado(cabOtroDocumento, centroComputo , numeroDocumento, abreviaturaDocumento, numeroPaginas, file,tokenInfo);
        }else{
            Optional<CabOtroDocumento> cabOtroDocumentoOptional = this.cabOtroDocumentoRepository.findByNumeroDocumento(numeroDocumento).stream().findFirst();
            if (cabOtroDocumentoOptional.isPresent()) {
                if (Objects.equals(ConstantesOtrosDocumentos.ESTADO_DIGTAL_RECHAZADO, cabOtroDocumentoOptional.get().getEstadoDigitalizacion())) {
                    throw new BadRequestException(String.format(
                            "El documento : %s está en estado rechazado en control de digitalización o calidad, procesarla desde el menú Ver Documentos Escaneados, seleccionando denuncias.",
                            numeroDocumento));
                }
                throw new BadRequestException(String.format("El número de documento %s ya se encuentra registrado.", numeroDocumento));
            }

            this.utilSceService.validarGuidUnico(tokenInfo, file);
            this.utilSceService.validarNombreArchivoUnico(file);
            procesarOtroDocumentoDigitalizado(null, centroComputo , numeroDocumento, abreviaturaDocumento, numeroPaginas, file,tokenInfo);
        }
    }


    private void procesarOtroDocumentoDigitalizado(CabOtroDocumento cabOtroDocumento, CentroComputo centroComputo ,String numeroDocumento,
                                                   String abreviaturaDocumento, Integer numeroPaginas,MultipartFile file, TokenInfo tokenInfo) {

        if(cabOtroDocumento!=null) {
            Archivo archivo = this.archivoService.guardarArchivo(file, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), Optional.empty());
            cabOtroDocumento.setIdArchivoOtroDocumento(archivo.getId());
            cabOtroDocumento.setNumeroPaginas(numeroPaginas);
            cabOtroDocumento.setNumeroDocumento(numeroDocumento);
            cabOtroDocumento.setEstadoDigitalizacion(ConstantesOtrosDocumentos.ESTADO_DIGTAL_DIGITALIZADO);
            cabOtroDocumento.setAudFechaModificacion(new Date());
            cabOtroDocumento.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
            this.cabOtroDocumentoRepository.save(cabOtroDocumento);

        } else {//documento nuevo
            Archivo archivo = this.archivoService.guardarArchivo(file, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), Optional.empty());
            CabOtroDocumento cabOtroDocumentoNew = CabOtroDocumento
                    .builder()
                    .centroComputo(centroComputo)
                    .numeroDocumento(numeroDocumento)
                    .numeroPaginas(numeroPaginas)
                    .estadoDigitalizacion(ConstantesOtrosDocumentos.ESTADO_DIGTAL_DIGITALIZADO)
                    .estadoDocumento(ConstantesOtrosDocumentos.ESTADO_DOC_SIN_PROCESAR)
                    .codTipoDocumento(abreviaturaDocumento)
                    .idArchivoOtroDocumento(archivo.getId())
                    .activo(ConstantesComunes.ACTIVO)
                    .audUsuarioCreacion(tokenInfo.getNombreUsuario())
                    .audFechaCreacion(new Date())
                    .build();
            this.cabOtroDocumentoRepository.save(cabOtroDocumentoNew);
        }
    }

    @Override
    public List<OtroDocumentoDto> listarOtrosDocumentosDigitalizados(String nombreUsuario) {

        List<DetCatalogoEstructuraDTO> estadosDocumento = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.MAE_ESTADOS_OTRO_DOCUMENTO,
                ConstantesCatalogo.DET_ESTADOS_OTRO_DOCUMENTO
        );
        List<DetCatalogoEstructuraDTO> estadosDigtalOtroDocumento = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_OTRO_DOCUMENTO,
                ConstantesCatalogo.DET_ESTADOS_DIGITALIZACION_OTRO_DOCUMENTO
        );


        return cabOtroDocumentoRepository.findAllByOrderByAudFechaModificacionDesc().stream()
                .map(cab -> {
                    Archivo archivo = Optional.ofNullable(cab.getIdArchivoOtroDocumento())
                            .map(id -> archivoService.getArchivoById(id.longValue()))
                            .orElse(null);

                    DocumentoElectoral documentoElectoral = Optional.ofNullable(cab.getCodTipoDocumento())
                            .map(documentoElectoralRepository::findByAbreviatura)
                            .orElse(null);

                    return buildOtroDocumentoDto(cab, archivo, documentoElectoral, estadosDocumento, estadosDigtalOtroDocumento);
                })
                .toList();

    }

    @Override
    @Transactional
    public List<OtroDocumentoDto> listarControlDigitalizacion(String usuario, String abreviaturaDocumento) {

        var estadosDocumento = detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.MAE_ESTADOS_OTRO_DOCUMENTO,
                ConstantesCatalogo.DET_ESTADOS_OTRO_DOCUMENTO
        );

        var estadosDigitalOtroDocumento = detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_OTRO_DOCUMENTO,
                ConstantesCatalogo.DET_ESTADOS_DIGITALIZACION_OTRO_DOCUMENTO
        );

        // Buscar documentos asignados al usuario
        var cabOtroDocumentoList = cabOtroDocumentoRepository
                .findByEstadoDigitalizacionAndCodTipoDocumentoAndUsuarioControlAndActivo(
                        ConstantesOtrosDocumentos.ESTADO_DIGTAL_DIGITALIZADO,
                        abreviaturaDocumento,
                        usuario,
                        ConstantesComunes.ACTIVO
                );

        // Si no tiene documentos asignados o son insuficientes, completar hasta el límite
        if (cabOtroDocumentoList.isEmpty() || cabOtroDocumentoList.size() < ConstantesComunes.N_DISTRIBUCION_CONTROL_DIGTAL_DENUNCIAS) {
            int nuevaDistribucion = ConstantesComunes.N_DISTRIBUCION_CONTROL_DIGTAL_DENUNCIAS - cabOtroDocumentoList.size();
            
            var documentosLibres = cabOtroDocumentoRepository
                    .findByEstadoDigitalizacionAndCodTipoDocumentoAndUsuarioControlIsNullAndActivo(
                            ConstantesOtrosDocumentos.ESTADO_DIGTAL_DIGITALIZADO,
                            abreviaturaDocumento,
                            ConstantesComunes.ACTIVO
                    );

            Collections.shuffle(documentosLibres);
            var documentosAdicionales = documentosLibres.stream()
                    .limit(nuevaDistribucion)
                    .toList();

            // Crear nueva lista combinada
            var listaCombinada = new ArrayList<>(cabOtroDocumentoList);
            listaCombinada.addAll(documentosAdicionales);
            cabOtroDocumentoList = listaCombinada;
        }

        // Mapear resultados
        var documentosDto = cabOtroDocumentoList.stream().map(cab -> {
            if (cab.getUsuarioControl() == null) {
                cab.setUsuarioControl(usuario);
                cab.setFechaUsuarioControl(Date.from(Instant.now()));
            }

            var documentoElectoral = Optional.ofNullable(cab.getCodTipoDocumento())
                    .map(documentoElectoralRepository::findByAbreviatura)
                    .orElse(null);

            var archivo = Optional.ofNullable(cab.getIdArchivoOtroDocumento())
                    .map(id -> archivoService.getArchivoById(id.longValue()))
                    .orElse(null);

            return buildOtroDocumentoDto(cab, archivo, documentoElectoral, estadosDocumento, estadosDigitalOtroDocumento);

        }).toList();

        // Guardar cambios de usuario asignado en bloque
        cabOtroDocumentoRepository.saveAll(
                cabOtroDocumentoList.stream()
                        .filter(cab -> cab.getUsuarioControl() != null) // solo los que se actualizaron
                        .toList()
        );

        return documentosDto;

    }

    @Override
    public void actualizarEstadoDigitalizacion(String nombreUsuario, Long idOtroDocumento, String estadoDigitalizacion) {

        CabOtroDocumento cabOtroDocumento = this.cabOtroDocumentoRepository.findById(idOtroDocumento)
                .orElseThrow(() -> new BadRequestException(String.format("Documento con identificador %s no se encuentra registrado.", idOtroDocumento)));

        if(!cabOtroDocumento.getEstadoDigitalizacion().equals(ConstantesOtrosDocumentos.ESTADO_DIGTAL_DIGITALIZADO)){
            throw new BadRequestException(String.format("El documento %s, no se encuentra digitalizado.", cabOtroDocumento.getNumeroDocumento()));
        }

        cabOtroDocumento.setEstadoDigitalizacion(estadoDigitalizacion);
        cabOtroDocumento.setAudFechaModificacion(new Date());
        cabOtroDocumento.setAudUsuarioModificacion(nombreUsuario);

        if(estadoDigitalizacion.equals(ConstantesOtrosDocumentos.ESTADO_DIGTAL_RECHAZADO)){
            Archivo archivo = this.archivoService.findById(cabOtroDocumento.getIdArchivoOtroDocumento().longValue())
                    .orElseThrow(() -> new BadRequestException(String.format("No existe el archivo asociado al documento %s.", cabOtroDocumento.getNumeroDocumento())));

            archivo.setActivo(ConstantesComunes.INACTIVO);
            this.archivoService.save(archivo);

            cabOtroDocumento.setIdArchivoOtroDocumento(null);
            cabOtroDocumento.setIdArchivoOtroDocumentoPdf(null);
        }
        this.cabOtroDocumentoRepository.save(cabOtroDocumento);
    }

    @Override
    public ResumenOtroDocumentoDto resumenOtrosDocumentos(String numeroDocumento, String estadoDocumento, String estadoDigitalizacion) {
        ResumenOtroDocumentoDto resumenOtroDocumentoDto = new ResumenOtroDocumentoDto();
        resumenOtroDocumentoDto.setNumeroTotalDocumentos(cabOtroDocumentoRepository.countByEstadoDigitalizacionAndActivo(ConstantesOtrosDocumentos.ESTADO_DIGTAL_APROBADO,
                ConstantesComunes.ACTIVO));
        resumenOtroDocumentoDto.setNumeroDocumentosPendientesDeAsociar(cabOtroDocumentoRepository.countByEstadoDocumentoAndEstadoDigitalizacionAndActivo(
                ConstantesOtrosDocumentos.ESTADO_DOC_SIN_PROCESAR,
                ConstantesOtrosDocumentos.ESTADO_DIGTAL_APROBADO,
                ConstantesComunes.ACTIVO));
        resumenOtroDocumentoDto.setMumeroDocumentosAsociados(cabOtroDocumentoRepository.countByEstadoDocumentoAndEstadoDigitalizacionAndActivo(
                ConstantesOtrosDocumentos.ESTADO_DOC_PROCESADO,
                ConstantesOtrosDocumentos.ESTADO_DIGTAL_APROBADO,
                ConstantesComunes.ACTIVO));
        resumenOtroDocumentoDto.setNumeroDocumentosAnulados(cabOtroDocumentoRepository.countByEstadoDocumentoAndEstadoDigitalizacionAndActivo(
                ConstantesOtrosDocumentos.ESTADO_DOC_ANULADO,
                ConstantesOtrosDocumentos.ESTADO_DIGTAL_APROBADO,
                ConstantesComunes.ACTIVO));
        resumenOtroDocumentoDto.setOtroDocumentoDtoList(listarOtrosDocumentosResumen(numeroDocumento, estadoDocumento, estadoDigitalizacion));
        return resumenOtroDocumentoDto;
    }

    @Override
    @Transactional
    public void registrarAsociacion(TokenInfo tokenInfo, OtroDocumentoDto otroDocumentoDto) {

        CabOtroDocumento cabOtroDocumento = validarIdCabOtroDocumento(otroDocumentoDto);

        if(cabOtroDocumento.getEstadoDocumento().equals(ConstantesOtrosDocumentos.ESTADO_DOC_PROCESADO)){
            throw new BadRequestException(
                    String.format("No se puede continuar con la operación, el documento %s se encuentra procesado.", otroDocumentoDto.getNumeroDocumento())
            );
        }

        List<DetOtroDocumento> detOtroDocumentoList = this.detOtroDocumentoRepository.findByCabOtroDocumento(cabOtroDocumento);
        this.detOtroDocumentoRepository.deleteAll(detOtroDocumentoList);
        cabOtroDocumento.setAudFechaCreacion(otroDocumentoDto.getFechaRegistro());
        cabOtroDocumento.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
        cabOtroDocumento.setAudFechaModificacion(new Date());
        cabOtroDocumento.setEstadoDocumento(ConstantesOtrosDocumentos.ESTADO_DOC_EN_PROCESO);
        this.cabOtroDocumentoRepository.save(cabOtroDocumento);

        List<DetOtroDocumento> entidades = otroDocumentoDto.getDetOtroDocumentoDtoList()
                .stream()
                .map(dto -> mapearDetOtroDocumento(dto, otroDocumentoDto.getIdOtroDocumento(), tokenInfo))
                .toList();

        this.detOtroDocumentoRepository.saveAll(entidades);
    }

    private CabOtroDocumento validarIdCabOtroDocumento(OtroDocumentoDto otroDocumentoDto) {
        return cabOtroDocumentoRepository.findById(otroDocumentoDto.getIdOtroDocumento())
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró el registro en otros documentos con identificador: "
                                + otroDocumentoDto.getIdOtroDocumento()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public DetOtroDocumentoDto validarMesaParaAsociacion(TokenInfo tokenInfo, DetOtroDocumentoDto detOtroDocumentoDto) {

        MesaInfo mesaInfo = this.utilSceService.validarMesaConEleccionPrincipal(detOtroDocumentoDto.getNumeroMesa());
        Mesa mesa = mesaInfo.getMesa();

        this.utilSceService.validarMesaNoInstalada(mesa);

        if (ConstantesEstadoActa.ESTADOS_ACTA_NO_PROCESADOS.contains(mesaInfo.getActaPrincipal().getEstadoActa())) {
            throw new BadRequestException(
                    String.format("El acta principal de la mesa %s no se encuentra en un estado PROCESADO.", mesa.getCodigo())
            );
        }

        validarExistenciaMesaConTipoDocumento(detOtroDocumentoDto);
        validarMesaConHojaAsistenciaDigitalizada(detOtroDocumentoDto, mesa);
        validarMesaConListaElectoresDigitalizada(detOtroDocumentoDto, mesa);

        return detOtroDocumentoDto;
    }

    @Override
    @Transactional
    public void procesarAsociacion(TokenInfo tokenInfo, OtroDocumentoDto otroDocumentoDto) {

        CabOtroDocumento cabOtroDocumento = validarIdCabOtroDocumento(otroDocumentoDto);

        if (!ConstantesOtrosDocumentos.ESTADO_DOC_EN_PROCESO.equals(cabOtroDocumento.getEstadoDocumento())) {
            throw new BadRequestException(
                    String.format("No se puede continuar con la operación, el documento %s no se encuentra en proceso.",
                            otroDocumentoDto.getNumeroDocumento())
            );
        }

        for (DetOtroDocumentoDto dto : otroDocumentoDto.getDetOtroDocumentoDtoList()) {

            Mesa mesa = Optional.ofNullable(mesaRepository.findByCodigo(dto.getNumeroMesa()))
                    .orElseThrow(() -> new BadRequestException(
                            "No se encontró la mesa con código: " + dto.getNumeroMesa()
                    ));

            switch (dto.getAbrevTipoDocumento()) {
                case String abrev when ConstantesOtrosDocumentos.TIPO_DOC_HOJA_ASISTENCIA.equals(abrev)
                        && ConstantesOtrosDocumentos.TIPO_PERDIDA_TOTAL.equals(dto.getAbrevTipoPerdida()) ->
                        mesa.setEstadoDigitalizacionMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL);

                case String abrev when ConstantesOtrosDocumentos.TIPO_DOC_LISTA_ELECTORES.equals(abrev) &&
                        ConstantesOtrosDocumentos.TIPO_PERDIDA_PARCIAL.equals(dto.getAbrevTipoPerdida()) &&
                        ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE.equals(mesa.getEstadoDigitalizacionLe()) ->
                    mesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL);

                case String abrev when ConstantesOtrosDocumentos.TIPO_DOC_LISTA_ELECTORES.equals(abrev)
                        && ConstantesOtrosDocumentos.TIPO_PERDIDA_TOTAL.equals(dto.getAbrevTipoPerdida()) ->
                        mesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL);

                default -> {
                    // opcional: log o excepción si el caso no aplica
                }
            }

            mesaRepository.save(mesa);
        }

        cabOtroDocumento.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
        cabOtroDocumento.setAudFechaModificacion(new Date());
        cabOtroDocumento.setEstadoDocumento(ConstantesOtrosDocumentos.ESTADO_DOC_PROCESADO);

        cabOtroDocumentoRepository.save(cabOtroDocumento);
    }

    @Override
    public void anularDocumento(TokenInfo tokenInfo, OtroDocumentoDto otroDocumentoDto) {
        CabOtroDocumento cabOtroDocumento = validarIdCabOtroDocumento(otroDocumentoDto);

        if (!ConstantesOtrosDocumentos.ESTADO_DOC_EN_PROCESO.equals(cabOtroDocumento.getEstadoDocumento()) &&
                !ConstantesOtrosDocumentos.ESTADO_DOC_SIN_PROCESAR.equals(cabOtroDocumento.getEstadoDocumento())) {
            throw new BadRequestException(
                    String.format("No se puede continuar con la operación, el documento %s se encuentra procesado.",
                            otroDocumentoDto.getNumeroDocumento())
            );
        }

        cabOtroDocumento.setEstadoDocumento(ConstantesOtrosDocumentos.ESTADO_DOC_ANULADO);
        cabOtroDocumento.setAudFechaModificacion(new Date());
        cabOtroDocumento.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
        cabOtroDocumentoRepository.save(cabOtroDocumento);

        List<DetOtroDocumento> detOtroDocumentoList = this.detOtroDocumentoRepository.findByCabOtroDocumento(cabOtroDocumento);
        this.detOtroDocumentoRepository.deleteAll(detOtroDocumentoList);

    }


    private void validarMesaConListaElectoresDigitalizada(DetOtroDocumentoDto dto, Mesa mesa) {
        if(!dto.getAbrevTipoDocumento().equals(ConstantesOtrosDocumentos.TIPO_DOC_LISTA_ELECTORES)) return;

        if(dto.getAbrevTipoPerdida().equals(ConstantesOtrosDocumentos.TIPO_PERDIDA_TOTAL) &&
                mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE)){
            throw new BadRequestException(
                        String.format("La lista de electores de la mesa %s, ya se encuentra digitalizada parcialmente, no puede declarse como %s.", mesa.getCodigo(), dto.getDescTipoPerdida())
                );
        }

        if(dto.getAbrevTipoPerdida().equals(ConstantesOtrosDocumentos.TIPO_PERDIDA_TOTAL) &&
                !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE)){
            throw new BadRequestException(
                    String.format("La lista de electores de la mesa %s, ya se encuentra digitalizada, no puede declarse como %s.", mesa.getCodigo(), dto.getDescTipoPerdida())
            );
        }

        if(dto.getAbrevTipoPerdida().equals(ConstantesOtrosDocumentos.TIPO_PERDIDA_PARCIAL) &&
                !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE) &&
                !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE)){
            throw new BadRequestException(
                    String.format("La lista de electores de la mesa %s, ya se encuentra digitalizada, no puede declarse como %s.", mesa.getCodigo(), dto.getDescTipoPerdida())
            );
        }

    }

    private void validarMesaConHojaAsistenciaDigitalizada(DetOtroDocumentoDto detOtroDocumentoDto, Mesa mesa) {

        if(!detOtroDocumentoDto.getAbrevTipoDocumento().equals(ConstantesOtrosDocumentos.TIPO_DOC_HOJA_ASISTENCIA)) return;

        if(!mesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE) &&
                !mesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA)) {
            throw new BadRequestException(
                    String.format("La hoja de asistencia de la mesa %s, ya se encuentra digitalizada.", mesa.getCodigo())
            );
        }
    }


    private void validarExistenciaMesaConTipoDocumento(DetOtroDocumentoDto dto) {
        var documentos = detOtroDocumentoRepository.findByNroMesaAndTipoDocumento(
                dto.getNumeroMesa(), dto.getAbrevTipoDocumento());

        if (!documentos.isEmpty()) {
            var documento = documentos.getFirst();
            var denuncia = documento.getCabOtroDocumento().getNumeroDocumento();

            var tipoPerdida = valorPreferido(dto.getDescTipoPerdida(), dto.getAbrevTipoPerdida());
            var tipoDocumento = valorPreferido(dto.getDescTipoDocumento(), dto.getAbrevTipoDocumento());

            throw new BadRequestException(String.format(
                    "La mesa %s, con tipo de documento %s y pérdida %s ya se encuentra asociada a la denuncia %s.",
                    dto.getNumeroMesa(), tipoDocumento, tipoPerdida, denuncia
            ));
        }
    }

    private String valorPreferido(String descripcion, String abreviatura) {
        return (descripcion == null || descripcion.isBlank()) ? abreviatura : descripcion;
    }



    private DetOtroDocumento mapearDetOtroDocumento(
            DetOtroDocumentoDto dto,
            Long idOtroDocumento,
            TokenInfo tokenInfo
    ) {
        Mesa mesa = Optional.ofNullable(mesaRepository.findByCodigo(dto.getNumeroMesa()))
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró la mesa con código: " + dto.getNumeroMesa()
                ));

        return DetOtroDocumento.builder()
                .cabOtroDocumento(new CabOtroDocumento(idOtroDocumento))
                .mesa(mesa)
                .codTipoDocumento(dto.getAbrevTipoDocumento())
                .codTipoPerdida(dto.getAbrevTipoPerdida())
                .audFechaCreacion(new Date())
                .audUsuarioCreacion(tokenInfo.getNombreUsuario())
                .activo(ConstantesComunes.ACTIVO)
                .build();
    }


    private List<OtroDocumentoDto> listarOtrosDocumentosResumen(String numeroDocumento, String estadoDocumento, String estadoDigitalizacion) {

        List<DetCatalogoEstructuraDTO> tipoDocumentos = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.CATALOGO_MAE_TIPO_DOCUMENTO_OTRO_DOC,
                ConstantesCatalogo.CATALOGO_DET_TIPO_DOCUMENTO_OTRO_DOC
        );

        List<DetCatalogoEstructuraDTO> tipoPerdida = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.CATALOGO_MAE_TIPO_PERDIDA_OTRO_DOC,
                ConstantesCatalogo.CATALOGO_DET_TIPO_PERDIDA_OTRO_DOC
        );

        List<DetCatalogoEstructuraDTO> estadosDocumento = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.MAE_ESTADOS_OTRO_DOCUMENTO,
                ConstantesCatalogo.DET_ESTADOS_OTRO_DOCUMENTO
        );

        List<DetCatalogoEstructuraDTO> estadosDocumentoDigtal = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_OTRO_DOCUMENTO,
                ConstantesCatalogo.DET_ESTADOS_DIGITALIZACION_OTRO_DOCUMENTO
        );

        if (estadoDocumento != null && estadoDocumento.trim().isEmpty()) {
            estadoDocumento = null;
        }
        List<CabOtroDocumento> resumenOtroDocumentosDto = this.cabOtroDocumentoRepository.buscar(numeroDocumento, estadoDocumento, estadoDigitalizacion, ConstantesComunes.ACTIVO);
        return resumenOtroDocumentosDto.stream()
                .map(cab -> mapToResolucionAsociadosRequest(cab, tipoDocumentos, tipoPerdida, estadosDocumento, estadosDocumentoDigtal))
                .toList();
    }

    private OtroDocumentoDto mapToResolucionAsociadosRequest(
            CabOtroDocumento cab,
            List<DetCatalogoEstructuraDTO> tipoDocumentos,
            List<DetCatalogoEstructuraDTO> tipoPerdida,
            List<DetCatalogoEstructuraDTO> estadosOtroDocumento,
            List<DetCatalogoEstructuraDTO> estadosOtroDocumentoDigtal) {

        Archivo archivo = Optional.ofNullable(cab.getIdArchivoOtroDocumento())
                .map(id -> archivoService.getArchivoById(id.longValue()))
                .orElse(null);

        DocumentoElectoral documentoElectoral = Optional.ofNullable(cab.getCodTipoDocumento())
                .map(documentoElectoralRepository::findByAbreviatura)
                .orElse(null);

        OtroDocumentoDto documentoDto = buildOtroDocumentoDto(cab, archivo, documentoElectoral, estadosOtroDocumento,estadosOtroDocumentoDigtal);

        List<DetOtroDocumento> detActaResolucionList = this.detOtroDocumentoRepository.findByCabOtroDocumento(cab);
        documentoDto.setDetOtroDocumentoDtoList(detActaResolucionList.stream()
                .map(det -> mapToDetOtroDocumento(det, tipoDocumentos, tipoPerdida))
                .toList());

        return documentoDto;
    }

    private DetOtroDocumentoDto mapToDetOtroDocumento(
            DetOtroDocumento det,
            List<DetCatalogoEstructuraDTO> tipoDocumentos,
            List<DetCatalogoEstructuraDTO> tipoPerdida) {

        String descTipoDocumento = tipoDocumentos.stream()
                .filter(td -> td.getCodigoS().equals(det.getCodTipoDocumento()))
                .map(DetCatalogoEstructuraDTO::getNombre)
                .findFirst()
                .orElse(null);

        String descTipoPerdida = tipoPerdida.stream()
                .filter(tp -> tp.getCodigoS().equals(det.getCodTipoPerdida()))
                .map(DetCatalogoEstructuraDTO::getNombre)
                .findFirst()
                .orElse(null);

        return DetOtroDocumentoDto.builder()
                .idDetOtroDocumento(det.getId())
                .abrevTipoDocumento(det.getCodTipoDocumento())
                .descTipoDocumento(descTipoDocumento)
                .abrevTipoPerdida(det.getCodTipoPerdida())
                .descTipoPerdida(descTipoPerdida)
                .activo(det.getActivo())
                .idMesa(Optional.ofNullable(det.getMesa())
                        .map(m -> m.getId().intValue())
                        .orElse(null))
                .numeroMesa(Optional.ofNullable(det.getMesa())
                        .map(Mesa::getCodigo)
                        .orElse(null))
                .build();
    }

    private OtroDocumentoDto buildOtroDocumentoDto(CabOtroDocumento cab, Archivo archivo, DocumentoElectoral documentoElectoral,
                                                   List<DetCatalogoEstructuraDTO> estadosOtroDocumento, List<DetCatalogoEstructuraDTO> estadosDigitalizacion) {

        String descEstadoOtroDocumento = estadosOtroDocumento.stream()
                .filter(tp -> tp.getCodigoS().equals(cab.getEstadoDocumento()))
                .map(DetCatalogoEstructuraDTO::getNombre)
                .findFirst()
                .orElse(ConstantesComunes.VACIO);

        String descEstadoDigtalOtroDocumento = estadosDigitalizacion.stream()
                .filter(tp -> tp.getCodigoS().equals(cab.getEstadoDocumento()))
                .map(DetCatalogoEstructuraDTO::getNombre)
                .findFirst()
                .orElse(ConstantesComunes.VACIO);

        return OtroDocumentoDto.builder()
                .idOtroDocumento(cab.getId())
                .numeroDocumento(cab.getNumeroDocumento())
                .codigoCentroComputo(cab.getCentroComputo().getCodigo())
                .numeroPaginas(cab.getNumeroPaginas())
                .fechaRegistro(cab.getAudFechaModificacion()==null?cab.getAudFechaCreacion():cab.getAudFechaModificacion())
                .fechaSceScanner(cab.getAudFechaModificacion()==null?cab.getAudFechaCreacion():cab.getAudFechaModificacion())
                .nombreTipoDocumento(Optional.ofNullable(documentoElectoral)
                        .map(DocumentoElectoral::getNombre)
                        .orElse(ConstantesComunes.VACIO))
                .abrevTipoDocumento(Optional.ofNullable(documentoElectoral)
                        .map(DocumentoElectoral::getAbreviatura)
                        .orElse(ConstantesComunes.VACIO))
                .idArchivo(cab.getIdArchivoOtroDocumento())
                .nombreArchivo(Optional.ofNullable(archivo)
                        .map(Archivo::getNombre)
                        .orElse(ConstantesComunes.VACIO))
                .estadoDigitalizacion(cab.getEstadoDigitalizacion())
                .descEstadoDigitalizacion(descEstadoDigtalOtroDocumento)
                .estadoDocumento(cab.getEstadoDocumento())
                .descEstadoDocumento(descEstadoOtroDocumento)
                .listaPaginas(SceUtils.getPaginadoResolucion(cab.getNumeroPaginas() == null ? 0 : cab.getNumeroPaginas()))
                .activo(cab.getActivo())
                .build();
    }

    @Override
    public void save(CabOtroDocumento cabOtroDocumento) {
        this.cabOtroDocumentoRepository.save(cabOtroDocumento);
    }

    @Override
    public void saveAll(List<CabOtroDocumento> k) {
        this.cabOtroDocumentoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.cabOtroDocumentoRepository.deleteAll();
    }

    @Override
    public List<CabOtroDocumento> findAll() {
        return this.cabOtroDocumentoRepository.findAll();
    }
}
