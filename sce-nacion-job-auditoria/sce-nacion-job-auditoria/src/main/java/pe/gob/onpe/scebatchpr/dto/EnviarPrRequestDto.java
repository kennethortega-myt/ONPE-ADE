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
public class EnviarPrRequestDto implements Serializable  {

	private static final long serialVersionUID = -4316138719909050589L;
	
	private String proceso;
	
}
