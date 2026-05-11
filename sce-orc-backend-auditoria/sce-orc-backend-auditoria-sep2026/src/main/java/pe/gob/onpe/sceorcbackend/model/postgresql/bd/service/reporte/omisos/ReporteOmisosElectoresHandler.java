package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.omisos;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.omisos.ReporteOmisosElectoresRepository;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;

import java.util.List;

@Getter
@Component
public class ReporteOmisosElectoresHandler implements ReporteOmisosHandler {

    @Autowired
    private ReporteOmisosElectoresRepository repository;
    private final String nombreColumna = ConstantesReportes.nombreColumnaOmisosElectores;
    private final Integer tipoReporteActorElectoral = 1;
    private final String tituloReporte = ConstantesReportes.TITULO_REPORTE_OMISOS_ELECTORES;
    private final String nombreReporteResumen = ConstantesReportes.nameReporteOmisosElectoresResumen;
    private final String nombreReporteDetalle = ConstantesReportes.nameReporteOmisosElectoresDetalle;

    @Override
    public List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro) {
        return repository.listarReporteOmisos(filtro);
    }
}