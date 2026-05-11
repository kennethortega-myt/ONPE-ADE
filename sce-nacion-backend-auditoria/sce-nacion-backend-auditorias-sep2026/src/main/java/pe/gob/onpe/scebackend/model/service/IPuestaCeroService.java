package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.FiltroPuestaCeroDTO;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroResponseDto;


public interface IPuestaCeroService {

    PuestaCeroResponseDto puestaCero(String esquema, Integer idCentroComputo, String usuario,
                                     Integer resultado,
                                     String acronimo);

    byte[] reportePuestaCero(FiltroPuestaCeroDTO filtro, String centroComputo, String ncc, String usuario);

    void activarBloqueo(String usuario);
}
