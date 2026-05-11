package pe.gob.onpe.sceorcbackend.model.dto.response.reporte;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AsistenciaBaseResponseDto {

  private String codDescODPE;
  private String codDescCC;
  private String numMesaMadre;
  private String descDepartamento;
  private String descDistrito;
  private String descProvincia;
  private String codUbigeo;
  private Integer eleHabil;
  private String numEle;
  private String votante;
  private String descargo;
  private String numero;


}
