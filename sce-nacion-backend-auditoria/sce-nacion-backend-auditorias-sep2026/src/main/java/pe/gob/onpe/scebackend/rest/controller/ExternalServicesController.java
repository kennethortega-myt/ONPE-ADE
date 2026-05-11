package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.CoordenadasRequestDTO;
import pe.gob.onpe.scebackend.model.service.IExternalApiService;

@RestController
@RequestMapping("/externalApi")
public class ExternalServicesController {

    private final IExternalApiService externalApiService;

    public ExternalServicesController(IExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @PostMapping("/relative-coordena")
    public ResponseEntity<Object> registroCoordenada(@RequestBody CoordenadasRequestDTO requestDTO){
        try {
            return new ResponseEntity<>(this.externalApiService.computeRelativeCoordinates(requestDTO), HttpStatus.OK);
        } catch (Exception ex) {

            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
