package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteProcedePagoDto {
    private Integer nro;
    private String numeroMesa;
    private String numeroDocumento;
    private String votante;
    private String cargo;
    private String procedePago;
}
