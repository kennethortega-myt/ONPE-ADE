package pe.gob.onpe.scebackend.model.service;


import java.util.List;

import pe.gob.onpe.scebackend.model.dto.ParametroConexionFiltroDto;
import pe.gob.onpe.scebackend.model.dto.PingConexionDto;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;

public interface ICentroComputoService extends CrudService<CentroComputo> {

    CentroComputo getCentroComputoByPk(Long id);

    CentroComputo getPadreNacion();
    
    List<CentroComputo> findAll();
    
    String generarToken(Integer byteLength);
    
    List<CentroComputo> findAll(ParametroConexionFiltroDto dto);
    
    boolean ping(PingConexionDto dto);

}

