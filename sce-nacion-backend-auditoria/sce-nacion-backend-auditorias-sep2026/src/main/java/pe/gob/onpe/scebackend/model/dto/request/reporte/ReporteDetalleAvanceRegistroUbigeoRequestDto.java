package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ReporteDetalleAvanceRegistroUbigeoRequestDto extends ReporteBaseRequestDto {
    @Alphanumeric
    private String proceso;
    @Alphanumeric
    private String ubigeo;
    @Alphanumeric
    private String ubigeoNivelUno;
    @Alphanumeric
    private String ubigeoNivelDos;
    @Alphanumeric
    private String ubigeoNivelTres;
}
