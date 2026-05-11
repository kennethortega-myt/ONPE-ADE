package pe.gob.onpe.scebackend.model.dto.request.reporte;

import org.hibernate.query.TypedParameterValue;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TypeParametersResultadoContabilizadas {

	private TypedParameterValue idEleccion;
	private TypedParameterValue centroComputo;
	private TypedParameterValue ambito;
	private TypedParameterValue ubigeo;
	private String esquema;
	private Integer tipoReporte;
	private String usuario;
}
