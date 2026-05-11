package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaEscrutinioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.PersoneroRepository;

@Getter
@Component
public class OmisosServiceGroup {

    private final OmisoVotanteService omisoVotanteService;
    private final OmisoMiembroMesaService omisoMiembroMesaService;
    private final MiembroMesaColaService miembroMesaColaService;
    private final MiembroMesaSorteadoService miembroMesaSorteadoService;
    private final PersoneroRepository personeroRepository;
    private final MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository;

    @Autowired
    public OmisosServiceGroup(
           OmisoVotanteService omisoVotanteService,
           OmisoMiembroMesaService omisoMiembroMesaService,
           MiembroMesaColaService miembroMesaColaService,
           MiembroMesaSorteadoService miembroMesaSorteadoService,
           PersoneroRepository personeroRepository,
           MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository
    ) {
        this.omisoVotanteService = omisoVotanteService;
        this.omisoMiembroMesaService = omisoMiembroMesaService;
        this.miembroMesaColaService = miembroMesaColaService;
        this.miembroMesaSorteadoService = miembroMesaSorteadoService;
        this.personeroRepository = personeroRepository;
        this.miembroMesaEscrutinioRepository = miembroMesaEscrutinioRepository;
    }

}
