package pe.gob.onpe.sceorcbackend.model.dto.response.reporte;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConteoMesaResponseDto{
	public int participaron;
	public int omiso;

	public ConteoMesaResponseDto(int participaron, int omiso) {
		this.participaron = participaron;
		this.omiso = omiso;
	}
}