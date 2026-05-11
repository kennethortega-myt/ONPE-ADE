package pe.gob.onpe.sceorcbackend.model.dto.response.reporte;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AsistenciaMmEscrutinioResponseDto extends AsistenciaBaseResponseDto{


	private String sexo;

}