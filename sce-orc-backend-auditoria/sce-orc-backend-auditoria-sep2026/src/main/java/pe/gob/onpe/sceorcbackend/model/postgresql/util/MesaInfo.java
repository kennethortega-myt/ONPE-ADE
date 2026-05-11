package pe.gob.onpe.sceorcbackend.model.postgresql.util;


import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;

import java.util.List;

@Data
public class MesaInfo {
  private Mesa mesa;
  private List<Acta> actaList;
  private Acta actaPrincipal;
}
