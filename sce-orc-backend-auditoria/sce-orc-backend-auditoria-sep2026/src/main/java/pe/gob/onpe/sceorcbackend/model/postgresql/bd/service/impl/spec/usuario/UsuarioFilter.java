package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario;

import pe.gob.onpe.sceorcbackend.model.dto.BaseFilter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UsuarioFilter extends BaseFilter {
  private String acronimoProceso;
  private String centroComputo;
  private String apellidoPaterno;
  private String apellidoMaterno;
  private String documento;
  private String nombres;
  private String perfil;
  private String usuario;
  private Integer personaAsignada;
  private Integer desincronizadoSaza;
}
