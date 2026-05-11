package pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar;

import lombok.Data;

@Data
public class AuxEstadoActa {
  private boolean anularActaPorExtravio;
  private boolean anularActaPorSiniestro;
  private boolean actaPorReproceso;

  public AuxEstadoActa(boolean extravio, boolean siniestro, boolean reproceso) {
    this.anularActaPorExtravio = extravio;
    this.anularActaPorSiniestro = siniestro;
    this.actaPorReproceso = reproceso;
  }
}
