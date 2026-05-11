package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportPadronElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportPadronElectoralService;

@Service
public class ImportPadronElectoralServiceImpl implements ImportPadronElectoralService  {
	
	private final ImportPadronElectoralRepository padronElectoralRepository;

	public ImportPadronElectoralServiceImpl(ImportPadronElectoralRepository padronElectoralRepository){
	     this.padronElectoralRepository = padronElectoralRepository;
	}

	@Override
	public List<ImportPadronElectoral> findPadronElectoralByCodigoMesaOrderByOrden(String codigoMesa) {
		return padronElectoralRepository.findPadronElectoralByCodigoMesaOrderByOrden(codigoMesa);
	}

	@Override
	public void save(ImportPadronElectoral k) {
		this.padronElectoralRepository.save(k);
	}

	@Override
	public void saveAll(List<ImportPadronElectoral> k) {
		this.padronElectoralRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.padronElectoralRepository.deleteAll();
	}

	@Override
	public List<ImportPadronElectoral> findAll() {
		return this.padronElectoralRepository.findAll();
	}
	
	@Override
	@Transactional
	public void truncateTable() {
		this.padronElectoralRepository.truncateTable();
	}

}
