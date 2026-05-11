package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.omisos;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.omisos.ReporteOmisosPersonerosRepository;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import java.util.List;

@Getter
@Component
public class ReporteOmisosPersonerosHandler implements ReporteOmisosHandler {

    @Autowired
    private ReporteOmisosPersonerosRepository repository;
    private final String nombreColumna = ConstantesReportes.nombreColumnaOmisosPersoneros;
    private final Integer tipoReporteActorElectoral = 3;
    private final String tituloReporte = ConstantesReportes.TITULO_REPORTE_OMISOS_PERSONEROS;
    private final String nombreReporteResumen = ConstantesReportes.nameReporteOmisosPersonerosResumen;
    private final String nombreReporteDetalle = ConstantesReportes.nameReporteOmisosPersonerosDetalle;

    @Override
    public List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro) {
        return repository.listarReporteOmisos(filtro);
    }
}