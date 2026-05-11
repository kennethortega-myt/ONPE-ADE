package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ListaParticipantesRequestDto;

public interface ListaParticipantesService {

	public byte[] getReporteListaParticipantes(ListaParticipantesRequestDto filtro);
}
