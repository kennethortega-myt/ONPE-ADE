package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasUbigeoRequestDto;

public interface IReporteMesasUbigeoService {
    byte[] reporteMesasUbigeo(ReporteMesasUbigeoRequestDto filtro);
}
