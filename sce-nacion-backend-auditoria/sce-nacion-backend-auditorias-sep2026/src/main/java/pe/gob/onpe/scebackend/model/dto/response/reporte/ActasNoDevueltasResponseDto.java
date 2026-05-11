package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActasNoDevueltasResponseDto extends ReporteBaseDto{

	private String numFila;
	private String lote;
	private String autoridad;
	private String acta;
	private String nomTipoEleccion;
	private String errorMaterial;
	private String tipoErrorM;
	private String actaSinFirma;
	private String votosImpugnados;
	private String ilegibilidad;
	private String actasIncompletas;
	private String solNulidad;
	private String actaSinDatos;
	private String extraviada;
	private String tipoIlegible;
	private String detalleIlegible;
	private String observacion;
	private String centroComputo;
	private String odpe;
}
