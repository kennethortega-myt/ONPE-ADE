package pe.gob.onpe.scebatchpr.repository.orc;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebatchpr.dto.VwArchivoEscrutinioSinFirmarDto;
import pe.gob.onpe.scebatchpr.dto.VwArchivoInstalacionSufragioSinFirmarDto;
import pe.gob.onpe.scebatchpr.dto.VwArchivoTransmisionDto;

@Repository
public class ArchivoTransmisionRepositoryCustomImpl implements ArchivoTransmisionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Value("${archivos.pendientes.block.size:100}")
    private int blockSize;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<VwArchivoTransmisionDto> listarArchivosPendientes() {
        String sql = """
             SELECT
			    a.n_acta_pk     AS n_acta_pk,
			    a.n_archivo_pk  AS n_archivo_pk,
			    a.c_ruta        AS c_ruta,
			    a.c_guid        AS c_guid,
			    a.c_formato     AS c_formato,
			    a.c_peso        AS c_peso,
			    a.c_nombre_original AS c_nombre_original,
			    a.n_documento_electoral as n_documento_electoral,
			    a.tipo_archivo  AS tipo_archivo
			FROM {h-schema}vw_archivos_pendientes_transmision_pr a
			LIMIT :limit
            """;

        List<Tuple> rows = entityManager
            .createNativeQuery(sql, Tuple.class)
            .setParameter("limit", blockSize)
            .getResultList();

        List<VwArchivoTransmisionDto> result = new ArrayList<>(rows.size());

        for (Tuple row : rows) {
        	VwArchivoTransmisionDto dto = new VwArchivoTransmisionDto();
            dto.setIdActa(getLong(row, "n_acta_pk"));
            dto.setIdArchivo(getLong(row, "n_archivo_pk"));
            dto.setNombreOriginal(row.get("c_nombre_original", String.class));
            dto.setRuta(row.get("c_ruta", String.class));
            dto.setGuid(row.get("c_guid", String.class));
            dto.setFormato(row.get("c_formato", String.class));
            dto.setPeso(row.get("c_peso", String.class));
            dto.setDocumentoElectoral(getInteger(row, "n_documento_electoral"));
            result.add(dto);
        }

        return result;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	public List<VwArchivoEscrutinioSinFirmarDto> listarArchivosEscrutinioSinFirmar() {
    	String sql = """
                SELECT
   			    s.n_acta_pk     AS n_acta_pk,
   			    s.c_ruta  		AS c_ruta
    			FROM {h-schema}vw_archivos_escrutinio_sin_firmar s
    			LIMIT :limit
               """;
    	
    	List<Tuple> rows = entityManager
               .createNativeQuery(sql, Tuple.class)
               .setParameter("limit", blockSize)
               .getResultList();
    	
    	List<VwArchivoEscrutinioSinFirmarDto> result = new ArrayList<>(rows.size());
    	
    	for (Tuple row : rows) {
    		VwArchivoEscrutinioSinFirmarDto dto = new VwArchivoEscrutinioSinFirmarDto();
            dto.setIdActa(getLong(row, "n_acta_pk"));
            dto.setRuta(row.get("c_ruta", String.class));
            result.add(dto);
        }
           
        return result;
	}
    
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
	public List<VwArchivoInstalacionSufragioSinFirmarDto> listarArchivosInstalacionSufragioSinFirmar() {
    	String sql = """
                SELECT
   			    s.n_acta_pk     AS n_acta_pk,
   			    s.c_ruta  		AS c_ruta
    			FROM {h-schema}vw_archivos_instalacion_sf_sin_firmar s
    			LIMIT :limit
               """;
    	
    	List<Tuple> rows = entityManager
               .createNativeQuery(sql, Tuple.class)
               .setParameter("limit", blockSize)
               .getResultList();
    	
    	List<VwArchivoInstalacionSufragioSinFirmarDto> result = new ArrayList<>(rows.size());
    	
    	for (Tuple row : rows) {
    		VwArchivoInstalacionSufragioSinFirmarDto dto = new VwArchivoInstalacionSufragioSinFirmarDto();
            dto.setIdActa(getLong(row, "n_acta_pk"));
            dto.setRuta(row.get("c_ruta", String.class));
            result.add(dto);
        }
           
        return result;
	}
    
    private Long getLong(Tuple tuple, String alias) {
        Object value = tuple.get(alias);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    private Integer getInteger(Tuple tuple, String alias) {
        Object value = tuple.get(alias);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }

	

	
}