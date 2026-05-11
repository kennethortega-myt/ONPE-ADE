package pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;

import java.util.List;

@Data
public class VotoPreferencialContext {

  private Integer nEstado;
  private Acta cabActa;
  private DetActa detActa;
  private String estadoActaActual;
  private String usuario;
  private List<DetActaPreferencial> detActaPreferencialList;
  private long totalVotosAgrupaciones;
  private String propOrdenVerificacionActas;
}
