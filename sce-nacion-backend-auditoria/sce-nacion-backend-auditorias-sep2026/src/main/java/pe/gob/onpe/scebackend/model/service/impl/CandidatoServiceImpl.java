package pe.gob.onpe.scebackend.model.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.impl.CandidatoExportService;
import pe.gob.onpe.scebackend.model.service.ICandidatoService;

@RequiredArgsConstructor
@Service
public class CandidatoServiceImpl implements ICandidatoService {

	private final CandidatoExportService candidatoExportService;
	
	@Override
	@Transactional("locationTransactionManager")
	public List<CandidatoExportDto> listarCandidatosPorCc(String cc) {
		return this.candidatoExportService.findByCc(cc);
	}

}
