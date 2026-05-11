package pe.gob.onpe.scebackend.model.exportar.orc.dto;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportarRequestDto {

	private String cc;
	private String acronimo;
	
	public String toJson() {
		 Gson gson = new Gson();
		 String jsonString = gson.toJson(this);
		 return jsonString;
	}
	
}
