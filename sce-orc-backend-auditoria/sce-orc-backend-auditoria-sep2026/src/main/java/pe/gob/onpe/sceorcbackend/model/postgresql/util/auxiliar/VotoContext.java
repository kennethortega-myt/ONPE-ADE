package pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import java.util.ArrayList;
import java.util.List;

@Data
public class VotoContext {

  private Acta cabActa;
  private String codigoEleccion;
  private String estadoActaActual;
  private String usuario;
  private Long idDetUbigeoEleccion;
  private List<DetActa> detActaListToErrores = new ArrayList<>();
  private List<DetActaPreferencial> detActaPreferencialListToErrores = new ArrayList<>();
  private List<DetActaOpcion> detActaOpcionesListToErrores = new ArrayList<>();
  private String propOrdenVerificacionActas;
}
