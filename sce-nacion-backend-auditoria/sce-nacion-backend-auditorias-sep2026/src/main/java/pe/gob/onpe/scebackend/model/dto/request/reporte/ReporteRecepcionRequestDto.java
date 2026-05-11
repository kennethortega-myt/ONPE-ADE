package pe.gob.onpe.scebackend.model.dto.request.reporte;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteRecepcionRequestDto extends ReporteBaseRequestDto{

	private String proceso;
	private String centroComputo;
	private Date fechaInicial;
    private Date fechaFin;
}
