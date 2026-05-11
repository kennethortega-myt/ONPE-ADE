package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteMesasEstadoMesaRequestDto extends ReporteRequestBaseDto {

    @Alphanumeric
    private String codigoEstadoMesa;
    @Alphanumeric
    private String idEstadoMesa;
    private String estadoMesa;
    private Integer ubigeoNivelUno;
    private Integer ubigeoNivelDos;
    private Integer ubigeoNivelTres;
    @NotNull(message = "Tipo de Reporte es requerido")
    private Integer tipoReporte;
    private boolean porDistrito;
}
