package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa.VerificationMm;
import pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa.VerificationMmPaginaItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa.VerificationMmSectionItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json.DetMmRectanguloPaginaData;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json.DetMmRectanguloSeccionData;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class VerificationServiceMiembrosMesaImpl implements VerificationServiceMiembrosMesa {

    Logger logger = LoggerFactory.getLogger(VerificationServiceMiembrosMesaImpl.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final OmisoMiembroMesaService tabOmisoMiembroMesaService;
    private final MiembroMesaColaService tabMiembroMesaColaService;
    private final DetMmRectanguloService detMmRectanguloService;
    private final MiembroMesaSorteadoService tabMiembroMesaSorteadoService;
    private final MesaService tabMesaService;
    private final PadronElectoralService maePadronService;
    private final DocumentoElectoralService admTabDocumentoElectoralService;
    private final MesaDocumentoService tabMesaDocumentoService;
    private final ITabLogService logService;

    public VerificationServiceMiembrosMesaImpl(
            OmisoMiembroMesaService tabOmisoMiembroMesaService,
            MiembroMesaColaService tabMiembroMesaColaService,
            DetMmRectanguloService detMmRectanguloService,
            MiembroMesaSorteadoService tabMiembroMesaSorteadoService,
            MesaService tabMesaService,
            PadronElectoralService maePadronService,
            MesaDocumentoService tabMesaDocumentoService,
            DocumentoElectoralService admTabDocumentoElectoralService,
            ITabLogService logService

    ) {
        this.tabOmisoMiembroMesaService = tabOmisoMiembroMesaService;
        this.tabMiembroMesaColaService = tabMiembroMesaColaService;
        this.detMmRectanguloService = detMmRectanguloService;
        this.tabMiembroMesaSorteadoService = tabMiembroMesaSorteadoService;
        this.tabMesaService = tabMesaService;
        this.maePadronService = maePadronService;
        this.tabMesaDocumentoService = tabMesaDocumentoService;
        this.admTabDocumentoElectoralService = admTabDocumentoElectoralService;
        this.logService = logService;
    }


    @Override
    @Transactional
    public GenericResponse<VerificationMm> getRandomMiembrosMesa(TokenInfo tokenInfo, boolean reprocesar, String  tipoDenuncia) {

        try{
            Long mesaId = getMesaIdRandom(tokenInfo.getNombreUsuario(), reprocesar, tipoDenuncia);
            if (mesaId == null)
                return new GenericResponse<>(false, "No existen Miembros de mesa para verificar", null);

            Mesa mesa = getMesa(mesaId);
            if (mesa == null)
                return new GenericResponse<>(false, String.format(ConstantesComunes.MENSAJE_FORMAT_MESA_NO_EXISTE, mesaId), null);

            List<MiembroMesaSorteado> miembrosMesaSorteados = new ArrayList<>();

            List<VerificationMmPaginaItem> verificationMmPaginaItemList = new ArrayList<>();
            List<OmisoMiembroMesa> tabOmisoMiembroMesaList = null;
            Integer cantidadAusentes = 0;
            if(! mesa.getEstadoMesa().equals(ConstantesEstadoMesa.NO_INSTALADA) &&
                    !mesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL) ) {
                miembrosMesaSorteados.addAll(getMiembrosMesaSorteados(mesa, tokenInfo.getAbrevProceso()));
                cantidadAusentes = this.tabMesaService.validarActaPrincipalProcesada(mesa);

                List<DetMmRectangulo> detMmRectangulos = getDetMmRectangulos(mesaId);
                if (detMmRectangulos.isEmpty())
                    return new GenericResponse<>(false, String.format("El modelo no ha generado las secciones o cortes de miembros de mesa para la mesa %s", mesaId), null);

                DetMmRectangulo detMmRectangulo = detMmRectangulos.getFirst();
                verificationMmPaginaItemList = processPages(detMmRectangulo, miembrosMesaSorteados, tokenInfo.getAbrevProceso(), reprocesar);

                if(verificationMmPaginaItemList.isEmpty())
                    return new GenericResponse<>(false, String.format("Ocurrió un error al generar las páginas 1, 2 de la mesa %s", mesaId), null);

            }else if(mesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL) ){
                miembrosMesaSorteados.addAll(getMiembrosMesaSorteados(mesa, tokenInfo.getAbrevProceso()));
                cantidadAusentes = mesa.getCantidadElectoresHabiles();
                List<VerificationMmPaginaItem> seccionesPagina1 = new ArrayList<>();
                processPagina1Denuncia(miembrosMesaSorteados, seccionesPagina1,tokenInfo.getAbrevProceso(), tipoDenuncia );
                 verificationMmPaginaItemList.addAll(seccionesPagina1);
            } else {
                cantidadAusentes = mesa.getCantidadElectoresHabiles();
                //Solo es necesario la página 1
                List<VerificationMmSectionItem> seccionesPagina1 = new ArrayList<>();

                for (int i = 1; i <= cantidadMiembrosSorteados(tokenInfo.getAbrevProceso()); i++) {
                    seccionesPagina1.add(VerificationMmSectionItem.builder()
                        .apellidoMaterno("")
                        .apellidoPaterno("")
                        .dni("NO DISPONIBLE")
                        .firma(false)
                        .nombres("NO DISPONIBLE")
                        .cargo(i)
                        .archivoSeccion(-1L)
                        .asistioAutomatico("2")
                        .asistioUser("0")
                        .idPadron(-1L)
                            .estado(2)
                        .build());
                }

                VerificationMmPaginaItem pagina1 = VerificationMmPaginaItem.builder()
                    .pagina(1)
                    .archivoObservacion(-1L)
                    .secciones(seccionesPagina1)
                    .textoObservacionesUser("-")
                    .build();

                verificationMmPaginaItemList.add(pagina1);
            }

            Ubigeo ubigeo = mesa.getLocalVotacion().getUbigeo();

            this.tabMesaService.actualizarUsuarioAsignadoMM(tokenInfo.getNombreUsuario(), mesaId);
            if (reprocesar){
                List<MesaDocumento> listaDetalleMesaDocumento = this.tabMesaDocumentoService.buscarIdMesaAndIdDocumentoElectoral(mesaId, ConstantesComunes.ID_DOCUMENT_MIEMBRO_MESA);
                    verificationMmPaginaItemList.forEach(item -> {
                        Integer pagina = item.getPagina();
                        MesaDocumento mesaDocumento = listaDetalleMesaDocumento.stream().filter(documento -> pagina.equals(documento.getPagina()))
                                .findFirst().orElse(null);
                        item.setTextoObservacionesUser(Objects.nonNull(mesaDocumento) ? mesaDocumento.getDescripcionObservacion() : null);
                    });
            }

            VerificationMm verificationMm = VerificationMm.builder()
                .mesaId(mesaId)
                .type(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_CONTROL_DE_ASISTENCIA_MIEMBROS_MESA)
                .mesa(mesa.getCodigo())
                .estadoMesa(mesa.getEstadoMesa())
                .electoresHabiles(mesa.getCantidadElectoresHabiles())
                .electoresAusentes(cantidadAusentes)
                .localVotacion(mesa.getLocalVotacion().getNombre())
                .ubigeo(ubigeo.getCodigo())
                .departamento(ubigeo.getDepartamento())
                .provincia(ubigeo.getProvincia())
                .distrito(ubigeo.getNombre())
                .paginas(verificationMmPaginaItemList)
                .data(tabOmisoMiembroMesaList)
                .build();

            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    String.format("Se obtuvo la información para el registro de los miembros de la mesa %s, al usuario %s.", mesa.getCodigo(), tokenInfo.getNombreUsuario()),
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
            return new GenericResponse<>(true, "Miembros de mesa obtenidos exitosamente.", verificationMm);

        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }

    private Long getMesaIdRandom(String usuario, boolean reprocesar, String tipoDenuncia) {
       final String estado = definirEstado(reprocesar, tipoDenuncia);

        List<Mesa> tabMesaListAsignados = this.tabMesaService.findByEstadoDigitalizacionMmAndUsuarioAsignadoMm(estado, usuario);
        List<Mesa> tabMesaListAsignadosNoInstalados = this.tabMesaService.findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMm
            (ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE, ConstantesEstadoMesa.NO_INSTALADA, usuario);

        tabMesaListAsignados.addAll(tabMesaListAsignadosNoInstalados);

        if (!tabMesaListAsignados.isEmpty()) {
            return tabMesaListAsignados.getFirst().getId();
        }

        List<Mesa> tabMesaListLibres = this.tabMesaService.findMesasByEstadoMmAndWithRectangulos( estado, ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL);
        List<Mesa> tabMesaListLibresNoInstalados = this.tabMesaService.findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMmIsNull
            (ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE,ConstantesEstadoMesa.NO_INSTALADA);

        tabMesaListLibres.addAll(tabMesaListLibresNoInstalados);

        if (!tabMesaListLibres.isEmpty()) {
            int indiceAleatorio = secureRandom.nextInt(tabMesaListLibres.size());
            return tabMesaListLibres.get(indiceAleatorio).getId();
        }
        Long mesaId = tabMesaService.obtenerMesaForUpdate(estado);

        if (Objects.nonNull(mesaId)) {
            return mesaId;
        }


        return null;
    }

    private Mesa getMesa(Long mesaId) {
        return this.tabMesaService.findById(mesaId).orElse(null);
    }

    private String definirEstado(boolean reprocesar, String tipoDenuncia) {
        if (reprocesar) {
            return ConstantesEstadoMesa.REPROCESAR;
        }

        if (StringUtils.isBlank(tipoDenuncia)) {
            return ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA;
        }

        return tipoDenuncia;
    }

    private List<MiembroMesaSorteado> getMiembrosMesaSorteados(Mesa mesa, String acronimo) {

        List<MiembroMesaSorteado> miembrosMesaSorteados = this.tabMiembroMesaSorteadoService.findByMesa(mesa);
        if(miembrosMesaSorteados.isEmpty() && SceUtils.esMesaExtranjera(mesa)){
            return new ArrayList<>();
        }
        if (miembrosMesaSorteados.size() != cantidadMiembrosSorteados(acronimo) ) throw new InternalServerErrorException(
                java.lang.String.format("La mesa %s no cuenta con 6 miembros de mesa sorteados.", mesa.getCodigo()));

        for (MiembroMesaSorteado miembro : miembrosMesaSorteados) {
            if (miembro.getPadronElectoral() == null) throw new InternalServerErrorException(String.format("Los miembros de mesa sorteados de la mesa %s, no tienen asociados su padron electoral.", mesa.getCodigo()));
        }

        return miembrosMesaSorteados;
    }

    private Integer cantidadMiembrosSorteados(String acronimo) {
        return SceUtils.isProcesoCpr(acronimo) ? ConstantesComunes.CANTIDAD_MIEMBROS_SORTEADOS_CPR :ConstantesComunes.CANTIDAD_MIEMBROS_SORTEADOS_NO_CPR;
    }

    private List<DetMmRectangulo> getDetMmRectangulos(Long mesaId) {
        return this.detMmRectanguloService.findByMesaId(mesaId);
    }

    private List<VerificationMmPaginaItem> processPages(DetMmRectangulo detMmRectangulo, List<MiembroMesaSorteado> miembrosMesaSorteados, String acronimo, boolean reprocesar) {

        List<VerificationMmPaginaItem> verificationMmPaginaItemList = new ArrayList<>();

        processPagina1(detMmRectangulo, miembrosMesaSorteados, verificationMmPaginaItemList, acronimo, reprocesar);

        processPagina2(detMmRectangulo, verificationMmPaginaItemList, reprocesar);

        return verificationMmPaginaItemList;

    }

    private void processPagina1(DetMmRectangulo detMmRectangulo,
                                List<MiembroMesaSorteado> miembrosMesaSorteados,
                                List<VerificationMmPaginaItem> verificationMmPaginaItemList, String acronimo, boolean reprocesar) {

        Optional<DetMmRectanguloPaginaData> optionalDetMmRectanguloPagina1 = getPaginaData(detMmRectangulo, 1);

        if (optionalDetMmRectanguloPagina1.isEmpty()) {
            return;
        }
        List<Long> tabOmisoMiembroMesaList = new ArrayList<>();
        if(reprocesar){
            tabOmisoMiembroMesaList = this.tabOmisoMiembroMesaService.buscarOmisoActivoPorMesa(detMmRectangulo.getMesaId()).
                    stream().map(x->x.getMiembroMesaSorteado().getId()).toList();
        }
        //Acá armar los 6 sorteados fijos, y llenar los espacios

        List<VerificationMmSectionItem> verificationMmSectionItems = IntStream.rangeClosed(1, cantidadMiembrosSorteados(acronimo))
            .mapToObj(i -> VerificationMmSectionItem.builder()
                .cargo(i)
                .estado(0)//TACHADO O NO EXISTE
                .firma(false)
                .asistioAutomatico("0")
                .asistioUser(null)
                .build())
            .toList();

        for(VerificationMmSectionItem verificationMmSectionItem:verificationMmSectionItems){
            procesamientoCargosMiembrosMesa(verificationMmSectionItem, miembrosMesaSorteados, optionalDetMmRectanguloPagina1.get(), tabOmisoMiembroMesaList, null);
        }

        VerificationMmPaginaItem paginaItem = VerificationMmPaginaItem.builder()
                .pagina(1)
                .textoObservacionesUser(null)
                .archivoObservacion(optionalDetMmRectanguloPagina1.get().getArchivoObservacion())
                .secciones(verificationMmSectionItems)
                .build();
        verificationMmPaginaItemList.add(paginaItem);

    }

    private void processPagina1Denuncia(List<MiembroMesaSorteado> miembrosMesaSorteados,
                                List<VerificationMmPaginaItem> verificationMmPaginaItemList, String acronimo, String tipoDenuncia) {

        List<VerificationMmSectionItem> verificationMmSectionItems = IntStream.rangeClosed(1, cantidadMiembrosSorteados(acronimo))
                .mapToObj(i -> VerificationMmSectionItem.builder()
                        .cargo(i)
                        .estado(0)//TACHADO O NO EXISTE
                        .firma(false)
                        .asistioAutomatico("0")
                        .asistioUser(null)
                        .build())
                .toList();

        for(VerificationMmSectionItem verificationMmSectionItem:verificationMmSectionItems){
            procesamientoCargosMiembrosMesa(verificationMmSectionItem, miembrosMesaSorteados, null, new ArrayList<>(), tipoDenuncia);
        }

        VerificationMmPaginaItem paginaItem = VerificationMmPaginaItem.builder()
                .pagina(1)
                .textoObservacionesUser(null)
                .archivoObservacion(-1L)
                .secciones(verificationMmSectionItems)
                .tipoDenuncia(tipoDenuncia)
                .build();
        verificationMmPaginaItemList.add(paginaItem);

    }

    private void procesamientoCargosMiembrosMesa(VerificationMmSectionItem verificationMmSectionItem, List<MiembroMesaSorteado> miembrosMesaSorteados,
                                                 DetMmRectanguloPaginaData paginaData, List<Long> tabOmisoMiembroMesaList, String tipoDenuncia) {

        Integer cargoActual = verificationMmSectionItem.getCargo();
        Long archivoSeccion;
        if(Objects.isNull(tipoDenuncia)){
            archivoSeccion = paginaData.getSecciones().stream()
                    .filter(seccion -> Objects.equals(seccion.getCargo(), cargoActual))
                    .findFirst()
                    .orElseThrow(() -> new InternalServerErrorException(
                            "El modelo no generó el corte del miembro de mesa de cargo: " +
                                    ConstantesComunes.getMapNameCargosMiembrosMesa().getOrDefault(cargoActual, "Cargo Desconocido")
                    )).getArchivoSeccion();
        } else {
            archivoSeccion = -1L;
        }

        miembrosMesaSorteados.stream()
            .filter(miembro -> Objects.equals(miembro.getCargo(), cargoActual))
            .findFirst()
            .ifPresent(miembro -> {
                PadronElectoral padron = miembro.getPadronElectoral();
                if (padron == null) {
                    throw new InternalServerErrorException(
                        "No se encuentra el padrón asociado al miembro de mesa de cargo: " +
                            ConstantesComunes.getMapNameCargosMiembrosMesa().getOrDefault(cargoActual, "Cargo Desconocido")
                    );
                }

                verificationMmSectionItem.setIdPadron(padron.getId());
                verificationMmSectionItem.setIdMiembroMesaSorteado(miembro.getId());
                verificationMmSectionItem.setNombres(padron.getNombres());
                verificationMmSectionItem.setApellidoMaterno(padron.getApellidoMaterno());
                verificationMmSectionItem.setApellidoPaterno(padron.getApellidoPaterno());
                verificationMmSectionItem.setDni(padron.getDocumentoIdentidad());
                verificationMmSectionItem.setArchivoSeccion(archivoSeccion);
                verificationMmSectionItem.setEstado(miembro.getEstado());
                verificationMmSectionItem.setFirma(false);
                verificationMmSectionItem.setAsistioUser(tabOmisoMiembroMesaList.isEmpty() ? null : tabOmisoMiembroMesaList.contains(miembro.getId()) ? "2" : "1");
                verificationMmSectionItem.setAsistioAutomatico(tabOmisoMiembroMesaList.isEmpty() ? "1" : tabOmisoMiembroMesaList.contains(miembro.getId()) ? "2" : "1");
            });

        if(miembrosMesaSorteados.isEmpty()){
            verificationMmSectionItem.setArchivoSeccion(archivoSeccion);
            verificationMmSectionItem.setFirma(false);
            verificationMmSectionItem.setEstado(5);
        }
    }


    private void processPagina2(DetMmRectangulo detMmRectangulo, List<VerificationMmPaginaItem> verificationMmPaginaItemList, boolean reprocesar) {
        Optional<DetMmRectanguloPaginaData> optionalDetMmRectanguloPagina2 = getPaginaData(detMmRectangulo, 2);
        if (optionalDetMmRectanguloPagina2.isEmpty()) {
            return;
        }

        List<VerificationMmSectionItem> verificationMmSectionItems2 = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            VerificationMmSectionItem sectionItem = processMiembroNoSorteado(i, optionalDetMmRectanguloPagina2.get(), reprocesar, detMmRectangulo.getMesaId());
            verificationMmSectionItems2.add(sectionItem);
        }

        verificationMmSectionItems2.sort(Comparator.comparingInt(VerificationMmSectionItem::getCargo));

        VerificationMmPaginaItem paginaItem = VerificationMmPaginaItem.builder()
                .pagina(2)
                .textoObservacionesUser(null)
                .archivoObservacion(optionalDetMmRectanguloPagina2.get().getArchivoObservacion())
                .secciones(verificationMmSectionItems2)
                .build();
        verificationMmPaginaItemList.add(paginaItem);
    }

    private VerificationMmSectionItem processMiembroNoSorteado(int posicion, DetMmRectanguloPaginaData paginaData, boolean reprocesar, Long idMesa) {
        Optional<DetMmRectanguloSeccionData> optionalSeccionData = paginaData.getSecciones().stream()
                .filter(seccionData -> Objects.equals(seccionData.getCargo(), posicion))
                .findFirst();

        if (optionalSeccionData.isPresent()) {
            DetMmRectanguloSeccionData seccionData = optionalSeccionData.get();
            if (reprocesar){
                List<MiembroMesaCola> listMiembroMesaCola = this.tabMiembroMesaColaService.listMiembroMesaColaActivoByMesaId(idMesa);
                    MiembroMesaCola miembroMesaCola = listMiembroMesaCola.stream().filter(mmc -> Objects.equals(mmc.getCargo(), posicion))
                            .findFirst().orElse(initMiembroMesaCola());

                return VerificationMmSectionItem.builder()
                        .idPadron(miembroMesaCola.getPadronElectoral().getId())
                        .idMiembroMesaSorteado(null)
                        .nombres(miembroMesaCola.getPadronElectoral().getNombres())
                        .apellidoMaterno(miembroMesaCola.getPadronElectoral().getApellidoMaterno())
                        .apellidoPaterno(miembroMesaCola.getPadronElectoral().getApellidoPaterno())
                        .dni(miembroMesaCola.getPadronElectoral().getDocumentoIdentidad())
                        .archivoSeccion(seccionData.getArchivoSeccion())
                        .cargo(seccionData.getCargo())
                        .firma(false)
                        .asistioAutomatico("1")
                        .asistioUser(null)
                        .build();
            }else{
                return VerificationMmSectionItem.builder()
                        .idPadron(null)
                        .idMiembroMesaSorteado(null)
                        .nombres("")
                        .apellidoMaterno("")
                        .apellidoPaterno("")
                        .dni("")
                        .archivoSeccion(seccionData.getArchivoSeccion())
                        .cargo(seccionData.getCargo())
                        .firma(false)
                        .asistioAutomatico("0")
                        .asistioUser(null)
                        .build();
            }
        }else{
            return getVerificationMmNoSorteadoSectionItemNulos(posicion);

        }
    }

    private MiembroMesaCola initMiembroMesaCola(){
        MiembroMesaCola miembroMesaCola = new MiembroMesaCola();
        PadronElectoral padronElectoral = new PadronElectoral();
        padronElectoral.setId(null);
        padronElectoral.setNombres("");
        padronElectoral.setApellidoMaterno("");
        padronElectoral.setApellidoPaterno("");
        padronElectoral.setDocumentoIdentidad("");
        miembroMesaCola.setPadronElectoral(padronElectoral);
        return miembroMesaCola;
    }



    private Optional<DetMmRectanguloPaginaData> getPaginaData(DetMmRectangulo detMmRectangulo, int pagina) {
        return detMmRectangulo.getPaginas().stream()
                .filter(paginaData -> Objects.equals(paginaData.getPagina(), pagina))
                .findFirst();
    }


    private boolean checkOmiso(VerificationMmSectionItem vlsi) {
        if (vlsi.getAsistioUser() == null) {
            return vlsi.getAsistioAutomatico() != null && vlsi.getAsistioAutomatico().equals("2");
        } else {
            return vlsi.getAsistioUser().equals("2");
        }
    }

    private boolean isObservacion(VerificationMmSectionItem vlsi) {
        if(vlsi.getEstado() != 1) return false;
        String asistio = vlsi.getAsistioUser() != null ? vlsi.getAsistioUser() : vlsi.getAsistioAutomatico();
        return asistio != null && (asistio.equals("0") || asistio.equals("3"));
    }

    private boolean addOmisoMiembroMesa(VerificationMmSectionItem vlsi, Mesa tabMesa,
                                        List<OmisoMiembroMesa> tabOmisoMiembroMesaList, TokenInfo tokenInfo) {
        Optional<PadronElectoral> optionalMaePadron = this.maePadronService.findById(vlsi.getIdPadron());
        if (optionalMaePadron.isPresent()) {
            OmisoMiembroMesa tabOmisoMiembroMesa = OmisoMiembroMesa.builder()
                    .miembroMesaSorteado(new MiembroMesaSorteado(vlsi.getIdMiembroMesaSorteado()))
                    .mesa(tabMesa)
                    .activo(ConstantesComunes.ACTIVO)
                    .usuarioCreacion(tokenInfo.getNombreUsuario())
                    .fechaCreacion(new Date())
                    .build();
            tabOmisoMiembroMesaList.add(tabOmisoMiembroMesa);
            return true;
        }
        return false;
    }

    private boolean addMiembroMesaCola(VerificationMmSectionItem vlsi, Mesa tabMesa,
                                       List<MiembroMesaCola> tabMiembroMesaColaList, TokenInfo tokenInfo) {
        Optional<PadronElectoral> optionalMaePadron = this.maePadronService.findById(vlsi.getIdPadron());
        if (optionalMaePadron.isPresent()) {
            PadronElectoral maePadron = optionalMaePadron.get();
            MiembroMesaCola miembroMesaCola = MiembroMesaCola.builder()
                    .padronElectoral(maePadron)
                    .mesa(tabMesa)
                    .cargo(vlsi.getCargo())
                    .activo(ConstantesComunes.ACTIVO)
                    .usuarioCreacion(tokenInfo.getNombreUsuario())
                    .fechaCreacion(new Date()).build();
            tabMiembroMesaColaList.add(miembroMesaCola);
            return true;
        }
        return false;
    }

    private String processPage1(List<VerificationMmSectionItem> sections, Mesa tabMesa,
                                 List<OmisoMiembroMesa> tabOmisoMiembroMesaList, TokenInfo tokenInfo) {
        for (VerificationMmSectionItem vlsi : sections) {
            boolean isOmiso = checkOmiso(vlsi);
            if (isOmiso) {
                if (!addOmisoMiembroMesa(vlsi, tabMesa, tabOmisoMiembroMesaList, tokenInfo)) {
                    return  String.format("En la página 1, el id padrón %d,no esta registrado en BD, en el cargo de posición %d", vlsi.getIdPadron(), vlsi.getCargo());
                }
            } else if (isObservacion(vlsi)) {
                return String.format("En la página 1, debe seleccionar ASISTIÓ O NO ASISTIÓ en el cargo de posición %d", vlsi.getCargo());
            }
        }
        return "";
    }

    private String processPage2(List<VerificationMmSectionItem> sections, Mesa tabMesa,
                                 List<MiembroMesaCola> tabMiembroMesaColaList, TokenInfo tokenInfo) {
        for (VerificationMmSectionItem vlsi : sections) {
            String dni = vlsi.getDni();
            if (dni != null && !dni.isEmpty()) {
                if (vlsi.getIdPadron() != null) {
                    if (!addMiembroMesaCola(vlsi, tabMesa, tabMiembroMesaColaList, tokenInfo)) {
                        return  String.format("En la página 1, el id padrón %d,no esta registrado en BD, en el cargo de posición %d", vlsi.getIdPadron(), vlsi.getCargo());
                    }
                } else {
                    return String.format("En la página 2, en el cargo de orden %d, no se ha consultado el servicio para obtener los datos del DNI %s", vlsi.getCargo(), dni);
                }
            }
        }
        return "";
    }



    @Override
    @Transactional
    public GenericResponse<Boolean> saveMiembrosMesa(VerificationMm request, TokenInfo tokenInfo, boolean reprocesar) {
        try {
            Mesa tabMesa = validarGuardadoOmisosMiembrosMesa(request);

            List<OmisoMiembroMesa> tabOmisoMiembroMesaList = new ArrayList<>();
            List<MiembroMesaCola> tabMiembroMesaColaList = new ArrayList<>();
            String mensaje = "Se registró correctamente los omisos de miembros de mesa de la mesa "+tabMesa.getCodigo()+".";
            if (tabMesa.getEstadoMesa().equals(ConstantesEstadoMesa.NO_INSTALADA)) {
                tabOmisoMiembroMesaList = procesarOmisosMiembrosMesaNoInstalada(tabMesa, tokenInfo.getNombreUsuario());
            } else {
                if(!tabMesa.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PERDIDA_TOTAL)){
                    procesarOmisosMiembrosMesaProcesadas(tabMesa, request, tabOmisoMiembroMesaList, tabMiembroMesaColaList, tokenInfo);
                }else{
                    mensaje = "Se registró correctamente los omisos de miembros de mesa de la mesa declarada como perdida total "+tabMesa.getCodigo()+".";
                }
            }

            if(!tabOmisoMiembroMesaList.isEmpty()){
            int cantidadAfectada =    this.tabOmisoMiembroMesaService.inhabilitarOmisoMiembroMesa(request.getMesaId(), tokenInfo.getNombreUsuario());
            logger.info("Se deshabilitaron {} omisos de mesa.",cantidadAfectada);
                this.tabOmisoMiembroMesaService.saveAll(tabOmisoMiembroMesaList);
            }
            boolean debeInhabilitar = !tabMiembroMesaColaList.isEmpty() || (reprocesar &&
                    !this.tabMiembroMesaColaService.listMiembroMesaColaActivoByMesaId(request.getMesaId()).isEmpty());

            if (debeInhabilitar) {
                int cantidadAfectada = this.tabMiembroMesaColaService
                        .inhabilitarOmisoMiembroMesaCola(
                                request.getMesaId(),
                                tokenInfo.getNombreUsuario()
                        );
                logger.info("Se deshabilitaron {} omisos de mesa cola.", cantidadAfectada);
            }
            if (!tabMiembroMesaColaList.isEmpty()) {
                this.tabMiembroMesaColaService.saveAll(tabMiembroMesaColaList);
            }

            tabMesa.setEstadoDigitalizacionMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PROCESADO);
            this.tabMesaService.save(tabMesa);



            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(), mensaje,
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

            return new GenericResponse<>(true, mensaje, true);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }


    public Mesa validarGuardadoOmisosMiembrosMesa(VerificationMm request){
        Optional<Mesa> optionalTabMesa = this.tabMesaService.findById(request.getMesaId());
        if (optionalTabMesa.isEmpty())
            throw  new InternalServerErrorException(String.format("La mesa %s no existe ", request.getMesaId()));
        Mesa tabMesa = optionalTabMesa.get();

        if (request.getPaginas() == null || request.getPaginas().isEmpty())
            throw  new BadRequestException("No se encontro páginas en la petición para registrarlas.");

        return tabMesa;
    }

    public List<OmisoMiembroMesa> procesarOmisosMiembrosMesaNoInstalada(Mesa tabMesa, String usuario){
        List<MiembroMesaSorteado> miembroMesaSorteados = this.tabMiembroMesaSorteadoService.findByMesa(tabMesa);

        List<MiembroMesaSorteado> filtrados = miembroMesaSorteados.stream()
            .filter(m -> ConstantesComunes.ACTIVO.equals(m.getEstado()))  // <-- aquí filtras por el estado que quieras
            .toList();

        if(filtrados.isEmpty())
            throw  new InternalServerErrorException("La mesa "+tabMesa.getCodigo()+" no contiene miembros de mesa sorteados.");

        Date now = new Date();
        return filtrados.stream()
            .map(sorteado -> OmisoMiembroMesa.builder()
                .miembroMesaSorteado(sorteado)
                .mesa(tabMesa)
                .activo(ConstantesComunes.ACTIVO)
                .usuarioCreacion(usuario)
                .fechaCreacion(now)
                .build())
            .toList();
    }

    public void procesarOmisosMiembrosMesaProcesadas(Mesa tabMesa, VerificationMm request, List<OmisoMiembroMesa> tabOmisoMiembroMesaList,
                                                     List<MiembroMesaCola> tabMiembroMesaColaList, TokenInfo tokenInfo){
        for (VerificationMmPaginaItem vpi : request.getPaginas()) {
            Integer pagina = vpi.getPagina();
            List<VerificationMmSectionItem> verificationMmSectionItems = vpi.getSecciones();

            if (verificationMmSectionItems == null || verificationMmSectionItems.isEmpty()) continue;

            //actualizar las observaciones
            guardarMesaDocumentoArchivoMm(tabMesa.getId(), vpi.getPagina(), vpi.getTextoObservacionesUser(), tokenInfo.getNombreUsuario());

            switch (pagina) {
                case 1:
                    String resultado = processPage1(verificationMmSectionItems, tabMesa, tabOmisoMiembroMesaList, tokenInfo);
                    if (!resultado.isEmpty()) throw new BadRequestException(resultado);
                    break;
                case 2:
                    String resultado2 =  processPage2(verificationMmSectionItems, tabMesa, tabMiembroMesaColaList, tokenInfo);
                    if (!resultado2.isEmpty()) throw new BadRequestException(resultado2);
                    break;
                default:
                    break;
            }
        }
    }


    private void guardarMesaDocumentoArchivoMm(Long idMesa, Integer pagina, String observacion, String usuario) {

        logger.info("Actualizando observaciones de documento MM para idMesa={}, página={}, usuario={}", idMesa, pagina, usuario);

        DocumentoElectoral documentoElectoral = this.admTabDocumentoElectoralService
            .findByAbreviatura(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA);

        if (documentoElectoral == null) {
            throw new InternalServerErrorException(String.format(
                "No existe registrado el documento electoral %s.",
                ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA));
        }

        if (pagina == null || pagina < 0) {
            throw new InternalServerErrorException("El nro de página no puede ser nulo o menor a cero.");
        }

        if (usuario == null || usuario.isBlank()) {
            throw new InternalServerErrorException("El usuario no puede ser nulo o vacío.");
        }

        if (observacion == null) {
            observacion = ""; // O lanzar error si lo necesitas obligatorio
        }

        MesaDocumento mesaDocumento = this.tabMesaDocumentoService
            .findByMesaAndAdmDocumentoElectoralAndTipoArchivoAndPagina(
                idMesa,
                documentoElectoral.getId(),
                ConstantesFormatos.IMAGE_TIF_VALUE,
                pagina
            )
            .orElseThrow(() -> new InternalServerErrorException(String.format(
                "No existe un documento archivo de MM con el número de página %d.", pagina)));

        mesaDocumento.setUsuarioModificacion(usuario);
        mesaDocumento.setFechaModificacion(new Date());
        mesaDocumento.setDescripcionObservacion(observacion);

        this.tabMesaDocumentoService.save(mesaDocumento);

        logger.info("Documento MM actualizado correctamente para idMesa={}, página={}", idMesa, pagina);
    }

    @Override
    @Transactional
    public GenericResponse<Boolean> rechazarMiembrosMesa(Long mesaId, TokenInfo tokenInfo) {
        Mesa tabMesa;
        if (mesaId == null)
            return new GenericResponse<>(false, "El parametro mesaId no puede ser nula", false);

        Optional<Mesa> optionalTabMesa = this.tabMesaService.findById(mesaId);
        if (optionalTabMesa.isEmpty())
            return new GenericResponse<>(false, String.format("La mesa %s no existe ", mesaId), false);

        tabMesa = optionalTabMesa.get();
        tabMesa.setEstadoDigitalizacionMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA);
        tabMesa.setUsuarioAsignadoMm(null);
        tabMesa.setFechaAsignadoMm(null);
        tabMesa.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        tabMesa.setFechaModificacion(new Date());
        this.tabMesaService.save(tabMesa);//cada
        this.detMmRectanguloService.deleteByMesaIdAndType(mesaId, ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA);

        DocumentoElectoral admTabDocumentoElectoral = admTabDocumentoElectoralService.findByAbreviatura(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA);
        if (admTabDocumentoElectoral == null)
            return new GenericResponse<>(false, "El documento electoral " + ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA + " no se encuentra registrado en los documentos electorales de la base de datos.", null);


        this.tabMesaDocumentoService.deleteByAdmDocumentoElectoralIdAndMesaId(admTabDocumentoElectoral.getId(), tabMesa.getId());

        this.detMmRectanguloService.deleteByMesaId(tabMesa.getId());

        String mensaje =  String.format("Los miembros de mesa de la mesa %s, fue rechazada, debe volver a digitalizarse.", tabMesa.getCodigo());

        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName()
                ,mensaje , tokenInfo.getCodigoCentroComputo(),ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return new GenericResponse<>(true,mensaje, true);
    }

    private VerificationMmSectionItem getVerificationMmNoSorteadoSectionItemNulos(int cargo) {
        return VerificationMmSectionItem.builder()
                .idPadron(null)
                .nombres("")
                .apellidoMaterno("")
                .apellidoPaterno("")
                .dni("")
                .archivoSeccion(null)
                .cargo(cargo)
                .asistioAutomatico("0")
                .asistioUser(null)
                .firma(null)
                .build();
    }
}
