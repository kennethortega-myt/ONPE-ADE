package pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;

import java.util.List;

@Getter
@AllArgsConstructor
public class EleccionYActasResult {
  private final DetTipoEleccionDocumentoElectoral configEleccion;
  private final String eleccionSeleccionada;
  private final List<Acta> listaActas;
  private final String nroActa;
  private final String copia;
  private final String nroCopiaDig;
}