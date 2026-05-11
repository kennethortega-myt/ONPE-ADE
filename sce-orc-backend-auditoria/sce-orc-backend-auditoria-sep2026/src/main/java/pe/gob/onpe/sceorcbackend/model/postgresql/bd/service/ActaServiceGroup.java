package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Getter
@Component
public class ActaServiceGroup {

    private final CabActaService cabActaService;
    private final DetActaService detActaService;
    private final DetActaPreferencialService detActaPreferencialService;
    private final DetActaOpcionService detActaOpcionService;
    private final DetActaRectangleService detActaRectangleService;
    private final DistritoElectoralService distritoElectoralService;
    private final DetUbigeoEleccionAgrupacionPoliticaService detUbigeoEleccionAgrupacionPoliticaService;
    private final UbigeoEleccionService ubigeoEleccionService;
    private final DetActaAccionService detActaAccionService;
    private final AgrupacionPoliticaService agrupacionPoliticaService;

    @Autowired
    public ActaServiceGroup(
            CabActaService cabActaService,
            DetActaRectangleService detActaRectangleService,
            DistritoElectoralService distritoElectoralService,
            DetUbigeoEleccionAgrupacionPoliticaService detUbigeoEleccionAgrupacionPoliticaService,
            UbigeoEleccionService ubigeoEleccionService,
            DetActaService detActaService,
            DetActaPreferencialService detActaPreferencialService,
            DetActaAccionService detActaAccionService,
            AgrupacionPoliticaService agrupacionPoliticaService,
            DetActaOpcionService detActaOpcionService
    ) {
        this.cabActaService = cabActaService;
        this.detActaRectangleService = detActaRectangleService;
        this.distritoElectoralService = distritoElectoralService;
        this.detUbigeoEleccionAgrupacionPoliticaService = detUbigeoEleccionAgrupacionPoliticaService;
        this.ubigeoEleccionService = ubigeoEleccionService;
        this.detActaService = detActaService;
        this.detActaPreferencialService = detActaPreferencialService;
        this.detActaAccionService = detActaAccionService;
        this.agrupacionPoliticaService = agrupacionPoliticaService;
        this.detActaOpcionService = detActaOpcionService;
    }

}
