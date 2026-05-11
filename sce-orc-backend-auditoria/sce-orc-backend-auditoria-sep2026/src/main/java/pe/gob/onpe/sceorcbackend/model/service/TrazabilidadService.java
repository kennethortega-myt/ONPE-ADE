package pe.gob.onpe.sceorcbackend.model.service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ActaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.ItemHistory;

public interface TrazabilidadService {

  ItemHistory switchItemHistoryByEstado(String estadoEvaluar, ActaTransmisionNacion atn, ActaPorTransmitirDto acta,
                                                int iRecibida, int iAprobada, int iVeri1, int iVeri2, int iPorCorregir);
}
