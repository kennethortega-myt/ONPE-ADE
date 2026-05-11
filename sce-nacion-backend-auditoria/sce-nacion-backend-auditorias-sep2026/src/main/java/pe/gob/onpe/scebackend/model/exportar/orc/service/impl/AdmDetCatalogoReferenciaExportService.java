package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.entities.DetalleCatalogoReferencia;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoReferenciaExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IAdmDetCatalogoReferenciaExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IAdmDetCatalogoReferenciaExportService;
import pe.gob.onpe.scebackend.model.repository.DetalleCatalogoReferenciaRepository;

@Service
public class AdmDetCatalogoReferenciaExportService implements IAdmDetCatalogoReferenciaExportService {

	@Autowired
	private DetalleCatalogoReferenciaRepository catalogoReferenciaRepository;
	
	@Autowired
	private IAdmDetCatalogoReferenciaExportMapper catalogoReferenciaMapper;
	
	@Override
	public List<AdmDetCatalogoReferenciaExportDto> findAll() {
		List<DetalleCatalogoReferencia> catalogos = (List<DetalleCatalogoReferencia>) this.catalogoReferenciaRepository.findAll();
		List<AdmDetCatalogoReferenciaExportDto> dtos = null;
		if(!Objects.isNull(catalogos)) {
			dtos = catalogos.stream().map(
					entity ->  this.catalogoReferenciaMapper.toDto(entity)).collect(Collectors.toList());
		}
		return dtos;
	}
	
	

}
