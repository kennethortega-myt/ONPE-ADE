package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasEstadoMesaRequestDto;

public interface IReporteMesasEstadoMesaService {
    byte[] reporteMesasEstadoMesa(ReporteMesasEstadoMesaRequestDto filtro);
}
