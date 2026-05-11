package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.scebackend.model.dto.request.DetalleTipoEleccionDocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.FileStorageResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ICatalogoService;
import pe.gob.onpe.scebackend.model.service.IDetalleTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.scebackend.model.service.IFileStorageService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/detalleTipoEleccionDocumentoElectoral")
public class DetalleTipoEleccionDocumentoElectoralController extends BaseController {

    private final IDetalleTipoEleccionDocumentoElectoralService detalleTipoProcesoDocumentoElectoralService;

    private final IFileStorageService fileStorageService;

    private final ICatalogoService catalogoService;

    public DetalleTipoEleccionDocumentoElectoralController(TokenDecoder tokenDecoder, IDetalleTipoEleccionDocumentoElectoralService detalleTipoProcesoDocumentoElectoralService, IFileStorageService fileStorageService, ICatalogoService catalogoService) {
        super(tokenDecoder);
        this.detalleTipoProcesoDocumentoElectoralService = detalleTipoProcesoDocumentoElectoralService;
        this.fileStorageService = fileStorageService;
        this.catalogoService = catalogoService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> guardar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,@RequestBody List<DetalleTipoEleccionDocumentoElectoralRequestDto> detalle){
        GenericResponse genericResponse = new GenericResponse();
       LoginUserHeader user =  getUserLogin(authorization) ;
        if(!detalle.isEmpty()){
            detalle.forEach(val->this.detalleTipoProcesoDocumentoElectoralService.guardarDetalle(val,user.getUsuario()));
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage("Se guardó la Información con éxito");
        }
        return new ResponseEntity<>(genericResponse,HttpStatus.CREATED);
    }
    
    @GetMapping("/{idTipo}")
    public ResponseEntity<GenericResponse> detalleTipoDocumentoElectoralByTipo(@PathVariable("idTipo") Integer tipoELeccion, @RequestParam(name = "metodo", required = false) Integer metodo){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        if(Objects.isNull(metodo)){
            genericResponse.setData(this.detalleTipoProcesoDocumentoElectoralService.obtenerDetalleByTipo(tipoELeccion, false));
        }else if(metodo == 2){
            genericResponse.setData(this.detalleTipoProcesoDocumentoElectoralService.obtenerDetalleByTipoPaso2(tipoELeccion));
        }else if(metodo == 3){
            genericResponse.setData(this.detalleTipoProcesoDocumentoElectoralService.obtenerDetalleByTipoPaso2Config());
        }
        
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PostMapping("/archivo")
    ResponseEntity<GenericResponse> updateArchivo(@Valid @RequestParam("idDetalle") Integer idDetalle
            , @Valid @RequestParam("file") MultipartFile file,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws IOException {
        GenericResponse response = new GenericResponse();
        response.setSuccess(Boolean.TRUE);
        FileStorageResponseDTO fileS = fileStorageService.save("", file);
        detalleTipoProcesoDocumentoElectoralService.actualizarArchivo(idDetalle, fileS.getIdentificador());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/catalogos")
    public ResponseEntity<GenericResponse> listCatalogos(){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.catalogoService.listaCalogos("det_configuracion_documento_electoral"));

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @GetMapping("/listDocumentoElectoralConfigGeneral")
    public ResponseEntity<GenericResponse> detalleTipoDocumentoElectoralConfigGeneral(){
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(this.detalleTipoProcesoDocumentoElectoralService.obtenerDetalleByTipo(0, true));

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }


}
