package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteOmisosDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ReporteOmisosPersonerosRepository;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;

import java.util.List;

@Getter
@Component
public class ReporteOmisosPersonerosHandler implements ReporteOmisosHandler {

    @Autowired
    private ReporteOmisosPersonerosRepository repository;
    private final String nombreColumna = ConstantesReportes.NOMBRE_COLUMNA_OMISOS_PERSONEROS;
    private final Integer tipoReporteActorElectoral = 3;
    private final String tituloReporte = ConstantesReportes.TITULO_REPORTE_OMISOS_PERSONEROS;
    private final String nombreReporteResumen = ConstantesReportes.NAME_REPORTE_OMISOS_PERSONEROS_RESUMEN;
    private final String nombreReporteDetalle = ConstantesReportes.NAME_REPORTE_OMISOS_PERSONEROS_DETALLE;

    @Override
    public List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro) {
        return repository.listarReporteOmisos(filtro);
    }
}