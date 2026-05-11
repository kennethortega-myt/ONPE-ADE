package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;
import java.util.Optional;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;

import org.springframework.stereotype.Service;

@Service
public class CentroComputoServiceImpl implements CentroComputoService {

	private final CentroComputoRepository centroComputoRepository;

	public CentroComputoServiceImpl(CentroComputoRepository centroComputoRepository) {
		this.centroComputoRepository = centroComputoRepository;
	}

	@Override
	public void save(CentroComputo centroComputo) {
		this.centroComputoRepository.save(centroComputo);
	}

	@Override
	public void saveAll(List<CentroComputo> k) {
		this.centroComputoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.centroComputoRepository.deleteAll();
	}

	@Override
	public List<CentroComputo> findAll() {
		return this.centroComputoRepository.findAll();
	}

	@Override
	public Optional<CentroComputo> findByCodigo(String codigo) {
		return this.centroComputoRepository.findByCodigo(codigo);
	}

	@Override
	public Optional<CentroComputo> getCentroComputoActual() {
		List<CentroComputo> centros = centroComputoRepository.findAll();
		String codigoNacion = "000";
		Optional<CentroComputo> optionalCentroComputo = centros.stream()
				.filter(c -> !c.getCodigo().contains(codigoNacion)).findFirst();
		return optionalCentroComputo;
	}
}
