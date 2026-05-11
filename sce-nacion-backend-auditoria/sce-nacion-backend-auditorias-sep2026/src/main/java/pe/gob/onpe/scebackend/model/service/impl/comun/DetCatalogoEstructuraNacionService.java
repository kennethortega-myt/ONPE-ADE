package pe.gob.onpe.scebackend.model.service.impl.comun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.comun.DetCatalogoEstructuraRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.DetCatalogoEstructuraResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.comun.IDetCatalogoEstructuraRepositoryCustom;
import pe.gob.onpe.scebackend.model.service.comun.IDetCatalogoEstructuraNacionService;

import java.util.List;

@Service
public class DetCatalogoEstructuraNacionService implements IDetCatalogoEstructuraNacionService {

    @Autowired
    IDetCatalogoEstructuraRepositoryCustom detCatalogoEstructuraRepositoryCustom;

    @Override
    @Transactional("locationTransactionManager")
    public List<DetCatalogoEstructuraResponseDto> listarDetCatalogoEstructura(DetCatalogoEstructuraRequestDto filtro) {
        return detCatalogoEstructuraRepositoryCustom.listarDetalleEstructura(filtro);
    }
}
