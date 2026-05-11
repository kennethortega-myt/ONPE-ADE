package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasUbigeoRequestDto;

public interface IReporteMesasUbigeoService {
    byte[] reporteMesasUbigeo(ReporteMesasUbigeoRequestDto filtro);
}
