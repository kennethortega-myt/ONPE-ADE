package pe.gob.onpe.scebackend.model.service;

import java.util.Map;

public interface IAgrupacionPoliticaService {

	Map<String, Object> cargarCandidatos(String piEsquema, Integer poResultado, String poMensaje);
	
}
