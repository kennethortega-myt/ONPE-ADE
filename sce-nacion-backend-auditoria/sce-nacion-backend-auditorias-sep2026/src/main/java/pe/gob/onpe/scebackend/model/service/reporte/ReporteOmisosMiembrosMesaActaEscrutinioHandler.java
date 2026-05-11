package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.Getter;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteOmisosDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ReporteOmisosMiembrosMesaActaEscrutinioRepository;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import java.util.List;

@Getter
@Component
public class ReporteOmisosMiembrosMesaActaEscrutinioHandler implements ReporteOmisosHandler {

    private final ReporteOmisosMiembrosMesaActaEscrutinioRepository repository;

    private static final String NOMBRE_COLUMNA = ConstantesReportes.NOMBRE_COLUMNA_OMISOS_MIEMBROS_MESA_ACTA_ESCRUTINIO;
    private static final Integer TIPO_REPORTE_ACTOR_ELECTORAL = 4;
    private static final String TITULO_REPORTE = ConstantesReportes.TITULO_REPORTE_OMISOS_MIEMBROS_MESA_ACTA_ESCRUTINIO;
    private static final String NOMBRE_REPORTE_RESUMEN = ConstantesReportes.NAME_REPORTE_OMISOS_MM_ACTA_ESC_RESUMEN;
    private static final String NOMBRE_REPORTE_DETALLE = ConstantesReportes.NAME_REPORTE_OMISOS_MM_ACTA_ESC_DETALLE;

    public ReporteOmisosMiembrosMesaActaEscrutinioHandler(
            ReporteOmisosMiembrosMesaActaEscrutinioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro) {
        return repository.listarReporteOmisos(
                filtro.getEsquema(),
                filtro.getIdEleccion(),
                filtro.getIdCentroComputo()
        ).stream().map(r -> {
            ReporteOmisosDto dto = new ReporteOmisosDto();
            dto.setCodigoUbigeo(r.getCodigoUbigeo());
            dto.setCodigoCentroComputo(r.getCodigoCentroComputo());
            dto.setDepartamento(r.getDepartamento());
            dto.setProvincia(r.getProvincia());
            dto.setDistrito(r.getDistrito());
            dto.setTotalMesas(r.getTotalMesas());
            dto.setTotalElectores(r.getTotalElectores());
            dto.setTotalMesasRegistradas(r.getTotalMesasRegistradas());
            return dto;
        }).toList();
    }


    @Override
    public String getNombreColumna() {
        return NOMBRE_COLUMNA;
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
    public String getNombreReporteResumen() {
        return NOMBRE_REPORTE_RESUMEN;
    }

    @Override
    public String getNombreReporteDetalle() {
        return NOMBRE_REPORTE_DETALLE;
    }
}