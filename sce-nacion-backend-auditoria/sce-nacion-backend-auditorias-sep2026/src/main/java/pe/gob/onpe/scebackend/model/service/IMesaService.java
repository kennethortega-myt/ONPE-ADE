package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.orc.entities.Mesa;

public interface IMesaService {

    Mesa findByCodigo(String mesa);

    List<Mesa> findByCodigoIn(List<String> codigos);

}
