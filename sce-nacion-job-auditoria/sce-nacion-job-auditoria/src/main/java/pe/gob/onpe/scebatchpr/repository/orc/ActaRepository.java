package pe.gob.onpe.scebatchpr.repository.orc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebatchpr.entities.orc.Acta;

public interface ActaRepository extends JpaRepository<Acta, Long> {
	
	@Query(value = "select distinct ca.* from {h-schema}cab_acta ca " +
			"inner join {h-schema}tab_pr_transmision pr on pr.n_acta = ca.n_acta_pk " +
			"left join  {h-schema}tab_archivo tae  on ca.n_archivo_escrutinio_pdf_firmado = tae.n_archivo_pk " +
			"left join  {h-schema}tab_archivo tas  on ca.n_archivo_escrutinio_pdf = tas.n_archivo_pk " +
			"left join  {h-schema}tab_archivo tais on ca.n_archivo_instalacion_sufragio_pdf = tais.n_archivo_pk " +
			"left join  {h-schema}tab_archivo tif  on ca.n_archivo_instalacion_pdf_firmado = tif.n_archivo_pk " +
			"left join  {h-schema}tab_archivo tsf  on ca.n_archivo_sufragio_pdf_firmado = tsf.n_archivo_pk " +
			"where pr.n_estado=1 and "
			+ "("
			+ "tae.n_estado_transmision = 0 or tais.n_estado_transmision = 0 or tif.n_estado_transmision = 0 or tsf.n_estado_transmision=0 "
			+ "or tas.n_estado_transmision=0) "
			+ "and (ca.n_archivo_escrutinio_pdf_firmado is not null or ca.n_archivo_instalacion_sufragio_pdf is not null or "
			+ "ca.n_archivo_instalacion_pdf_firmado is not null or ca.n_archivo_sufragio_pdf_firmado is not null "
			+ "or ca.n_archivo_escrutinio_pdf is not null "
			+ ") " +
			"union " +
			"select distinct ca.* from {h-schema}cab_acta ca " +
			"inner join {h-schema}tab_pr_transmision pr on pr.n_acta = ca.n_acta_pk " +
			"inner join {h-schema}det_acta_resolucion dar on dar.n_acta = ca.n_acta_pk " +
			"inner join {h-schema}tab_resolucion tr on dar.n_resolucion = tr.n_resolucion_pk " +
			"inner join {h-schema}tab_archivo a on a.n_archivo_pk = tr.n_archivo_resolucion " +
			"where pr.n_estado=1 and a.n_estado_transmision = 0", nativeQuery = true)
		List<Acta> listarArchivosActasTransmitirFirmados();
	
	@Query(value = "select distinct ca.* from {h-schema}cab_acta ca " +
			"inner join {h-schema}tab_pr_transmision pr on pr.n_acta = ca.n_acta_pk " +
			"left join {h-schema}tab_archivo tae on ca.n_archivo_escrutinio_pdf = tae.n_archivo_pk " +
			"left join {h-schema}tab_archivo tais on ca.n_archivo_instalacion_sufragio_pdf = tais.n_archivo_pk " +
			"where (tae.n_estado_transmision = 0 or tais.n_estado_transmision = 0) " +
			"and pr.n_estado=1 and (ca.n_archivo_escrutinio_pdf is not null or ca.n_archivo_instalacion_sufragio_pdf is not null) " +
			"union " +
			"select distinct ca.* from {h-schema}cab_acta ca " +
			"inner join {h-schema}tab_pr_transmision pr on pr.n_acta = ca.n_acta_pk " +
			"inner join {h-schema}det_acta_resolucion dar on dar.n_acta = ca.n_acta_pk " +
			"inner join {h-schema}tab_resolucion tr on dar.n_resolucion = tr.n_resolucion_pk " +
			"inner join {h-schema}tab_archivo a on a.n_archivo_pk = tr.n_archivo_resolucion " +
			"where pr.n_estado=1 and a.n_estado_transmision = 0", nativeQuery = true)
		List<Acta> listarArchivosActasTransmitir();
	
	
	@Query("""
	        SELECT DISTINCT a.id FROM Acta a
	        WHERE (a.archivoInstalacionSufragioPdf IS NOT NULL AND a.archivoInstalacionSufragioFirmado IS NULL)
	""")
	List<Long> findActasConArchivoInstalacionSufragioSinFirma();
	
}
