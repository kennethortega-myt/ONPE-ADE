package pe.gob.onpe.scebackend.rest.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.adapter.LocalDateTypeAdapter;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ResponseAutorizacionDTO;
import pe.gob.onpe.scebackend.model.dto.request.*;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.service.ICatalogoService;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.IDetalleTipoEleccionDocumentoElectoralEscrutinioService;
import pe.gob.onpe.scebackend.model.service.ITabAutorizacionService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;


@RestController
@Validated
@Slf4j
@RequestMapping("/configuracionProcesoElectoral")
public class ConfiguracionProcesoElectoralController extends BaseController {

    private final IConfiguracionProcesoElectoralService configuracionProcesoElectoralService;

    private final ICatalogoService catalogoService;

    private final IDetalleTipoEleccionDocumentoElectoralEscrutinioService escrutinioService;
    
    private final ITabAutorizacionService autorizacionService;

    private final ITabLogTransaccionalService logService;

    public ConfiguracionProcesoElectoralController(TokenDecoder tokenDecoder, IConfiguracionProcesoElectoralService configuracionProcesoElectoralService, 
                                                   ICatalogoService catalogoService, IDetalleTipoEleccionDocumentoElectoralEscrutinioService escrutinioService,
                                                   ITabAutorizacionService autorizacionService, ITabLogTransaccionalService logService) {
        super(tokenDecoder);
        this.configuracionProcesoElectoralService = configuracionProcesoElectoralService;
        this.catalogoService = catalogoService;
        this.escrutinioService = escrutinioService;
        this.autorizacionService = autorizacionService;
        this.logService = logService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping()
    public ResponseEntity<GenericResponse> listAll()  {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.configuracionProcesoElectoralService.listAll(SceConstantes.ACTIVO));
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping
    public ResponseEntity<GenericResponse> guardar(@RequestBody ConfiguracionProcesoElectoralRequestDTO requestDTO, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  {
        GenericResponse genericResponse = new GenericResponse();
        try {
            genericResponse.setSuccess(Boolean.TRUE);
            LoginUserHeader user = getUserLogin(authorization);
            requestDTO.setNombreEsquemaBdOnpe(requestDTO.getNombreEsquemaBdOnpe().toLowerCase());
            requestDTO.setNombreEsquemaPrincipal(requestDTO.getNombreEsquemaPrincipal().toLowerCase());
            this.configuracionProcesoElectoralService.guardarConfiguracionProcesoElectoral(requestDTO, user.getUsuario());
        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(genericResponse, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(genericResponse, HttpStatus.CREATED);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PutMapping
    public ResponseEntity<GenericResponse> update(@RequestParam("data") String data, @RequestParam("file") MultipartFile file, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws IOException {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTypeAdapter())
                .create();
        ConfiguracionProcesoElectoralRequestDTO request = gson.fromJson(data, ConfiguracionProcesoElectoralRequestDTO.class);
        request.setNombreEsquemaBdOnpe(request.getNombreEsquemaBdOnpe().toLowerCase());
        request.setNombreEsquemaPrincipal(request.getNombreEsquemaPrincipal().toLowerCase());
        request.setLogo(file.getBytes());
        GenericResponse genericResponse = new GenericResponse();
        try {
            genericResponse.setSuccess(Boolean.TRUE);
            LoginUserHeader user = getUserLogin(authorization);
            genericResponse.setData(this.configuracionProcesoElectoralService.guardarConfiguracionProcesoElectoral(request, user.getUsuario()));
        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(genericResponse, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(genericResponse, HttpStatus.CREATED);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PutMapping("/eliminarProceso")
    public ResponseEntity<GenericResponse> updateEstado(@RequestBody GenericRequestDTO requestDTO, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        GenericResponse response = new GenericResponse();
        try{
            LoginUserHeader user = getUserLogin(authorization);
            return new ResponseEntity<>(this.configuracionProcesoElectoralService.actualizarEstado(requestDTO.getEstado(),
                    requestDTO.getId(), user.getUsuario()), HttpStatus.OK);
        }catch(Exception e){
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping("/cargaData")
    public ResponseEntity<GenericResponse> cargarData(@RequestBody CargaDataRequestDTO request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  {
        GenericResponse response = new GenericResponse();
        try {
        	
            LoginUserHeader user = getUserLogin(authorization);
            request.setUsuario(user.getUsuario());
            
            Optional<ConfiguracionProcesoElectoral> procesoOp = configuracionProcesoElectoralService.findByAcronimo(request.getIdProceso());

            ResponseAutorizacionDTO responseAutorizacionDTO = this.autorizacionService.verificarAutorizacion(
            		procesoOp.get().getFechaConvocatoria(), 
            		request.getUsuario(), ConstantesAutorizacion.TIPO_AUTORIZACION_CARGA_DATOS,
                    "la Carga de datos", String.format("Solicitud para la Carga de datos por el usuario %s", request.getUsuario()));

            if(!responseAutorizacionDTO.isContinue()){
              response.setSuccess(Boolean.FALSE);
              response.setMessage(responseAutorizacionDTO.getMessage());
              return new ResponseEntity<>(response, HttpStatus.OK);
            }
            
            
            response = this.configuracionProcesoElectoralService.cargardatos(request);
            if (response.isSuccess()) {
                int autorizacionFlag = responseAutorizacionDTO.isAutorizado()
                        ? ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI
                        : ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO;
                try {
                    this.logService.registrarLog(
                            request.getUsuario(),
                            Thread.currentThread().getStackTrace()[1].getMethodName(),
                            this.getClass().getSimpleName(),
                            String.format("Solicitud para la Carga de datos por el usuario %s", request.getUsuario()),
                            "",
                            "C56000",
                            autorizacionFlag,
                            ConstantesComunes.LOG_TRANSACCIONES_ACCION
                    );
                } catch (Exception e) {
                    log.error("Error al registrar log: {}", e.getMessage());
                }

                Map<String, Boolean> resultadoEstructura = this.catalogoService.ejecutarEstructuras(request.getNombreEsquemaPrincipal(), request.getUsuario());
                response.setData(resultadoEstructura);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @GetMapping("/vigente")
    public ResponseEntity<GenericResponse> getProcesoVigente()  {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.configuracionProcesoElectoralService.getProcesoVigente());
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping("/cargar-usuarios")
    public ResponseEntity<GenericResponse> cargarUsuarios(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestHeader("X-Tenant-Id") String tentat,
                                            @RequestBody CargarUsuarioAuthRequest request)  {

        GenericResponse genericResponse = new GenericResponse();
        try {
            LoginUserHeader user = getUserLogin(authorization);
            genericResponse = this.configuracionProcesoElectoralService.cargarUsuarios(tentat, user.getUsuario(), request.getClave());

        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setData("");
            genericResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);

    }

    @PostMapping("/principal")
    public ResponseEntity<GenericResponse> updatePrincipal(@RequestBody CargaDataRequestDTO request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  {

        GenericResponse response = new GenericResponse();
        LoginUserHeader user = getUserLogin(authorization);
        request.setUsuario(user.getUsuario());
        boolean respuesta = this.configuracionProcesoElectoralService.actualizarPrincipal(request);
        response.setSuccess(respuesta);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping("/list-tipo-eleccion-escrutinio")
    public ResponseEntity<GenericResponse> listTipoElecionEscrutinio(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                       @RequestBody ProcesoElectoralOtherRequestDTO request)  {

        GenericResponse genericResponse = new GenericResponse();
        try {
            LoginUserHeader user = getUserLogin(authorization);
            request.setUsuario(user.getUsuario());
            genericResponse.setData(this.configuracionProcesoElectoralService.listaTipoEleccionEscrutinio(request));
            genericResponse.setSuccess(Boolean.TRUE);

        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setData("");
            genericResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(genericResponse, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);

    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PutMapping("/update-tipo-eleccion-escrutinio")
    public ResponseEntity<GenericResponse> updateTipoDocumentoElectoralEscrutinio(@RequestBody DetalleTipoDocumentoEscrutinioRequest data, 
                                                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse genericResponse = new GenericResponse();
        try {
            genericResponse.setSuccess(Boolean.TRUE);
            LoginUserHeader user = getUserLogin(authorization);
            genericResponse.setData(this.escrutinioService.actualizarDocumentoElectoral(data.getDetalles(), user.getUsuario()));
        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(genericResponse, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping("/etapa")
    public ResponseEntity<GenericResponse> updateEtapa(@RequestBody CargaDataRequestDTO request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                       @RequestParam("isUpdate") boolean isUpdate)  {

        GenericResponse response = new GenericResponse();
        try{
            LoginUserHeader user = getUserLogin(authorization);
            request.setUsuario(user.getUsuario());
            this.configuracionProcesoElectoralService.actualizarEtapaProceso(request.getIdProceso(), isUpdate);
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

    }

}
