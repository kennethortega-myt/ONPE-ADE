package pe.gob.onpe.scebackend.sasa.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUsuarioOutputDto {
	private Integer idUsuario;
	private Integer claveNueva;
	private String nombres;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String numeroDocumento;
	private Integer tipoDocumento;
	private Integer tipoAutenticacion;
	private String token;
	private Integer superAdmin;
	
	private Integer tipoGeneracion;
	private Integer personaAsignada;
	
	private Integer idAmbitoElectoral;
	private Integer tipoAmbito;
	private String nombreAmbito;
	private String abreviaturaAmbito;
	private Integer idUnidadOrganica;
	private String siglasUnidadOrganica;
	private String acronimoProceso;
	
	private Integer idProcesoElectoral;
	private String nombreProceso;
	private Integer tipoProceso;
	private String nombreCentroComputo;
	private String codigoCentroComputo;
}
