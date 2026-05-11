package pe.gob.onpe.sceorcbackend.model.dto.response;

import java.util.List;

public class ListarAdminDocumentoElectoralResponse extends GenericResponse<List<AdminDocumentoElectoralDto>> {


  public ListarAdminDocumentoElectoralResponse(boolean success, String message, List<AdminDocumentoElectoralDto> data) {
    super(success, message, data);
  }

}
