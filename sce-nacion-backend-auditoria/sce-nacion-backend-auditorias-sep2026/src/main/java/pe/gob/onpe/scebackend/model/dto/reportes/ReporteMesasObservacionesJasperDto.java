package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Data;

@Data
public class ReporteMesasObservacionesJasperDto {

    private String observacion;
    private String observacionMM;
    private String numeroMesa;
    private String descDepartamento;
    private String descProvincia;
    private String descDistrito;
    private String codiDesCompu;
    private String codiUbigeo;
    private String nombreDocumento;

}
