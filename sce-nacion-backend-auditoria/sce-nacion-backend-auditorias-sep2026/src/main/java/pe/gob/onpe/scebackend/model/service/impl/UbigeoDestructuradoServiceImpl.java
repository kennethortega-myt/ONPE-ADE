package pe.gob.onpe.scebackend.model.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.orc.entities.UbigeoDestructurado;
import pe.gob.onpe.scebackend.model.orc.repository.UbigeoDestructuradoRepository;
import pe.gob.onpe.scebackend.model.service.IUbigeoDestructuradoService;

@RequiredArgsConstructor
@Service
public class UbigeoDestructuradoServiceImpl implements IUbigeoDestructuradoService {

	private final UbigeoDestructuradoRepository ubigeoDestructuradoRepository;
	
	@Override
	@Transactional("locationTransactionManager")
	public List<UbigeoDestructurado> getUbigeoNivel3(Long idEleccion) {
		return this.ubigeoDestructuradoRepository.getUbigeoNivel3(idEleccion);
	}

	@Override
	@Transactional("locationTransactionManager")
	public List<UbigeoDestructurado> getUbigeoNivel2(Long idUbigeoPadre, Long idEleccion) {
		return this.ubigeoDestructuradoRepository.getUbigeoNivel2(idUbigeoPadre, idEleccion);
	}

	@Override
	@Transactional("locationTransactionManager")
	public List<UbigeoDestructurado> getUbigeoNivel1(Long idUbigeoPadre,Long idEleccion) {
		return this.ubigeoDestructuradoRepository.getUbigeoNivel1(idUbigeoPadre, idEleccion);
	}


}
