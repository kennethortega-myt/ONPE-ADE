package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.omisos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.sceorcbackend.model.enums.TipoReporteOmiso;

@Log4j2
public class ReporteOmisosRepository {
	
	protected TipoReporteOmiso tipoReporte;
	
	public ReporteOmisosRepository(TipoReporteOmiso tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

	protected List<ReporteOmisosDto> preparedStatementCall(ReporteOmisosRequestDto filtro, PreparedStatement ps) throws SQLException {
    	mapaerParametros(filtro, ps);
        try (ResultSet rs = ps.executeQuery()) {
            List<ReporteOmisosDto> lista = new ArrayList<>();
            while (rs.next()) {
                llenarDatos(rs, lista);
            }
            return lista;
        }
    }

	protected void llenarDatos(ResultSet rs, List<ReporteOmisosDto> lista) throws SQLException {
        ReporteOmisosDto reporte = new ReporteOmisosDto();
        reporte.setCodigoUbigeo(rs.getString("c_codigo_ubigeo"));
        reporte.setCodigoCentroComputo(rs.getString("c_codigo_centro_computo"));
        reporte.setDepartamento(rs.getString("c_departamento"));
        reporte.setProvincia(rs.getString("c_provincia"));
        reporte.setDistrito(rs.getString("c_distrito"));
        reporte.setTotalMesasRegistradas(rs.getInt("n_mesas_registradas"));
        switch(tipoReporte) {
        	case PERSONERO:
        		reporte.setTotalMesas(rs.getInt("n_total_mesas"));
                reporte.setTotalElectores(rs.getInt("n_total_personeros"));
                break;
        	case MIEMBRO_MESA:
        		reporte.setTotalMesas(rs.getInt("n_total_mesa"));
                reporte.setTotalElectores(rs.getInt("n_total_miembros_mesa"));
                reporte.setTotalOmisos(rs.getInt("n_total_omisos"));
        		break;
        	case MIEMBRO_MESA_ESCRUTINIO:
        		reporte.setTotalMesas(rs.getInt("n_total_mesas"));
                reporte.setTotalElectores(rs.getInt("n_total_mm_escrutinio"));
        		break;
        	case ELECTORES:
        		reporte.setTotalMesas(rs.getInt("n_total_mesa"));
                reporte.setTotalElectores(rs.getInt("n_votantes"));
                reporte.setTotalOmisos(rs.getInt("n_total_omisos"));
        		break;
        	default:
        		break;
        }
        
        lista.add(reporte);
    }
    
	protected void mapaerParametros(ReporteOmisosRequestDto filtro, PreparedStatement ps) throws SQLException {
        ps.setString(1, filtro.getEsquema());
        if(filtro.getIdEleccion() == null) {
            ps.setNull(2, Types.NULL);
        } else {
            ps.setInt(2, filtro.getIdEleccion());
        }

        if(filtro.getIdCentroComputo() == null) {
            ps.setNull(3, Types.NULL);
        } else {
            ps.setInt(3, filtro.getIdCentroComputo());
        }
      log.debug("Filtro:"+ filtro);
      log.debug("Sentencia:" + ps);
  }
}
