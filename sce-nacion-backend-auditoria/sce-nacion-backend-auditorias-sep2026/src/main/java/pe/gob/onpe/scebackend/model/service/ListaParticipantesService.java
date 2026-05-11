package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ListaParticipantesRequestDto;

public interface ListaParticipantesService {

	byte[] getReporteListaParticipantes(ListaParticipantesRequestDto filtro);
}
