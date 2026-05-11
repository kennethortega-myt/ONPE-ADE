package pe.gob.onpe.scebatchpr.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.scebatchpr.repository.orc.DetParametroRepository;
import pe.gob.onpe.scebatchpr.service.PuestaCeroService;
import pe.gob.onpe.scebatchpr.utils.SceConstantes;


@Service
@RequiredArgsConstructor
public class PuestaCeroServiceImpl implements PuestaCeroService{

	private final DetParametroRepository detParametroRepository;

    @Override
    public boolean isPuestaCeroActiva() {
        return detParametroRepository
                .findValorParametro(SceConstantes.CAB_PARAM_EJECUCION_PUESTA_CERO_NACION, SceConstantes.ACTIVO)
                .map(val -> SceConstantes.TEXT_TRUE.equalsIgnoreCase(val.trim())).orElse(false);
    }

	
}
