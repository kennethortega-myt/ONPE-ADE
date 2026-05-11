package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasEstadoMesaRequestDto;

public interface IReporteMesasEstadoMesaService {
    byte[] reporteMesasEstadoMesa(ReporteMesasEstadoMesaRequestDto filtro);
}
