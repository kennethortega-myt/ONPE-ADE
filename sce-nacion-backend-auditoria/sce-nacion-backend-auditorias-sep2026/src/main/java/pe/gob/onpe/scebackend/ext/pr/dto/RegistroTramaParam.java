package pe.gob.onpe.scebackend.ext.pr.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistroTramaParam {

	String 	esquema;
	boolean ejecutarPs;
	TipoEjecProcEnum tipoEjecProc;
	Long    idTransmision;
	Long 	idActa;
	Integer idDetUbigeoEleccion;
	String 	estadoActa; 
	String 	estadoComputo; 
	String 	estadoActaResolucion; 
	String 	audUsuarioCreacion;
	Integer resultado;
	String  mensaje;
	
}
