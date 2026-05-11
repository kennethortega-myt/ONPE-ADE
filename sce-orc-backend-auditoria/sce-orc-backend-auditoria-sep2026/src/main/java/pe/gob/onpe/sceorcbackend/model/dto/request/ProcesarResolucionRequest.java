package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.SeguimientoOficioDTO;

@Getter
@Setter
public class ProcesarResolucionRequest {
    private SeguimientoOficioDTO seguimiento;
    private String accion;
    private String observacion;
}
