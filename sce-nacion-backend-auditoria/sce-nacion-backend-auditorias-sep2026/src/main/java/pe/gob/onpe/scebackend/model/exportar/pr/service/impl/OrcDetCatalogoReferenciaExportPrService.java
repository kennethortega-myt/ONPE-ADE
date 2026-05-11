package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.DetCatalogoReferenciaExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IOrcDetCatalogoReferenciaExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.service.IOrcDetCatalogoReferenciaExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoReferencia;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDetalleCatalogoReferenciaRepository;

@Service
public class OrcDetCatalogoReferenciaExportPrService implements IOrcDetCatalogoReferenciaExportPrService {

	@Autowired
	private OrcDetalleCatalogoReferenciaRepository catalogoReferenciaRepository;
	
	@Autowired
	private IOrcDetCatalogoReferenciaExportPrMapper catalogoReferenciaMapper;
	
	@Override
	public List<DetCatalogoReferenciaExportDto> findAll() {
		List<OrcDetalleCatalogoReferencia> catalogos = (List<OrcDetalleCatalogoReferencia>) this.catalogoReferenciaRepository.findAll();
		List<DetCatalogoReferenciaExportDto> dtos = null;
		if(!Objects.isNull(catalogos)) {
			dtos = catalogos.stream().map(
					entity ->  this.catalogoReferenciaMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}

}
