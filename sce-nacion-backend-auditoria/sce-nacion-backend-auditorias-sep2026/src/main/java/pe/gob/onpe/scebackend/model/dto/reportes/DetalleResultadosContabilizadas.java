package pe.gob.onpe.scebackend.model.dto.reportes;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleResultadosContabilizadas {

	private Integer numeroAp;
	private String codigoAp;
	private String agrupacionPolitica; 
	private BigInteger cantidadVotos; 
	private Double votosValidados;
	private Double votosEmitidos;
	private Integer[] votosPreferenciales;
	
	//Para CPR
    private Integer votosSi;
    private Integer votosNo;
    private Integer votosBlancos;
    private Integer votosNulos;
    private Integer ciudadanosVotaron;
    private Integer votosSiNo2;
	
}
