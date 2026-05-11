package pe.gob.onpe.scebackend.model.dto.transmision;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArchivoFirmaDto {

	private Long idActa;
	private Integer tipoArchivo;
	private ArchivoTransmisionDto archivo;
}
