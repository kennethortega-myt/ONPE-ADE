package pe.gob.onpe.scebackend.model.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import pe.gob.onpe.scebackend.model.orc.entities.Mesa;
import pe.gob.onpe.scebackend.model.orc.repository.MesaRepository;
import pe.gob.onpe.scebackend.model.service.IMesaService;

@Service
@RequiredArgsConstructor
public class MesaService implements IMesaService {

    private final MesaRepository mesaRepository;

    @Override
    public Mesa findByCodigo(String mesa) {
        return this.mesaRepository.findByCodigo(mesa);
    }

    @Override
    public List<Mesa> findByCodigoIn(List<String> codigos) {
        return this.mesaRepository.findByCodigoIn(codigos);
    }

}
