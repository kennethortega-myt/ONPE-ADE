package pe.gob.onpe.sceorcbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.PadronElectoralBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.PadronElectoralResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PadronElectoralService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("/padron-electoral")
@RequiredArgsConstructor
public class PadronElectoralController {

    private final PadronElectoralService padronElectoralService;

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/buscar-electores")
    public ResponseEntity<GenericResponse<Page<PadronElectoralResponse>>> buscarElectores(
            @RequestBody PadronElectoralBusquedaDto criterios,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<PadronElectoralResponse> resultados =
                    padronElectoralService.buscarElectores(criterios, page, size);
            return ResponseHelperException.createSuccessResponse("Operación realizada con éxito", resultados);
        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e, "PadronElectoralController.buscarElectores");
        }
    }
}
