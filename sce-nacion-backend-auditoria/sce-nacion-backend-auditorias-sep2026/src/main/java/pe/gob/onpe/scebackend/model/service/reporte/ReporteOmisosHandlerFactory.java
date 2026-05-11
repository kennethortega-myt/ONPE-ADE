package pe.gob.onpe.scebackend.model.service.reporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;

@Component
public class ReporteOmisosHandlerFactory {

    @Autowired
    private ReporteOmisosElectoresHandler reporteOmisosElectoresHandler;
    @Autowired
    private ReporteOmisosMiembrosMesaHandler reporteOmisosMiembrosMesaHandler;
    @Autowired
    private ReporteOmisosPersonerosHandler reporteOmisosPersonerosHandler;
    @Autowired
    private ReporteOmisosMiembrosMesaActaEscrutinioHandler reporteOmisosMiembrosMesaActaEscrutinioHandler;

    public ReporteOmisosHandler getHandler(ReporteOmisosRequestDto filtro) {
        if (filtro.getTipoReporteActorElectoral() == 1) {
            return reporteOmisosElectoresHandler;
        } else if (filtro.getTipoReporteActorElectoral() == 2) {
            return reporteOmisosMiembrosMesaHandler;
        } else if (filtro.getTipoReporteActorElectoral() == 3) {
            return reporteOmisosPersonerosHandler;
        } else if (filtro.getTipoReporteActorElectoral() == 4) {
            return reporteOmisosMiembrosMesaActaEscrutinioHandler;
        }else {
            throw new IllegalArgumentException("Tipo de reporte no soportado");
        }
    }
}