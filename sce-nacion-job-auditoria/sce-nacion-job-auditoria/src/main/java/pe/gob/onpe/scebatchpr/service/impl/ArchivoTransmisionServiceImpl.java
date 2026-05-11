package pe.gob.onpe.scebatchpr.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionDto;
import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionRequest;
import pe.gob.onpe.scebatchpr.dto.VwArchivoTransmisionDto;
import pe.gob.onpe.scebatchpr.repository.orc.ArchivoTransmisionRepositoryCustom;
import pe.gob.onpe.scebatchpr.service.ArchivoTransmisionService;
import pe.gob.onpe.scebatchpr.utils.ArchivoUtils;

@Service
@Slf4j
public class ArchivoTransmisionServiceImpl implements ArchivoTransmisionService {

	@Autowired
	private ArchivoTransmisionRepositoryCustom archivoTransmisionRepositoryCustom;
	
	@Override
	public List<ArchivoTransmisionRequest> listarArchivosPendientes() {
		List<VwArchivoTransmisionDto> rows = this.archivoTransmisionRepositoryCustom.listarArchivosPendientes();
		return mapear(rows);
	}
	
	private List<ArchivoTransmisionRequest> mapear(List<VwArchivoTransmisionDto> rows) {

	    Map<Long, List<ArchivoTransmisionDto>> agrupado = rows.stream()
	        .collect(Collectors.groupingBy(
	            VwArchivoTransmisionDto::getIdActa,
	            Collectors.mapping(r -> {
					try {
						return ArchivoTransmisionDto.builder()
							.base64(ArchivoUtils.convertToBase64(r.getRuta()))
						    .guid(r.getGuid())
						    .mimeType(r.getFormato())
							.extension(ArchivoUtils.getExtension(r.getFormato()))
							.peso(r.getPeso())
							.tipoArchivo(r.getDocumentoElectoral())
						    .build();
					} catch (IOException e) {
					    log.error(
					        "Error al leer archivo. idActa={}, idArchivo={}, ruta={}, tipoArchivo={}",
					        r.getIdActa(),
					        r.getIdArchivo(),
					        r.getRuta(),
					        r.getDocumentoElectoral(),
					        e
					    );
					}
					return null;
				},
	            Collectors.filtering(Objects::nonNull, Collectors.toList()))
	        ));

	    return agrupado.entrySet().stream()
	        .map(e -> {
	            ArchivoTransmisionRequest req = new ArchivoTransmisionRequest();
	            req.setIdActa(e.getKey());
	            req.setArchivos(e.getValue());
	            return req;
	        })
	        .toList();
	}
	
	

}
