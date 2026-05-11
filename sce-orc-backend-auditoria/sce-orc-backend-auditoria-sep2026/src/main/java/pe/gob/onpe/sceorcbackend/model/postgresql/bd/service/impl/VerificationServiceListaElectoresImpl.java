package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.gob.onpe.sceorcbackend.exception.BusinessValidationException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores.VerificationLe;
import pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores.VerificationLePaginaItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores.VerificationLeSectionItem;
import pe.gob.onpe.sceorcbackend.model.dto.verification.ProcessingResult;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json.DetLeRectanguloPaginaData;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json.DetLeRectanguloSeccionData;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class VerificationServiceListaElectoresImpl implements VerificationServiceListaElectores {

    private static final Logger logger = LoggerFactory.getLogger(VerificationServiceListaElectoresImpl.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final MesaService tabMesaService;
    private final PadronElectoralService maePadronService;
    private final DetLeRectanguloService detLeRectanguloService;
    private final OmisoVotanteService tabOmisoVotanteService;
    private final MesaDocumentoService tabMesaDocumentoService;
    private final DocumentoElectoralService admTabDocumentoElectoralService;
    private final ITabLogService logService;

    public VerificationServiceListaElectoresImpl(
            MesaService tabMesaService,
            PadronElectoralService maePadronService,
            OmisoVotanteService tabOmisoVotanteService,
            MesaDocumentoService tabMesaDocumentoService,
            DetLeRectanguloService detLeRectanguloService,
            DocumentoElectoralService admTabDocumentoElectoralService,
            ITabLogService logService

    ) {
        this.tabMesaService = tabMesaService;
        this.maePadronService = maePadronService;
        this.detLeRectanguloService = detLeRectanguloService;
        this.tabOmisoVotanteService = tabOmisoVotanteService;
        this.tabMesaDocumentoService = tabMesaDocumentoService;
        this.admTabDocumentoElectoralService = admTabDocumentoElectoralService;
        this.logService = logService;
    }


    Long getMesaIdRandomLe(String usuario, boolean reprocesar) {

        Long mesaId=null;
        List<String> estadosAFiltrar = definirEstado(reprocesar);
        //primero consultar las asignadas por usuario
        List<Long> tabMesaListAsignados = this.tabMesaService.findMesasAsignadasConFiltro(usuario, estadosAFiltrar, ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE,
                ConstantesEstadoMesa.NO_INSTALADA);

        if (!tabMesaListAsignados.isEmpty()) {
            mesaId = tabMesaListAsignados.getFirst();
        } else {
            //consultar las libres
            List<Long> tabMesaListLibres = this.tabMesaService.findMesaIdsLeRandom(
                    estadosAFiltrar,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE,
                    ConstantesEstadoMesa.NO_INSTALADA,
                    List.of(ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_CON_PERDIDA_PARCIAL, ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA),
                    List.of(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION, ConstantesEstadoActa.ESTADO_ACTA_DIGITADA,
                            ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA, ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE)
            );

            if (!tabMesaListLibres.isEmpty()) {
                int indiceAleatorio = secureRandom.nextInt(tabMesaListLibres.size());
                mesaId = tabMesaListLibres.get(indiceAleatorio);
            }
        }

        return mesaId;
    }

    private Mesa getMesa(Long mesaId) {
        return this.tabMesaService.findById(mesaId).orElse(null);
    }

    private List<String> definirEstado(boolean reprocesar) {
        //en cualquier caso se agrega las no instaladas
        if (reprocesar) {
            return List.of(ConstantesEstadoMesa.REPROCESAR);
        }

        //si no esta marcado reproocesar busca todos los demás estados
        //para ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE debe concaternarse el estado_mesa no instalada
        return List.of(ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_CON_PERDIDA_PARCIAL,
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE);

    }

    private List<List<PadronElectoral>> getListasDivididas(List<PadronElectoral> maePadronList) {
        int size = maePadronList.size();
        int sublistSize = 10;//cantidad de electores por página

        return IntStream.range(0, (size + sublistSize - 1) / sublistSize)
                .mapToObj(i -> maePadronList.subList(i * sublistSize, Math.min(size, (i + 1) * sublistSize)))
                .toList();
    }

    @Override
    @Transactional
    public GenericResponse<VerificationLe> getRandomListaElectores(TokenInfo tokenInfo, boolean reprocesar) {

        try{
            boolean isExistPadron = this.maePadronService.existsByActivo(ConstantesComunes.ACTIVO);
            if(!isExistPadron)  throw new BusinessValidationException("Para continuar se debe antes realizar la carga de padrón electoral.");

            Long mesaId = getMesaIdRandomLe(tokenInfo.getNombreUsuario(), reprocesar);

            if(mesaId == null) return new GenericResponse<>(false, "No existen Listas de Electores para verificar.", null);

            Mesa mesa = getMesa(mesaId);
            if (mesa == null) return new GenericResponse<>(false, String.format(ConstantesComunes.MENSAJE_FORMAT_MESA_NO_EXISTE, mesaId), null);

            List<PadronElectoral> maePadronList = this.maePadronService.findPadronElectoralByCodigoMesaOrderByOrden(mesa.getCodigo());

            List<VerificationLePaginaItem> verificationLePaginaItems = new ArrayList<>();

            Integer cantidadAusentes = 0;
            List<List<PadronElectoral>> listasDivididas = getListasDivididas(maePadronList);

            if(! mesa.getEstadoMesa().equals(ConstantesEstadoMesa.NO_INSTALADA) &&
                    !mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL)) {
                //validaciones del acta principal
                cantidadAusentes = this.tabMesaService.validarActaPrincipalProcesada(mesa);

                List<DetLeRectangulo> detLeRectangulos = this.detLeRectanguloService.findByMesaId(mesaId);
                if (detLeRectangulos.isEmpty())
                    return new GenericResponse<>(false, String.format("EL modelo no ha generado las secciones para la mesa %s ", mesaId), null);

                DetLeRectangulo detLeRectangulo = detLeRectangulos.getFirst();
                verificationLePaginaItems = getVerificactionLePaginaItems( detLeRectangulo, listasDivididas, mesa.getEstadoDigitalizacionLe());

                if(reprocesar){
                    List<OmisoVotante> listaOmisosVotantes = this.tabOmisoVotanteService.buscarPorIdMesaActivo(mesaId);
                    List<MesaDocumento> listaDetalleMesaDocumento = this.tabMesaDocumentoService.buscarIdMesaAndIdDocumentoElectoral(mesaId, ConstantesComunes.ID_DOCUMENT_LISTA_ELECTORES);
                    if(!listaOmisosVotantes.isEmpty()){
                        verificationLePaginaItems.forEach(item -> {
                            Integer pagina = item.getPagina();
                            MesaDocumento mesaDocumento = listaDetalleMesaDocumento.stream().filter(documento -> pagina.equals(documento.getPagina()))
                                    .findFirst().orElse(null);
                            item.setTextoObservacionesUser(Objects.nonNull(mesaDocumento) ? mesaDocumento.getDescripcionObservacion() : null);
                            item.getSecciones().forEach(itemSeccion -> {
                                OmisoVotante omisoVotante = listaOmisosVotantes.stream().filter(omiso -> itemSeccion.getIdPadron().equals(omiso.getPadronElectoral().getId()))
                                        .findFirst().orElse(null);
                                itemSeccion.setAsistioUser(Objects.nonNull(omisoVotante) ? "2" : null);
                                itemSeccion.setAsistioAutomatico(Objects.isNull(omisoVotante) ? "1" : "2");
                            });

                        });
                    }
                }

            } else if(mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL) ){
                cantidadAusentes = mesa.getCantidadElectoresHabiles();
                verificationLePaginaItems = getVerificactionLePaginaItems( null, listasDivididas, mesa.getEstadoDigitalizacionLe());
            }else {
                cantidadAusentes = mesa.getCantidadElectoresHabiles();
                //construir la pagina 1  flujo para no instaladas
                List<VerificationLeSectionItem> verificationLeSectionItems = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    verificationLeSectionItems.add(VerificationLeSectionItem.builder()
                        .apellidoMaterno("")
                        .apellidoPaterno("")
                        .dni("NO DISPONIBLE")
                        .huella(false)
                        .firma(false)
                        .nombres("NO DISPONIBLE")
                        .noVoto(false)
                        .orden(i)
                        .archivoSeccion(-1L)
                        .asistioAutomatico("2")
                        .asistioUser("0")
                        .idPadron(-1L)
                        .build());
                }

                VerificationLePaginaItem lePaginaItemNoIstall = VerificationLePaginaItem.builder()
                    .pagina(1)
                    .archivoObservacion(-1L)
                    .secciones(verificationLeSectionItems)
                    .textoObservacionesUser("-")
                    .build();

                verificationLePaginaItems.add(lePaginaItemNoIstall);

            }

            Ubigeo ubigeo = mesa.getLocalVotacion().getUbigeo();

            mesa.setUsuarioAsignadoLe(tokenInfo.getNombreUsuario());
            mesa.setFechaAsignadoLe(new Date());
            //
            this.tabMesaService.save(mesa);

            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    String.format("Se obtuvo la información para registro de la lista de electores de la mesa %s, al usuario %s.", mesa.getCodigo(), tokenInfo.getNombreUsuario()),
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

            List<OmisoVotante> omisoVotanteList = null;

            return new GenericResponse<>(true,
                String.format("Se obtuvo la información para el registro de la lista de electores de la mesa %s.", mesa.getCodigo()),
                VerificationLe
                    .builder()
                    .mesaId(mesaId)
                    .type(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)
                    .mesa(mesa.getCodigo())
                    .estadoMesa(mesa.getEstadoMesa())
                    .electoresAusentes(cantidadAusentes)
                    .electoresHabiles(mesa.getCantidadElectoresHabiles())
                    .localVotacion(mesa.getLocalVotacion().getNombre())
                    .ubigeo(ubigeo.getCodigo())
                    .departamento(ubigeo.getDepartamento())
                    .provincia(ubigeo.getProvincia())
                    .distrito(ubigeo.getNombre())
                    .paginas(verificationLePaginaItems)
                    .data(omisoVotanteList)
                    .estadoLe(mesa.getEstadoDigitalizacionLe())
                    .build());

        } catch (Exception ex){
            logger.error("Error:", ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new GenericResponse<>(false, "No fue posible obtener la lista de electores omisos.");
        }
    }


    Optional<DetLeRectanguloPaginaData> getOptionalDetLeRectanguloPaginaData(DetLeRectangulo detLeRectangulo , int pagina){
        List<DetLeRectanguloPaginaData> detLeRectanguloPaginaDataList = detLeRectangulo.getPaginas();

        Integer finalPagina = pagina;
        return detLeRectanguloPaginaDataList.stream()
                .filter(detLeRectanguloPaginaData1 -> Objects.equals(detLeRectanguloPaginaData1.getPagina(), finalPagina))
                .findFirst();
    }


    VerificationLeSectionItem buildVerificationLeSectionItem(PadronElectoral maePadron, DetLeRectanguloSeccionData detLeRectanguloSeccionData, String asistioAutomatico){
        return VerificationLeSectionItem.builder()
                .idPadron(maePadron.getId())
                .nombres(maePadron.getNombres())
                .apellidoMaterno(maePadron.getApellidoMaterno())
                .apellidoPaterno(maePadron.getApellidoPaterno())
                .dni(maePadron.getDocumentoIdentidad())
                .archivoSeccion(detLeRectanguloSeccionData.getArchivoSeccion())
                .orden(detLeRectanguloSeccionData.getOrden())
                .firma(detLeRectanguloSeccionData.getFirma())
                .huella(detLeRectanguloSeccionData.getHuella())
                .noVoto(detLeRectanguloSeccionData.getNoVoto())
                .asistioAutomatico(asistioAutomatico)
                .asistioUser(null)
                .build();
    }

    VerificationLePaginaItem buildVerificationLePaginaItem(Optional<DetLeRectanguloPaginaData> optionalDetLeRectanguloPaginaData,
                                                           List<VerificationLeSectionItem> verificationLeSectionItems,
                                                           int pagina, String tipoDenuncia){
        if (Objects.nonNull(optionalDetLeRectanguloPaginaData) && optionalDetLeRectanguloPaginaData.isPresent()
                &&  Objects.nonNull(optionalDetLeRectanguloPaginaData.get().getArchivoPagina())) {
            return VerificationLePaginaItem.builder()
                    .pagina(pagina)
                    .textoObservacionesUser(null)
                    .archivoObservacion(optionalDetLeRectanguloPaginaData.get().getArchivoObservacion())
                    .existeObservacion(Boolean.TRUE.equals(optionalDetLeRectanguloPaginaData.get().getExisteObservacion()))
                    .secciones(verificationLeSectionItems)
                    .build();
        } else {
            return VerificationLePaginaItem.builder()
                    .pagina(pagina)
                    .archivoObservacion(StringUtils.isBlank(tipoDenuncia) ? null : -1L)
                    .textoObservacionesUser(null)
                    .existeObservacion(false)
                    .secciones(verificationLeSectionItems)
                    .tipoDenuncia(tipoDenuncia)
                    .build();
        }
    }

    void processSectionOmisosLe(PadronElectoral maePadron, List<VerificationLeSectionItem> verificationLeSectionItems, Optional<DetLeRectanguloPaginaData> optionalDetLeRectanguloPaginaData, String tipoDenuncia){
        List<DetLeRectanguloSeccionData> detLeRectanguloSeccionDataList;
        if (Objects.nonNull(optionalDetLeRectanguloPaginaData) && optionalDetLeRectanguloPaginaData.isPresent() &&  Objects.nonNull(optionalDetLeRectanguloPaginaData.get().getArchivoPagina())) {
            detLeRectanguloSeccionDataList = optionalDetLeRectanguloPaginaData.get().getSecciones();
            if (detLeRectanguloSeccionDataList == null) {
                verificationLeSectionItems.add(getVerificationLeSectionItemNulos(maePadron,  tipoDenuncia));
            } else {
                Optional<DetLeRectanguloSeccionData> optionalDetLeRectanguloSeccionData = detLeRectanguloSeccionDataList.stream()
                        .filter(detLeRectanguloSeccionData -> Objects.equals(detLeRectanguloSeccionData.getOrden(), maePadron.getOrden()))
                        .findFirst();
                if (optionalDetLeRectanguloSeccionData.isPresent()) {
                    DetLeRectanguloSeccionData detLeRectanguloSeccionData = optionalDetLeRectanguloSeccionData.get();
                    verificationLeSectionItems.add(buildVerificationLeSectionItem(maePadron, detLeRectanguloSeccionData, getAsistioAutomatico(detLeRectanguloSeccionData)));
                } else {
                    verificationLeSectionItems.add(getVerificationLeSectionItemNulos(maePadron, tipoDenuncia));
                }
            }
        } else {
            verificationLeSectionItems.add(getVerificationLeSectionItemNulos(maePadron, tipoDenuncia));
        }
    }

    private List<VerificationLePaginaItem> getVerificactionLePaginaItems(DetLeRectangulo detLeRectangulo, List<List<PadronElectoral>> listasDivididas, String tipoDenuncia) {
        int pagina = 0;
        List<VerificationLePaginaItem> verificationLePaginaItems = new ArrayList<>();
        for (List<PadronElectoral> maePadronList1 : listasDivididas) {
            pagina = pagina + 1;
            List<VerificationLeSectionItem> verificationLeSectionItems = new ArrayList<>();
            Optional<DetLeRectanguloPaginaData> optionalDetLeRectanguloPaginaData =ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL.equals(tipoDenuncia)
            ? null : getOptionalDetLeRectanguloPaginaData(detLeRectangulo, pagina);
            for (PadronElectoral maePadron : maePadronList1) {
                processSectionOmisosLe(maePadron, verificationLeSectionItems, optionalDetLeRectanguloPaginaData, tipoDenuncia);
            }
            verificationLePaginaItems.add(buildVerificationLePaginaItem(optionalDetLeRectanguloPaginaData, verificationLeSectionItems, pagina, tipoDenuncia));
        }
        return verificationLePaginaItems;
    }


    private static String getAsistioAutomatico(DetLeRectanguloSeccionData data) {
        if (data.getFirma() == null || data.getHuella() == null) {
            return "2"; // rojo
        }
        return (data.getFirma() || data.getHuella()) ? "1" : "2";
    }

    private Mesa getTabMesa(Long mesaId) {
        if (mesaId == null) {
            return null;
        }
        return tabMesaService.findById(mesaId).orElse(null);
    }



    private ProcessingResult processPagesOmisosLe(List<VerificationLePaginaItem> paginas, Mesa tabMesa, TokenInfo tokenInfo) {
        List<OmisoVotante> tabOmisoVotanteList = new ArrayList<>();
        for (VerificationLePaginaItem vpi : paginas) {
            ProcessingResult pageResult = processPageOmisosLe(vpi, tabMesa, tokenInfo);
            if (pageResult.isObservaciones()) {
                return pageResult;
            }
            tabOmisoVotanteList.addAll(pageResult.getTabOmisoVotanteList());
        }
        return new ProcessingResult(false, "", tabOmisoVotanteList);
    }

    private ProcessingResult processPageOmisosLe(VerificationLePaginaItem vpi, Mesa tabMesa, TokenInfo tokenInfo) {

        List<OmisoVotante> tabOmisoVotanteList = new ArrayList<>();

        //actualizar las observaciones
        if(StringUtils.isBlank(vpi.getTipoDenuncia())){
            guardarMesaDocumentoArchivoLe(tabMesa.getId(), vpi.getPagina(), vpi.getTextoObservacionesUser(), tokenInfo.getNombreUsuario());
        }

        for (VerificationLeSectionItem vlsi : vpi.getSecciones()) {

            ProcessingResult sectionResult = processSectionOmisosLe(vlsi, vpi.getPagina(), tabMesa, tokenInfo);

            if (sectionResult.isObservaciones()) {
                return sectionResult;
            }

            if(!sectionResult.getTabOmisoVotanteList().isEmpty()){
                tabOmisoVotanteList.addAll(sectionResult.getTabOmisoVotanteList());
            }
        }

        return new ProcessingResult(false, "", tabOmisoVotanteList);
    }


    private void guardarMesaDocumentoArchivoLe(Long idMesa, Integer pagina, String observacion, String usuario) {

        logger.info("Actulizando observaciones documento LE para idMesa={}, página={}, usuario={}", idMesa, pagina, usuario);

        DocumentoElectoral documentoElectoral = this.admTabDocumentoElectoralService
            .findByAbreviatura(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);

        if (documentoElectoral == null) {
            throw new InternalServerErrorException(String.format(
                "No existe registrado el documento electoral %s.",
                ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES));
        }

        if (pagina == null || pagina < 0) {
            throw new InternalServerErrorException("El número de página no puede ser nulo o menor que cero.");
        }

        if (usuario == null || usuario.isBlank()) {
            throw new InternalServerErrorException("El usuario no puede ser nulo o vacío.");
        }

        if (observacion == null) {
            observacion = ""; // Podés lanzar excepción si debe ser obligatorio
        }

        MesaDocumento mesaDocumento = this.tabMesaDocumentoService
            .findByMesaAndAdmDocumentoElectoralAndTipoArchivoAndPagina(
                idMesa,
                documentoElectoral.getId(),
                ConstantesFormatos.IMAGE_TIF_VALUE,
                pagina
            )
            .orElseThrow(() -> new InternalServerErrorException(String.format(
                "No existe un documento archivo de LE con el número de página %d.", pagina)));

        mesaDocumento.setUsuarioModificacion(usuario);
        mesaDocumento.setFechaModificacion(new Date());
        mesaDocumento.setDescripcionObservacion(observacion);

        this.tabMesaDocumentoService.save(mesaDocumento);

        logger.info("Documento LE actualizado correctamente para idMesa={}, página={}", idMesa, pagina);
    }




    private ProcessingResult processSectionOmisosLe(VerificationLeSectionItem vlsi, Integer pagina, Mesa tabMesa, TokenInfo tokenInfo) {
        String asistio = vlsi.getAsistioUser() != null ? vlsi.getAsistioUser() : vlsi.getAsistioAutomatico();
        if (asistio == null) {
            return new ProcessingResult(true, String.format("En la pagina %d, no se encuentra marcada la sección de orden %d", pagina, vlsi.getOrden()), new ArrayList<>());
        }
        if(asistio.equals("2")) {
            return  createOmisoVotante(vlsi, tabMesa, tokenInfo);
        }else if(asistio.equals("0") || asistio.equals("3") ) {
            return new ProcessingResult(true, String.format("En la pagina %d, no se encuentra marcada la sección de orden %d", pagina, vlsi.getOrden()), new ArrayList<>());
        }else{
            return new ProcessingResult(false, "", new ArrayList<>());
        }
    }

    private ProcessingResult createOmisoVotante(VerificationLeSectionItem vlsi, Mesa tabMesa, TokenInfo tokenInfo) {
        return maePadronService.findById(vlsi.getIdPadron())
                .map(maePadron -> {
                    OmisoVotante tabOmisoVotante = OmisoVotante.builder()
                            .padronElectoral(maePadron)
                            .mesa(tabMesa)
                            .activo(ConstantesComunes.ACTIVO)
                            .usuarioCreacion(tokenInfo.getNombreUsuario())
                            .fechaCreacion(new Date())
                            .build();
                    return new ProcessingResult(false, "", Collections.singletonList(tabOmisoVotante));
                })
                .orElse(new ProcessingResult(true, "Padron "+vlsi.getIdPadron() + " no encontrado.", new ArrayList<>()));
    }

    private void saveProcessedData(List<OmisoVotante> tabOmisoVotanteList, Mesa tabMesa) {
        tabOmisoVotanteService.saveAll(tabOmisoVotanteList);
        tabMesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PROCESADO);
        tabMesaService.save(tabMesa);
    }


    @Override
    @Transactional
    public GenericResponse<Boolean> saveListaElectores(VerificationLe request, TokenInfo tokenInfo, boolean reprocesar) {

        try {
            Mesa tabMesa = getTabMesa(request.getMesaId());
            if (tabMesa == null)
                return new GenericResponse<>(false, String.format(ConstantesComunes.MENSAJE_FORMAT_MESA_NO_EXISTE, request.getMesaId()), false);

            if (request.getPaginas() == null || request.getPaginas().isEmpty())
                return new GenericResponse<>(false, "No se encontraron datos para guardar", false);

            if (tabMesa.getEstadoMesa().equals(ConstantesEstadoMesa.NO_INSTALADA) ||
                    tabMesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL)) {
                List<PadronElectoral> padronElectorals = this.maePadronService.findPadronElectoralByCodigoMesaOrderByOrden(tabMesa.getCodigo());

                if (padronElectorals.isEmpty())
                    return new GenericResponse<>(false, "La mesa " + tabMesa.getCodigo() + ", no cuenta con su padrón registrado.", true);

                Date now = new Date();
                List<OmisoVotante> omisoVotanteList = padronElectorals.stream()
                    .map(padronElectoral -> OmisoVotante.builder()
                        .padronElectoral(padronElectoral)
                        .mesa(tabMesa)
                        .activo(ConstantesComunes.ACTIVO)
                        .usuarioCreacion(tokenInfo.getNombreUsuario())
                        .fechaCreacion(now)
                        .build())
                    .toList();

                deshabilitarOmisosVotantes(request, tokenInfo, reprocesar);

                saveProcessedData(tabMesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL) ? new ArrayList<>() : omisoVotanteList, tabMesa);

                this.logService.registrarLog( tokenInfo.getNombreUsuario(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(), "Se realizó el registro de la lista de electores " + tabMesa.getCodigo() + ".",
                        tokenInfo.getCodigoCentroComputo(),
                        ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

                return new GenericResponse<>(true, "Se registró correctamente los omisos de la mesa " + tabMesa.getCodigo() + ".", true);
            } else {
                ProcessingResult result = processPagesOmisosLe(request.getPaginas(), tabMesa, tokenInfo);

                if (result.isObservaciones()) {
                    return new GenericResponse<>(false, result.getMensajeObservacion(), false);
                } else {
                    deshabilitarOmisosVotantes(request, tokenInfo, reprocesar);
                    this.logService.registrarLog( tokenInfo.getNombreUsuario(),Thread.currentThread().getStackTrace()[1].getMethodName(), "Se realizó el registro de la lista de electores " + tabMesa.getCodigo() + ".",  tokenInfo.getCodigoCentroComputo(),ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
                    saveProcessedData(result.getTabOmisoVotanteList(), tabMesa);
                    return new GenericResponse<>(true, "Se registró correctamente los omisos de la mesa " + tabMesa.getCodigo() + ".", true);
                }
            }

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }

    }

    private void deshabilitarOmisosVotantes(VerificationLe request, TokenInfo tokenInfo, boolean reprocesar) {
        if(reprocesar){
            int deshabilitados = this.tabOmisoVotanteService.inhabilitarOmisosVotantes(request.getMesaId(), tokenInfo.getNombreUsuario());
            logger.info("Se inactivaron {} omisos votantes", deshabilitados);
        }
    }

    @Override
    @Transactional
    public GenericResponse<Boolean> rechazarListaElectores(Long mesaId, TokenInfo tokenInfo) {

        Mesa tabMesa;
        if (mesaId == null)
            return new GenericResponse<>(false, "El parametro mesaId no puede ser nula", false);

        Optional<Mesa> optionalTabMesa = this.tabMesaService.findById(mesaId);
        if (optionalTabMesa.isEmpty())
            return new GenericResponse<>(false, String.format("La mesa %s no existe ", mesaId), false);

        tabMesa = optionalTabMesa.get();
        tabMesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA);
        tabMesa.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        tabMesa.setUsuarioAsignadoLe(null);
        tabMesa.setFechaAsignadoLe(null);
        tabMesa.setFechaModificacion(new Date());
        this.tabMesaService.save(tabMesa);


        DocumentoElectoral admTabDocumentoElectoral = admTabDocumentoElectoralService.findByAbreviatura(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);
        if (admTabDocumentoElectoral == null)
            return new GenericResponse<>(false, "El documento electoral " + ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES +
                    " no se encuentra registrado en los documentos electorales de la base de datos.", null);

        this.tabMesaDocumentoService.deleteByAdmDocumentoElectoralIdAndMesaId(admTabDocumentoElectoral.getId(), tabMesa.getId());

        this.detLeRectanguloService.deleteByMesaIdAndType(mesaId, ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);

        String mensaje = String.format("La lista de electores de la mesa %s, fue rechazada, debe volver a digitalizarse.", tabMesa.getCodigo());


        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                mensaje,
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                1);

        return new GenericResponse<>(true,mensaje, true);
    }


    private static VerificationLeSectionItem getVerificationLeSectionItemNulos(PadronElectoral maePadron, String tipoDenuncia) {
        return VerificationLeSectionItem.builder()
                .idPadron(maePadron.getId())
                .nombres(maePadron.getNombres())
                .apellidoMaterno(maePadron.getApellidoMaterno())
                .apellidoPaterno(maePadron.getApellidoPaterno())
                .dni(maePadron.getDocumentoIdentidad())
                .archivoSeccion( StringUtils.isBlank(tipoDenuncia)?  null : -1L)
                .orden(maePadron.getOrden())
                .noVoto(null)
                .asistioAutomatico(StringUtils.isBlank(tipoDenuncia)? "0": "1")
                .asistioUser(null)
                .firma(null)
                .huella(null)
                .build();
    }
}
