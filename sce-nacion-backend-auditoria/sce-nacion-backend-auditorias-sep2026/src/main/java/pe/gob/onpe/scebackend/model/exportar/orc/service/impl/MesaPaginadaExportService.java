package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IMesaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IMesaPaginadaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Mesa;
import pe.gob.onpe.scebackend.model.orc.repository.MesaRepository;

@Service
@AllArgsConstructor
public class MesaPaginadaExportService implements IMesaPaginadaExportService {
	
	private final IMesaExportMapper mesaMapper;
	
	private final MesaRepository mesaRepository;
	
	@Override
	@Transactional(
		    transactionManager = "tenantTransactionManager",
		    readOnly = true
		)
	public PaginaOptDto<MesaExportDto> importarMesas(String cc, Long lastId, int tamanoPagina) {
		Pageable limit = PageRequest.of(0, tamanoPagina);
		List<Mesa> mesas = this.mesaRepository.importar(cc, lastId, limit);
		PaginaOptDto<MesaExportDto> dto = new PaginaOptDto<>();
        dto.setData(mesas.stream().map(this::toDto).toList());
        dto.setNext(mesas.size() == tamanoPagina);
        Long nextLastId = mesas.isEmpty() ? lastId : mesas.get(mesas.size()-1).getId();
        dto.setLastId(nextLastId);
        return dto;
	}
	
	private MesaExportDto toDto(Mesa entity){
		return this.mesaMapper.toDto(entity);
	}

	@Override
	public int contar(String cc) {
		return this.mesaRepository.contar(cc);
	}

	
	
}
