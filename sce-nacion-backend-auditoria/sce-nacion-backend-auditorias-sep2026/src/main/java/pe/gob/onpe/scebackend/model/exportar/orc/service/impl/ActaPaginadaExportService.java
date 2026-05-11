package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IActaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IActaPaginadaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;

@Service
@AllArgsConstructor
public class ActaPaginadaExportService implements IActaPaginadaExportService {

	private final IActaExportMapper actaMapper;
	
	private final ActaRepository actaRepository;
	
	@Override
	@Transactional(
		    transactionManager = "tenantTransactionManager",
		    readOnly = true
		)
	public PaginaOptDto<CabActaExportDto> importarActas(String cc, Long lastId, int tamanoPagina) {
		Pageable limit = PageRequest.of(0, tamanoPagina);
		List<Acta> actas = this.actaRepository.importar(cc, lastId, limit);
		PaginaOptDto<CabActaExportDto> dto = new PaginaOptDto<>();
        dto.setData(actas.stream().map(this::toDto).toList());
        dto.setNext(actas.size() == tamanoPagina);
        Long nextLastId = actas.isEmpty() ? lastId : actas.get(actas.size()-1).getId();
        dto.setLastId(nextLastId);
        return dto;
	}
	
	private CabActaExportDto toDto(Acta entity){
		return this.actaMapper.toDto(entity);
	}

	@Override
	public int contar(String cc) {
		return this.actaRepository.contar(cc);
	}
	
}
