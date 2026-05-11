package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import java.util.Date;
import lombok.Data;

@Data
public class DigitizationListActasItem {
  private Long actaId;
  private String mesa;
  private String estado;
  private String ubigeo;
  private String esActaDeConsulado;

  private Long acta1FileId;
  private String acta1Status;
  private Long digitalizacionEscrutinio;
  private String observacionDigtalAe;
  private String nroActaEscrutinio;

  private Long acta2FileId;
  private String acta2Status;
  private Long digitalizacionInstalacionSufragio;
  private String observacionDigtalAis;
  private String nroActaInstalacionSufragio;

  private Date fecha;
}
