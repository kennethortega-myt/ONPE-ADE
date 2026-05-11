package pe.gob.onpe.scebackend.model.exportar.orc.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsuarioExportDto {

	private Long	id;
	private String nombreUsuario;
	private Integer tipoDocumentoIdentidad;
	private String documentoIdentidad;
	private String perfil;
	private String centroComputo;
	private Integer sesionActiva;
	private Integer actasAsignadas;
	private Integer actasAtendidas;
	private Integer activo;
	private Integer idUsuario;
	private String  acronimoProceso;
	private String  nombreCentroComputo;
	private String  clave;
	private Integer idPerfil;
	private Integer claveTemporal;
	private String	nombres;
	private String	correos;
	private boolean desincronizadoSasa;
	private String  apellidoPaterno;
	private String  apellidoMaterno;
	private Integer	personaAsignada;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
