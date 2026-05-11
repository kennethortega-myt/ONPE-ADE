package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class RegistroMesaBaseDTO {
  private Long mesaId;
  private String type;
  private String mesa;
  private Integer electoresHabiles;
  private Integer electoresAusentes;
  private Integer electoresOmisos;
  private String localVotacion;
  private String ubigeo;
  private String departamento;
  private String provincia;
  private String distrito;
  private Long actaId;
  private Long archivoEscrutinioId;
  private Long archivoInstalacionId;
}
