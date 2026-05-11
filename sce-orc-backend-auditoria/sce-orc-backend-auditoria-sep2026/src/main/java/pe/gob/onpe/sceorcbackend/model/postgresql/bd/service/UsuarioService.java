package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.usuario.UsuarioUpdateRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.usuario.UsuarioResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario.UsuarioFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;

public interface UsuarioService extends CrudService<Usuario> {
  Usuario findByUsername(String usuario);

  UsuarioResponseDto update(Usuario usuario, UsuarioUpdateRequestDto usuarioDto, TokenInfo tokenInfo);

  void updateDesincronizado(Long usuarioId, Boolean desincronizadoSasa);

  List<DetCatalogoEstructuraDTO> listPerfilesCentroComputo();

  List<DetCatalogoEstructuraDTO> listTiposDocumento();

  GenericResponse<String> cerrarSession(String usuario);

  SearchFilterResponse<UsuarioResponseDto> listPaginted(String usuario, Integer page, Integer size);

  SearchFilterResponse<UsuarioResponseDto> listPaginatedAll(UsuarioFilter filter, Integer page, Integer size);

  void resetSesionActiva(String id, TokenInfo tokenInfo);
  
  boolean estaActivo(String username);

  List<Usuario> usuarioActivos() ;

  Long contarOtrosUsuarioActivos(String nombreUsuario);

  boolean validarSessionActiva(String nombreUsuario);

  UsuarioResponseDto convertirUsuarioDto(Usuario usuario);

  byte[] getReporteUsuariosPdf(UsuarioFilter filter, Integer page, Integer size, String user) throws JRException;

    List<UsuarioResponseDto> listReportAll(UsuarioFilter filter);
    List<Usuario> listUsuariosByPerfil(String perfil);
}
