package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumenActasContabilizadas {

	private Integer electoresHabiles;
	private Integer mesasAinstalar;
	private Integer mesasInstaladas;
	private Integer mesasNoInstaladas;
	private Integer mesasPorProcesar;
	private Integer mesasHabiles;
	private Integer actasProcesadas;
	
	private Integer impugnados;
	private Integer nulidad;
	private Integer errorMaterial;
	private Integer ilegible;
	private Integer sinDatos;
	private Integer otrasObservaciones;
	private Integer contabilizadasNormal;
	private Integer actasNoInstalada;
	
	private Integer enDigitacion;
	private Integer pendiente;
	private Integer extraviada;
	private Integer anulada;
	private Integer incompleta;
	private Integer sinFirma;
	private Integer siniestrada;
	
	private Double actasProcesadasPorcentaje;
	private Double actasPorProcesarPorcentaje;
	private Double porcentajeAvance;
	
}
