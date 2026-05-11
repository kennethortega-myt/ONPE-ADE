package pe.gob.onpe.sceorcbackend.model.postgresql.util;

import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;

@Data
@Builder
public class ActaInfo {
  private Acta acta;
  private Mesa mesa;
  private String codigoEleccion;
  private String nombreEleccion;
  private BarCodeInfo barCodeInfo;
  DetTipoEleccionDocumentoElectoral configAe;
  DetTipoEleccionDocumentoElectoral configAis;
}
