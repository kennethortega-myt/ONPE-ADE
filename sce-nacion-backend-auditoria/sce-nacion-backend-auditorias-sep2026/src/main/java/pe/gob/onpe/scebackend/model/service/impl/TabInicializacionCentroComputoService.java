package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportarRequestDto;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.TabInicializacionCentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.TransmisionEnvio;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.TabInicializacionCentroComputoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.TransmisionEnvioRepository;
import pe.gob.onpe.scebackend.model.service.ITabInicializacionCentroComputoService;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class TabInicializacionCentroComputoService implements ITabInicializacionCentroComputoService {

	@Autowired
	private TabInicializacionCentroComputoRepository tabInicializacionCentroComputoRepository;
	
	@Autowired
	private CentroComputoRepository centroComputoRepository;
	
	@Autowired
	private TransmisionEnvioRepository transmisionEnvioRepository;
	
	@Override
	@Transactional("locationTransactionManager")
	public void guardarInicializarCc(ExportarRequestDto request) {
		Optional<CentroComputo> cc = this.centroComputoRepository.findOneByCc(request.getCc());
		if(cc.isPresent()) {
			TransmisionEnvio envio = new TransmisionEnvio();
			envio.setCentroComputo(cc.get());
			envio.setTramaDato(request.toJson());
			envio.setEstado(SceConstantes.ACTIVO);
			envio.setActivo(SceConstantes.ACTIVO);
			envio.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
			envio.setFechaCreacion(new Date());
			transmisionEnvioRepository.save(envio);
			TabInicializacionCentroComputo inicializacionCc = new TabInicializacionCentroComputo();
			inicializacionCc.setCentroComputo(cc.get());
			inicializacionCc.setTransmisionEnvio(envio);
			inicializacionCc.setActivo(SceConstantes.ACTIVO);
			inicializacionCc.setUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
			inicializacionCc.setFechaCreacion(new Date());
			tabInicializacionCentroComputoRepository.save(inicializacionCc);
		}
		
	}

}
