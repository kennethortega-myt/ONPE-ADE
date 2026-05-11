package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteOmisosDto;
import pe.gob.onpe.scebackend.model.orc.repository.ReporteOmisosMiembrosMesaRepository;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class ReporteOmisosMiembrosMesaHandler implements ReporteOmisosHandler {

    @Autowired
    private ReporteOmisosMiembrosMesaRepository repository;
    private final String nombreColumna = ConstantesReportes.NOMBRE_COLUMNA_OMISOS_MIEMBROS_MESA;
    private final Integer tipoReporteActorElectoral = 2;
    private final String tituloReporte = ConstantesReportes.TITULO_REPORTE_OMISOS_MIEMBROS_MESA;
    private final String nombreReporteResumen = ConstantesReportes.NAME_REPORTE_OMISOS_MM_RESUMEN;
    private final String nombreReporteDetalle = ConstantesReportes.NAME_REPORTE_OMISOS_MM_DETALLE;

    @Override
    public List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro) {
        return this.repository.listarReporteOmisos(filtro.getEsquema(),
        									filtro.getIdEleccion(),
        									filtro.getIdCentroComputo())
        						.stream()
        						.map(this::llenarDatos).toList();
    }
    
    private ReporteOmisosDto llenarDatos(Map<String, Object> mapOmiso) {
        ReporteOmisosDto reporte = new ReporteOmisosDto();
        reporte.setCodigoUbigeo((String) mapOmiso.get("c_codigo_ubigeo"));
        reporte.setCodigoCentroComputo((String) mapOmiso.get("c_codigo_centro_computo"));
        reporte.setDepartamento((String) mapOmiso.get("c_departamento"));
        reporte.setProvincia((String) mapOmiso.get("c_provincia"));
        reporte.setDistrito((String) mapOmiso.get("c_distrito"));
        reporte.setTotalMesas(((Long) mapOmiso.get("n_total_mesa")).intValue());
        reporte.setTotalElectores(mapOmiso.get("n_total_miembros_mesa") == null ? 0 : ((BigDecimal) mapOmiso.get("n_total_miembros_mesa")).intValue());
        reporte.setTotalMesasRegistradas(((Long) mapOmiso.get("n_mesas_registradas")).intValue());
        reporte.setTotalOmisos(mapOmiso.get("n_total_omisos") == null ? 0 : ((BigDecimal) mapOmiso.get("n_total_omisos")).intValue());
        
        return reporte;
    }
}