package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

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
