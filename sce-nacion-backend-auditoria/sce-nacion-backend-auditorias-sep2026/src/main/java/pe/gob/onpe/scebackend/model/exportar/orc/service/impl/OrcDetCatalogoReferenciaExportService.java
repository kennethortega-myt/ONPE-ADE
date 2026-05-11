package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDetCatalogoReferenciaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IOrcDetCatalogoReferenciaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IOrcDetCatalogoReferenciaExportService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoReferencia;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDetalleCatalogoReferenciaRepository;

@Service
public class OrcDetCatalogoReferenciaExportService implements IOrcDetCatalogoReferenciaExportService {

	@Autowired
	private OrcDetalleCatalogoReferenciaRepository catalogoReferenciaRepository;
	
	@Autowired
	private IOrcDetCatalogoReferenciaExportMapper catalogoReferenciaMapper;
	
	@Override
	public List<OrcDetCatalogoReferenciaExportDto> findAll() {
		List<OrcDetalleCatalogoReferencia> catalogos = (List<OrcDetalleCatalogoReferencia>) this.catalogoReferenciaRepository.findAll();
		List<OrcDetCatalogoReferenciaExportDto> dtos = null;
		if(!Objects.isNull(catalogos)) {
			dtos = catalogos.stream().map(
					entity ->  this.catalogoReferenciaMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
	

}
