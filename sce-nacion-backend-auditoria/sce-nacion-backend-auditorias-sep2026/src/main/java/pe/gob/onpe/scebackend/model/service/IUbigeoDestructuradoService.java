package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.orc.entities.UbigeoDestructurado;

public interface IUbigeoDestructuradoService {

	List<UbigeoDestructurado> getUbigeoNivel3(Long idEleccion);
	List<UbigeoDestructurado> getUbigeoNivel2(Long idUbigeoPadre, Long idEleccion);
	List<UbigeoDestructurado> getUbigeoNivel1(Long idUbigeoPadre, Long idEleccion);

}
