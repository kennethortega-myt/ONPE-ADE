package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConteoMesaResponseDto {

	private int participaron;
	private int omiso;

	public ConteoMesaResponseDto(int participaron, int omiso) {
		this.participaron = participaron;
		this.omiso = omiso;
	}
	
	public void incrementarParticipacion() {
		this.participaron++;
	}
	
	public void incrementarOmiso() {
		this.omiso++;
	}
}
