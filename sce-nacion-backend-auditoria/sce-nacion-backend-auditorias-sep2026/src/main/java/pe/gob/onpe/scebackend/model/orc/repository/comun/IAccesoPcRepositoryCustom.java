package pe.gob.onpe.scebackend.model.orc.repository.comun;

import java.util.Map;

public interface IAccesoPcRepositoryCustom {
    Map<String, Object> executeRegistrarAccesoPc(String piEsquema, String piUsuario, String piIp);
}
