package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ControlCalidadActaPendiente {
	private Long idActa;
	private String mesa;
	private String copia;
	private String digitoChequeo;
	private String codigoEleccion;
	private String nombreEleccion;
	private Long cvas;
	private String ubigeoDepa;
	private String ubigeoProv;
	private String ubigeoNombre;
	private Long idArchivoActaEscrutinio;
	private Long idArchivoActaInstalacionSufragio;
}
