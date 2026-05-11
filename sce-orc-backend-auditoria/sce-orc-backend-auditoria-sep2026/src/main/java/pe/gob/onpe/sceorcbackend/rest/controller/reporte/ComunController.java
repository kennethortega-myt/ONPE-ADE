package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.*;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AmbitoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.*;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ACCESO_TOTAL)
@RestController
@Validated
@CrossOrigin
@RequestMapping("/comun")
public class ComunController extends BaseController {
	
	private static final String MESSAGE = "Se listo correctamente";

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final CentroComputoService maeCentroComputoService;

    private final IAmbitoElectoralNacionService ambitoElectoralNacionService;

    private final IUbigeoService ubigeoService;

    private final ICentroComputoNacionService centroComputoNacionService;

    private final IEleccionCustomService eleccionCustomService;

    private final IDetCatalogoEstructuraNacionService detCatalogoEstructuraNacionService;

    public ComunController(CentroComputoService maeCentroComputoService, IAmbitoElectoralNacionService ambitoElectoralNacionService, IUbigeoService ubigeoService, ICentroComputoNacionService centroComputoNacionService, IEleccionCustomService eleccionCustomService, IDetCatalogoEstructuraNacionService detCatalogoEstructuraNacionService) {
        this.maeCentroComputoService = maeCentroComputoService;
        this.ambitoElectoralNacionService = ambitoElectoralNacionService;
        this.ubigeoService = ubigeoService;
        this.centroComputoNacionService = centroComputoNacionService;
        this.eleccionCustomService = eleccionCustomService;
        this.detCatalogoEstructuraNacionService = detCatalogoEstructuraNacionService;
    }



    @GetMapping("/centro-computo-por-eleccion/{idEleccion}/{esquema}")
    public ResponseEntity<GenericResponse> listCentroComputoPorIdEleccion(@PathVariable("idEleccion") Integer idEleccion,
                                                                          @PathVariable("esquema") String esquema,
                                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse<List<pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto>> genericResponse = new pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse<>();
        List<CentroComputo> centrosComputo = this.maeCentroComputoService.findAll();

        List<pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto> ccDto = null;
        if (centrosComputo != null && !centrosComputo.isEmpty()) {
            ccDto = centrosComputo.parallelStream()
                    .map(dto -> {
                        pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto ambito = new pe.gob.onpe.sceorcbackend.model.dto.CentroComputoDto();
                        ambito.setId(dto.getId());
                        ambito.setCodigo(dto.getCodigo());
                        ambito.setNombre(dto.getNombre());
                        return ambito;
                    })
                    .toList();
        }


        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(ccDto);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/centro-computo-por-eleccion/{esquema}")
    public ResponseEntity<GenericResponse> listCentroComputo(@PathVariable("esquema") String esquema,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        CentroComputoRequestDto filtro = new CentroComputoRequestDto();
        filtro.setEsquema(schema);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        GenericResponse genericResponse = new GenericResponse();
        List<CentroComputoDto> lista = this.centroComputoNacionService.listarCentroComputo(filtro);

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ambito-electoral/buscar-por-centro-computo/{idCentroComputo}/{esquema}")
    public ResponseEntity<?> listAmbitoElectoralPorIdCentroComputo(
            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        AmbitoElectoralRequestDto filtro = new AmbitoElectoralRequestDto();
        filtro.setEsquema(schema);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<AmbitoElectoralDto> lista = this.ambitoElectoralNacionService.listarAmbitoElectoralPorCentroComputo(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage("Se listo correctamente");
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/buscar-nivel-ubigeo-uno/{idEleccion}/{idAmbitoElectoral}/{esquema}")
    public ResponseEntity<?> listNivelUbigeoUnoPorAmbitoElectoral(
            @PathVariable(name = "idEleccion") Integer idEleccion,
            @PathVariable(name = "idAmbitoElectoral") Integer idAmbitoElectoral,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(schema);
        filtro.setIdEleccion(idEleccion);
        filtro.setIdAmbitoElectoral(idAmbitoElectoral);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeUnoPorAmbitoElectoral(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage("Se listo correctamente");

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/{idEleccion}/departamento/{idDepartamento}/provincias/{esquema}/idCentroComputo/{idCentroComputo}")
    public ResponseEntity<?> listProvincias(@PathVariable(name = "idDepartamento") Integer idDepartamento,
                                            @PathVariable(name = "idEleccion") Integer idEleccion,
                                            @PathVariable(name = "esquema") String esquema,
                                            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setIdEleccion(idEleccion);
        filtro.setEsquema(schema);
        filtro.setIdUbigePadre(idDepartamento);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeDosPorNivelUno(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage("Se listo correctamente");

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/{idEleccion}/provincia/{idProvincia}/distritos/{esquema}/idCentroComputo/{idCentroComputo}")
    public ResponseEntity<?> listDistritos(@PathVariable(name = "idProvincia") Integer idProvincia,
                                           @PathVariable(name = "idEleccion") Integer idEleccion,
                                           @PathVariable(name = "esquema") String esquema,
                                           @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setIdEleccion(idEleccion);
        filtro.setEsquema(schema);
        filtro.setIdUbigePadre(idProvincia);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeTresPorNivelDos(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage("Se listo correctamente");

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ubigeo/buscar-nivel-ubigeo-uno-por-eleccion-y-centro-computo/{idEleccion}/{idCentroComputo}/{esquema}")
    public ResponseEntity<?> listNivelUbigeoUnoPorIdEleccionIdCentroComputo(
            @PathVariable(name = "idEleccion") Integer idEleccion,
            @PathVariable(name = "idCentroComputo") Integer idCentroComputo,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(schema);
        filtro.setIdEleccion(idEleccion);
        filtro.setIdCentroComputo(idCentroComputo);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<NivelUbigeoDto> lista = this.ubigeoService.listarNivelUbigeUnoPorCentroComputo(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage("Se listo correctamente");

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/ambito-electoral/buscar-por-eleccion/{idEleccion}/{esquema}")
    public ResponseEntity<?> listAmbitoElectoralPorIdEleccion(
            @PathVariable(name = "idEleccion") Integer idEleccion,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        AmbitoElectoralRequestDto filtro = new AmbitoElectoralRequestDto();
        filtro.setEsquema(schema);
        filtro.setIdEleccion(idEleccion);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<AmbitoElectoralDto> lista = this.ambitoElectoralNacionService.listarAmbitoElectoralPorEleccion(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        genericResponse.setMessage("Se listo correctamente");

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/centro-computo-por-ambito/{idAmbito}/{esquema}")
    public ResponseEntity<GenericResponse> listCentroComputoPorIdAmbito(@PathVariable("idAmbito") Integer idAmbito,
                                                                        @PathVariable("esquema") String esquema,
                                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        CentroComputoRequestDto filtro = new CentroComputoRequestDto();
        filtro.setEsquema(schema);
        filtro.setIdAmbitoElectoral(idAmbito);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        GenericResponse genericResponse = new GenericResponse();
        List<CentroComputoDto> lista = this.centroComputoNacionService.listarCentroComputoPorAmbitoElectoral(filtro);

        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(lista);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/eleccion-por-proceso-electoral/{procesoId}/{esquema}")
    public ResponseEntity<GenericResponse> listEleccionesPorProceso(
            @PathVariable("procesoId") Integer id,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {


        GenericResponse genericResponse = new GenericResponse();
        EleccionRequestDto filtro = new EleccionRequestDto();
        filtro.setEsquema(schema);
        filtro.setIdProcesoElectoral(id);

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        List<EleccionDto> elecciones = this.eleccionCustomService.obtenerEleccionPorProcesoElectoralId(filtro);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(elecciones);

        return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/det-catalogo-estructura/{esquema}/{tipoReporte}")
    public ResponseEntity<?> listDetCatalogoEstructura(
            @PathVariable(name = "esquema") String esquema,
            @PathVariable(name = "tipoReporte") String tipoReporte,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        DetCatalogoEstructuraRequestDto filtro = new DetCatalogoEstructuraRequestDto();
        filtro.setEsquema(schema);
        filtro.setTipoReporte(tipoReporte);

        List<DetCatalogoEstructuraResponseDto> lista = this.detCatalogoEstructuraNacionService.listarDetCatalogoEstructura(filtro);
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setMessage("Se listo correctamente");
        genericResponse.setData(lista);

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
    
    @GetMapping("/ubigeo/buscar-distrito-electoral-ambito/{idAmbitoElectoral}/{esquema}")
    public ResponseEntity<GenericResponse> listDistritoElectoralPorAmbitoElectoral(
            @PathVariable(name = "idAmbitoElectoral") Integer idAmbitoElectoral,
            @PathVariable("esquema") String esquema,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        UbigeoRequestDto filtro = new UbigeoRequestDto();
        filtro.setEsquema(schema);
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
        filtro.setEsquema(schema);
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
