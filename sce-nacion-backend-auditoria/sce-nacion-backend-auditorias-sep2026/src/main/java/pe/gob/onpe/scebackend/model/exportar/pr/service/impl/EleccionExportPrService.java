package pe.gob.onpe.scebackend.model.exportar.pr.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.pr.service.IEleccionExportPrService;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.entities.VwPrEleccion;
import pe.gob.onpe.scebackend.model.orc.repository.EleccionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.VwPrDistritalRepository;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.EleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.EleccionViewExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrEleccionExportDto;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IEleccionExportPrMapper;
import pe.gob.onpe.scebackend.model.exportar.pr.mapper.IVwPrEleccionExportPrMapper;

@Service
public class EleccionExportPrService implements IEleccionExportPrService {

	@Autowired
	private EleccionRepository eleccionRepository;
	
	@Autowired
	private VwPrDistritalRepository viewEleccionResultRepository;
	
	@Autowired
	private IEleccionExportPrMapper eleccionMapper;
	
	@Autowired
	private IVwPrEleccionExportPrMapper vistaMapper;
	
	@Override
	public EleccionViewExportDto findAll(Integer flag) {
		
		List<Eleccion> entities = this.eleccionRepository.findAll();
		
		EleccionViewExportDto rpta = new EleccionViewExportDto();
		
		List<EleccionExportDto> dtos = null;
		if(!Objects.isNull(entities)) {
			dtos = entities.stream().map(
					entity ->  this.eleccionMapper.toDto(entity)).collect(Collectors.toList());
		}
		
		rpta.setElecciones(dtos);
		
		for(EleccionExportDto eleccion : rpta.getElecciones()) {
			if (eleccion.getNombreVista()!= null && eleccion.getNombreVista().endsWith("_orig")) {
                String nombreVista = eleccion.getNombreVista().substring(0, eleccion.getNombreVista().length() -5);
                eleccion.setNombreVista(nombreVista);
            }
		}
		
		if(flag!=null && flag.equals(1)) {
			rpta.setVistas(getVistas(entities));
		}

		return rpta;
	}

	private Map<String, List<VwPrEleccionExportDto>> getVistas(List<Eleccion> elecciones) {
		Map<String, List<VwPrEleccionExportDto>> results = new HashMap<>();
		List<VwPrEleccion> resultado = null;
		List<VwPrEleccionExportDto> resultadoDto = null;
		if(elecciones!=null) {
			for(Eleccion e:elecciones) {
				  String nombreVista = e.getNombreVista();
				if(e.getNombreVista()!=null && !e.getNombreVista().isEmpty() && results.get(e.getNombreVista())==null) {
					 if (nombreVista.endsWith("_orig")) {
		                    nombreVista = nombreVista.substring(0, nombreVista.length() -5);
		                    e.setNombreVista(nombreVista);
		                }
						resultado = viewEleccionResultRepository.findByVista(e.getNombreVista());
						if(!Objects.isNull(resultado)) {
							resultadoDto = resultado.stream().map(
									entity ->  this.vistaMapper.toDto(entity)).toList();
							results.put(e.getNombreVista(), resultadoDto);
						}
					}
			}
		}

		return results;
	}

}
