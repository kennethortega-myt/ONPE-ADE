package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.Getter;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasSinOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasSinOmisosDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ReporteMesasSinOmisosElectoresRepository;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import java.util.List;

@Getter
@Component
public class ReporteMesasSinOmisosElectoresHandler implements ReporteMesasSinOmisosHandler {


    private static final Integer TIPO_REPORTE_ACTOR_ELECTORAL = 1;
    private static final String TITULO_REPORTE = ConstantesReportes.TITULO_REPORTE_MESAS_SIN_OMISOS_ELECTORES;

    private final ReporteMesasSinOmisosElectoresRepository reporteMesasSinOmisosElectoresRepository;

    public ReporteMesasSinOmisosElectoresHandler(ReporteMesasSinOmisosElectoresRepository reporteMesasSinOmisosElectoresRepository) {
        this.reporteMesasSinOmisosElectoresRepository = reporteMesasSinOmisosElectoresRepository;
    }

    @Override
    public Integer getTipoReporteActorElectoral() {
        return TIPO_REPORTE_ACTOR_ELECTORAL;
    }

    @Override
    public String getTituloReporte() {
        return TITULO_REPORTE;
    }

    @Override
    public List<ReporteMesasSinOmisosDto> obtenerReporte(ReporteMesasSinOmisosRequestDto filtro) {
        return reporteMesasSinOmisosElectoresRepository.listarReporteMesasSinOmisos(filtro.getEsquema(),
                        filtro.getIdEleccion(),
                        filtro.getIdCentroComputo())
                .stream()
                .map(p -> {
                    ReporteMesasSinOmisosDto dto = new ReporteMesasSinOmisosDto();
                    dto.setCodigoUbigeo(p.getCodigoUbigeo());
                    dto.setCodigoODPE(p.getCodigoODPE());
                    dto.setCodigoCentroComputo(p.getCodigoCentroComputo());
                    dto.setDepartamento(p.getDepartamento());
                    dto.setProvincia(p.getProvincia());
                    dto.setDistrito(p.getDistrito());
                    dto.setNumeroMesa(p.getNumeroMesa());
                    dto.setTotalMesas(p.getTotalMesas());
                    return dto;
                })
                .toList();
    }
}
