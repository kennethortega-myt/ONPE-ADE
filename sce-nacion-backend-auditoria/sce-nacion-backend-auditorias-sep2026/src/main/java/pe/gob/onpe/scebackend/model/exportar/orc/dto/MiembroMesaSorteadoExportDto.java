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
public class MiembroMesaSorteadoExportDto {

	private Long	id;
	private Long    idMesa;
	private Long    idPadronElectoral;
	private Integer cargo;
	private Integer bolo;
	private String  direccion;
	private Integer turno;
	private Integer estado;
	private Integer asistenciaAutomatico;
	private Integer asistenciaManual;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
