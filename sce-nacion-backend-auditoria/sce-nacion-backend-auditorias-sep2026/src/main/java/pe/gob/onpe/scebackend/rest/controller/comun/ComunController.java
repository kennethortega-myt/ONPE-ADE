package pe.gob.onpe.scebackend.rest.controller.comun;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.comun.*;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.comun.AmbitoElectoralDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.CentroComputoDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.DetCatalogoEstructuraResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.NivelUbigeoDto;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.comun.*;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.List;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/comun")
public class ComunController extends BaseController {

    private static final String MESSAGE = "Se listo correctamente";

    private final IAmbitoElectoralNacionService ambitoElectoralNacionService;

    private final IUbigeoService ubigeoService;

    private final IConfiguracionProcesoElectoralService configuracionProcesoElectoralService;

    private final ICentroComputoNacionService centroComputoNacionService;

    private final IEleccionCustomService eleccionCustomService;

    private final IDetCatalogoEstructuraNacionService detCatalogoEstructuraNacionService;

    public ComunController(TokenDecoder tokenDecoder, IAmbitoElectoralNacionService ambitoElectoralNacionService, IUbigeoService ubigeoService, IConfiguracionProcesoElectoralService configuracionProcesoElectoralService, ICentroComputoNacionService centroComputoNacionService, IEleccionCustomService eleccionCustomService, IDetCatalogoEstructuraNacionService detCatalogoEstructuraNacionService) {
        super(tokenDecoder);
        this.ambitoElectoralNacionService = ambitoElectoralNacionService;
        this.ubigeoService = ubigeoService;
        this.configuracionProcesoElectoralService = configuracionProcesoElectoralService;
        this.centroComputoNacionService = centroComputoNacionService;
        this.eleccionCustomService = eleccionCustomService;
        this.detCatalogoEstructuraNacionService = detCatalogoEstructuraNacionService;
    }

    @GetMapping("/proceso-electoral")
    public ResponseEntity<GenericResponse> listProcesoElectal() {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.configuracionProcesoElectoralService.listAll(SceConstantes.ACTIVO));
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/centro-computo-por-eleccion/{idEleccion}/{esquema}")
    public ResponseEntity<GenericResponse> listCentroComputoPorIdEleccion(@PathVariable("idEleccion") Integer idEleccion,
                                                             @PathVariable("esquema") String esquema,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        CentroComputoRequestDto filtro = new CentroComputoRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdEleccion(idEleccion);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        GenericResponse genericResponse = new GenericResponse();
        List<CentroComputoDto> lista = this.centroComputoNacionService.listarCentroComputoPorEleccion(filtro);

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/centro-computo-por-eleccion/{esquema}")
    public ResponseEntity<GenericResponse> listCentroComputo(@PathVariable("esquema") String esquema,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        CentroComputoRequestDto filtro = new CentroComputoRequestDto();
        filtro.setEsquema(esquema);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        GenericResponse genericResponse = new GenericResponse();
        List<CentroComputoDto> lista = this.centroComputoNacionService.listarCentroComputo(filtro);

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/ambito-electoral/buscar-por-centro-computo/{idCentroComputo}/{esquema}")    
    public ResponseEntity<GenericResponse> listAmbitoElectoralPorIdCentroComputo(
            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        AmbitoElectoralRequestDto filtro = new AmbitoElectoralRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<AmbitoElectoralDto> lista = this.ambitoElectoralNacionService.listarAmbitoElectoralPorCentroComputo(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/buscar-nivel-ubigeo-uno-por-eleccion-y-centro-computo/{idEleccion}/{idCentroComputo}/{esquema}")
    public ResponseEntity<GenericResponse> listNivelUbigeoUnoPorIdEleccionIdCentroComputo(
            @PathVariable(name = "idEleccion") Integer idEleccion,
            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdEleccion(idEleccion);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeUnoPorCentroComputo(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/{idEleccion}/departamento/{idDepartamento}/provincias/{esquema}/idCentroComputo/{idCentroComputo}")
    public ResponseEntity<GenericResponse> listProvincias(@PathVariable(name = "idDepartamento") Integer idDepartamento,
                                            @PathVariable(name = "idEleccion") Integer idEleccion,
                                            @PathVariable(name = "esquema") String esquema,
                                            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setIdEleccion(idEleccion);
        filtro.setEsquema(esquema);
        filtro.setIdUbigePadre(idDepartamento);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeDosPorNivelUno(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/{idEleccion}/provincia/{idProvincia}/distritos/{esquema}/idCentroComputo/{idCentroComputo}")
    public ResponseEntity<GenericResponse> listDistritos(@PathVariable(name = "idProvincia") Integer idProvincia,
                                           @PathVariable(name = "idEleccion") Integer idEleccion,
                                           @PathVariable(name = "esquema") String esquema,
                                           @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setIdEleccion(idEleccion);
        filtro.setEsquema(esquema);
        filtro.setIdUbigePadre(idProvincia);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeTresPorNivelDos(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/eleccion-por-proceso-electoral/{procesoId}/{esquema}")
    public ResponseEntity<GenericResponse> listEleccionesPorProceso(
            @PathVariable("procesoId") Integer id,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse genericResponse = new GenericResponse();

        EleccionRequestDto filtro = new EleccionRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdProcesoElectoral(id);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<pe.gob.onpe.scebackend.model.dto.response.comun.EleccionDto> elecciones = this.eleccionCustomService.obtenerEleccionPorProcesoElectoralId(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(elecciones);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/buscar-nivel-ubigeo-uno/{idEleccion}/{idAmbitoElectoral}/{esquema}")
    public ResponseEntity<GenericResponse> listNivelUbigeoUnoPorAmbitoElectoral(
            @PathVariable(name = "idEleccion") Integer idEleccion,
            @PathVariable(name = "idAmbitoElectoral") Integer idAmbitoElectoral,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdEleccion(idEleccion);
        filtro.setIdAmbitoElectoral(idAmbitoElectoral);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeUnoPorAmbitoElectoral(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/det-catalogo-estructura/{esquema}/{tipoReporte}")
    public ResponseEntity<GenericResponse> listDetCatalogoEstructura(
                                           @PathVariable(name = "esquema") String esquema,
                                           @PathVariable(name = "tipoReporte") String tipoReporte,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        DetCatalogoEstructuraRequestDto filtro = new DetCatalogoEstructuraRequestDto();
        filtro.setEsquema(esquema);
        filtro.setTipoReporte(tipoReporte);

        List<DetCatalogoEstructuraResponseDto> lista = this.detCatalogoEstructuraNacionService.listarDetCatalogoEstructura(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }


    @GetMapping("/ambito-electoral/buscar-por-eleccion/{idEleccion}/{esquema}")    
    public ResponseEntity<GenericResponse> listAmbitoElectoralPorIdEleccion(
            @PathVariable(name = "idEleccion") Integer idEleccion,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        AmbitoElectoralRequestDto filtro = new AmbitoElectoralRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdEleccion(idEleccion);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<AmbitoElectoralDto> lista = this.ambitoElectoralNacionService.listarAmbitoElectoralPorEleccion(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    
    @GetMapping("/centro-computo-por-ambito/{idAmbito}/{esquema}")
    public ResponseEntity<GenericResponse> listCentroComputoPorIdAmbito(@PathVariable("idAmbito") Integer idAmbito,
                                                             @PathVariable("esquema") String esquema,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        CentroComputoRequestDto filtro = new CentroComputoRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdAmbitoElectoral(idAmbito);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        GenericResponse genericResponse = new GenericResponse();
        List<CentroComputoDto> lista = this.centroComputoNacionService.listarCentroComputoPorAmbitoElectoral(filtro);

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/ubigeo/buscar-distrito-electoral-ambito/{idAmbitoElectoral}/{esquema}")
    public ResponseEntity<GenericResponse> listDistritoElectoralPorAmbitoElectoral(
            @PathVariable(name = "idAmbitoElectoral") Integer idAmbitoElectoral,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdAmbitoElectoral(idAmbitoElectoral);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeUnoDistritoElecXambito(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/ubigeo/buscar-nivel-ubigeo-dos-distrito-elec/{idDistritoElectoral}/{esquema}/{idCentroComputo}/{idAmbitoElectoral}")
    public ResponseEntity<GenericResponse> listNivelUbigeoDosPorDistritoElec(
            @PathVariable(name = "idDistritoElectoral") Integer idDistritoElectoral,
            @PathVariable("esquema") String esquema,
            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
            @PathVariable(name = "idAmbitoElectoral") Integer idAmbitoElectoral,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(esquema);
        filtro.setIdUbigePadre(idDistritoElectoral);
        filtro.setIdCentroComputo(idCentroComputo);
        filtro.setIdAmbitoElectoral(idAmbitoElectoral);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeDosPorDistritoElec(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage(MESSAGE);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

}
