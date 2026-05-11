package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportDetImportadorProgreso;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportImportadorProgreso;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportDetImportadorProgresoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.repository.ImportImportadorProgresoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportImportadorProgresoService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesImportador;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;

@Service
public class ImportImportadorProgresoServiceImpl implements ImportImportadorProgresoService {
	
	Logger logger = LoggerFactory.getLogger(ImportImportadorProgresoServiceImpl.class);

	private ImportImportadorProgresoRepository cabRepository;
	
	private ImportDetImportadorProgresoRepository detalleRepository;
	
	public ImportImportadorProgresoServiceImpl(
			ImportImportadorProgresoRepository cabRepository,
			ImportDetImportadorProgresoRepository detalleRepository){
		this.cabRepository = cabRepository;
		this.detalleRepository = detalleRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<ImportImportadorProgreso> findTopByOrderByFechaCreacionDesc() {
		return cabRepository.findTopByOrderByFechaCreacionDesc();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ImportImportadorProgreso guardar(Integer estado, Double porcentaje, String usuario) {
		try {
			ImportImportadorProgreso importador = new ImportImportadorProgreso();
			importador.setEstado(estado);
			importador.setPorcentaje(porcentaje);
			importador.setUsuarioCreacion(usuario);
			importador.setUsuarioModificacion(usuario);
			importador.setFechaCreacion(DateUtil.getFechaActualPeruana());
			importador.setFechaModificacion(DateUtil.getFechaActualPeruana());
			cabRepository.save(importador);
			return importador;
		} catch (Exception e) {
			logger.warn("Error al guardar el estado de la importacion, pero no se hace rollback de la transacción principal", e);
			return null;
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ImportDetImportadorProgreso guardarDetalle(ImportImportadorProgreso importador, Double porcentaje, String mensaje, String usuario) {
		try {
			ImportDetImportadorProgreso detalle = new ImportDetImportadorProgreso();
			
			importador.setPorcentaje(porcentaje);
			importador.setEstado(ConstantesImportador.IMPORTACION_EN_PROGRESO);
			cabRepository.save(importador);
			
			detalle.setImportador(importador);
			detalle.setPorcentaje(porcentaje);
			detalle.setUsuarioCreacion(usuario);
			detalle.setMensaje(mensaje);
			detalle.setUsuarioModificacion(usuario);
			detalle.setFechaCreacion(DateUtil.getFechaActualPeruana());
			detalle.setFechaModificacion(DateUtil.getFechaActualPeruana());
			detalleRepository.save(detalle);
			return detalle;
		} catch (Exception e) {
			logger.warn("Error al guardar el detalle de la importacion, pero no se hace rollback de la transacción principal", e);
			return null;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ImportImportadorProgreso actualizar(ImportImportadorProgreso importador, Integer estado, Double porcentaje,
			String usuario) {
		importador.setEstado(estado);
		importador.setPorcentaje(porcentaje);
		importador.setUsuarioModificacion(usuario);
		importador.setFechaModificacion(DateUtil.getFechaActualPeruana());
		cabRepository.save(importador);
		return importador;
	}

}
