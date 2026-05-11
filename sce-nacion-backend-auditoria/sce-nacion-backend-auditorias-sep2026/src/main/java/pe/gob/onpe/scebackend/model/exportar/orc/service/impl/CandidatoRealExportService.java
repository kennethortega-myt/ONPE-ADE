package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoRealExportDto;
import pe.gob.onpe.scebackend.model.exportar.orc.service.ICandidatoRealExportService;
import pe.gob.onpe.scebackend.model.orc.repository.CandidatoRealRepository;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class CandidatoRealExportService implements ICandidatoRealExportService {

	@Autowired
	private CandidatoRealRepository candidatoRealRepository;
	
	@Override
	public List<CandidatoRealExportDto> findAll() {
		 return candidatoRealRepository.findAll()
		            .stream()
		            .map(candidato ->
		            	{
		            		CandidatoRealExportDto dto = new CandidatoRealExportDto();
		            		dto.setId(candidato.getId());
		            		dto.setIdDistritoElectoral(candidato.getIdDistritoElectoral());
		            		dto.setIdEleccion(candidato.getIdEleccion());
		                    dto.setIdAgrupacionPolitica(candidato.getIdAgrupacionPolitica());
		                    dto.setIdUbigeo(candidato.getIdUbigeo());
		                    dto.setCargo(candidato.getCargo());
		                    dto.setDocumentoIdentidad(candidato.getDocumentoIdentidad());
		                    dto.setApellidoPaterno(candidato.getApellidoPaterno());
		                    dto.setApellidoMaterno(candidato.getApellidoMaterno());
		                    dto.setNombres(candidato.getNombres());
		                    dto.setSexo(candidato.getSexo());
		                    dto.setEstado(candidato.getEstado());
		                    dto.setLista(candidato.getLista());
		                    dto.setActivo(candidato.getActivo());
		                    dto.setAudUsuarioCreacion(candidato.getUsuarioCreacion());
		                    dto.setAudFechaCreacion(DateUtil.getDateString(candidato.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
		                    dto.setAudUsuarioModificacion(candidato.getUsuarioModificacion());
		                    dto.setAudFechaModificacion(DateUtil.getDateString(candidato.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
		            		return dto;
		            	}).toList();
	}

	@Override
	public List<CandidatoRealExportDto> findByCc(String codigo) {
		return candidatoRealRepository.findByCc(codigo)
	            .stream()
	            .map(candidato ->
	            	{
	            		CandidatoRealExportDto dto = new CandidatoRealExportDto();
	            		dto.setId(candidato.getId());
	            		dto.setIdDistritoElectoral(candidato.getIdDistritoElectoral());
	            		dto.setIdEleccion(candidato.getIdEleccion());
	                    dto.setIdAgrupacionPolitica(candidato.getIdAgrupacionPolitica());
	                    dto.setIdUbigeo(candidato.getIdUbigeo());
	                    dto.setCargo(candidato.getCargo());
	                    dto.setDocumentoIdentidad(candidato.getDocumentoIdentidad());
	                    dto.setApellidoPaterno(candidato.getApellidoPaterno());
	                    dto.setApellidoMaterno(candidato.getApellidoMaterno());
	                    dto.setNombres(candidato.getNombres());
	                    dto.setSexo(candidato.getSexo());
	                    dto.setEstado(candidato.getEstado());
	                    dto.setLista(candidato.getLista());
	                    dto.setActivo(candidato.getActivo());
	                    dto.setAudUsuarioCreacion(candidato.getUsuarioCreacion());
	                    dto.setAudFechaCreacion(DateUtil.getDateString(candidato.getFechaCreacion(), ConstantesComunes.FORMATO_FECHA));
	                    dto.setAudUsuarioModificacion(candidato.getUsuarioModificacion());
	                    dto.setAudFechaModificacion(DateUtil.getDateString(candidato.getFechaModificacion(), ConstantesComunes.FORMATO_FECHA));
	            		return dto;
	            	}).toList();
	}

	

}
