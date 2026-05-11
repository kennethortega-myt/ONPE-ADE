package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PuestaCero;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.AutorizacionDto;
import java.util.List;

public interface PuestaCeroService extends CrudService<PuestaCero> {


    void puestaCeroGeneral(TokenInfo tokenInfo, String idAutorizacion);

    byte[] reportePuestaCeroCentroComputo(String acronimoProceso, String centroComputo, String ncc, String usuario);

    GenericResponse<TabAutorizacion> registrarAutorizacion(String codCentroComputo,String usuarioPC, String tipoAutorizacion, String proceso);

    GenericResponse<TabAutorizacion> aprobarAutorizacion(Long idAutorizacion, String usuarioPC);
    GenericResponse<TabAutorizacion> rechazarAutorizacion(Long idAutorizacion, String usuarioPC);

    GenericResponse<List<AutorizacionDto>> listarAutorizaciones();

    int realizarPuestaCeroCentroComputo();

    GenericResponse<Boolean> confirmarPuestaCeroDesdeCC(TokenInfo tokenInfo);

    int desactivarPuestaCeroMasivo();
}
