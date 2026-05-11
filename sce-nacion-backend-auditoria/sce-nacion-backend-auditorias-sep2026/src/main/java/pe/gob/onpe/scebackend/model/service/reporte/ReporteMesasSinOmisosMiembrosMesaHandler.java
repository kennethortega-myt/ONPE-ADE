package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasSinOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasSinOmisosDto;
import pe.gob.onpe.scebackend.model.orc.repository.ReporteMesasSinOmisosMiembrosMesaRepository;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;

import java.util.List;
import java.util.Map;

@Getter
@Component
public class ReporteMesasSinOmisosMiembrosMesaHandler implements ReporteMesasSinOmisosHandler {
	
	@Autowired
    private ReporteMesasSinOmisosMiembrosMesaRepository repository;
    private final Integer tipoReporteActorElectoral = 2;
    private final String tituloReporte = ConstantesReportes.TITULO_REPORTE_MESAS_SIN_OMISOS_MIEMBROS_MESA;

    @Override
    public List<ReporteMesasSinOmisosDto> obtenerReporte(ReporteMesasSinOmisosRequestDto filtro) {
    	return this.repository.listarReporteMesasSinOmisos(filtro.getEsquema(),
										filtro.getIdEleccion(),
										filtro.getIdCentroComputo())
								.stream()
								.map(this::llenarDatos).toList();
    }
    
    private ReporteMesasSinOmisosDto llenarDatos(Map<String, Object> mapMesa) {
        ReporteMesasSinOmisosDto reporte = new ReporteMesasSinOmisosDto();
        reporte.setCodigoUbigeo((String) mapMesa.get("c_codigo_ubigeo"));
        reporte.setCodigoODPE((String) mapMesa.get("c_codigo_odpe"));
        reporte.setCodigoCentroComputo((String) mapMesa.get("c_codigo_centro_computo"));
        reporte.setDepartamento((String) mapMesa.get("c_departamento"));
        reporte.setProvincia((String) mapMesa.get("c_provincia"));
        reporte.setDistrito((String) mapMesa.get("c_distrito"));
        reporte.setNumeroMesa((String) mapMesa.get("c_mesa"));
        reporte.setTotalMesas(Math.toIntExact((Long) mapMesa.get("n_mesas")));
        
        return reporte;
    }
}
