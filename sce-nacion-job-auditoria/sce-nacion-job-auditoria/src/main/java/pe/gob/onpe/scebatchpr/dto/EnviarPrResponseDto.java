package pe.gob.onpe.scebatchpr.dto;

import java.io.Serializable;

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
public class EnviarPrResponseDto implements Serializable  {
	
	private static final long serialVersionUID = -9019149242414030793L;
	private boolean success;
	private String message;

}
