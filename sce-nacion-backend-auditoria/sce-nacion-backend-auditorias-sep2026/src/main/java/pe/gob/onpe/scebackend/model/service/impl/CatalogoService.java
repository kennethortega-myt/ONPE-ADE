package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.entities.Catalogo;
import pe.gob.onpe.scebackend.model.entities.DetalleCatalogoEstructura;
import pe.gob.onpe.scebackend.model.entities.DetalleCatalogoReferencia;
import pe.gob.onpe.scebackend.model.repository.DetalleCatalogoEstructuraRepository;
import pe.gob.onpe.scebackend.model.repository.DetalleCatalogoReferenciaRepository;
import pe.gob.onpe.scebackend.model.repository.Procedures;
import pe.gob.onpe.scebackend.model.service.ICatalogoService;
import pe.gob.onpe.scebackend.utils.LoggingUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CatalogoService implements ICatalogoService {

    private final DetalleCatalogoReferenciaRepository detalleCatalogoReferenciaRepository;

    private final DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository;

    private final Procedures procedures;

    @Override
    @Transactional("tenantTransactionManager")
    public Catalogo getCatalogoByTabla(String table) {
        return null;
    }

    @Override
    @Transactional("tenantTransactionManager")
    public Object listaCalogos(String tablaReferencia) {
        List<DetalleCatalogoReferencia> listaReferencia = this.detalleCatalogoReferenciaRepository.findByTablaReferencia(tablaReferencia);
        List<Integer> listIdsCatalogos = listaReferencia.stream().map(x->x.getCatalogo().getId()).toList();
        List<DetalleCatalogoEstructura> listaDatos = this.detalleCatalogoEstructuraRepository.findByCatalogoIdIn(listIdsCatalogos);
        return listaDatos.stream().collect(Collectors.groupingBy(x->x.getCatalogo().getMaestro()));
    }

    @Override
    public Map<String, Boolean> ejecutarEstructuras(String esquema, String usuario) {
        Map<String, Boolean> result = new HashMap<>();
        try{
            Boolean estructuraVistas = this.procedures.cargarEstructuraVistas(esquema,usuario);
            result.put(ConstantesComunes.NAME_SP_VISTA,estructuraVistas) ;
        }catch (Exception e){
            result.put(ConstantesComunes.NAME_SP_VISTA,false) ;
            LoggingUtil.logTrace("ejecutarEstructuras","EstructuraServicio.java", Arrays.asList(esquema, usuario),null,true,e);

        }
        try{
            Boolean estructuraParticipacion = this.procedures.cargarEstructuraParticipacionCiudadana(esquema,usuario);
            result.put(ConstantesComunes.NAME_SP_CIUDADANO,estructuraParticipacion);
        }catch (Exception e){
            result.put(ConstantesComunes.NAME_SP_CIUDADANO,false);
            LoggingUtil.logTrace("ejecutarEstructuras","EstructuraServicio.java", Arrays.asList(esquema, usuario),null,true,e);

        }

        return result;
    }


}
