package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.entities.Catalogo;

import java.util.Map;

public interface ICatalogoService {

    Catalogo getCatalogoByTabla (String table);
    Object listaCalogos(String tablaReferencia);

    Map<String, Boolean> ejecutarEstructuras(String esquema, String usuario);
}
