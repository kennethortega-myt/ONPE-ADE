package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.exception.BusinessValidationException;
import pe.gob.onpe.sceorcbackend.model.dto.PadronElectoralBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.PadronElectoralResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.PadronElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.specification.PadronElectoralSpecification;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PadronElectoralService;
import java.util.Optional;

@Service
public class PadronElectoralServiceImpl implements PadronElectoralService {

    private final PadronElectoralRepository padronElectoralRepository;

    public PadronElectoralServiceImpl(PadronElectoralRepository padronElectoralRepository){
     this.padronElectoralRepository = padronElectoralRepository;
    }

    @Override
    public void save(PadronElectoral padronElectoral) {
        this.padronElectoralRepository.save(padronElectoral);
    }

    @Override
    public void saveAll(List<PadronElectoral> k) {
        this.padronElectoralRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.padronElectoralRepository.deleteAll();
    }

    @Override
    public List<PadronElectoral> findAll() {
        return padronElectoralRepository.findAll();
    }

    @Override
    public Optional<PadronElectoral> findById(Long id) {
        return padronElectoralRepository.findById(id);
    }

    @Override
    public Optional<PadronElectoral> findByDocumentoIdentidad(String dni) {
        return this.padronElectoralRepository.findByDocumentoIdentidad(dni);
    }

    @Override
    public Optional<PadronElectoral> findByDocumentoIdentidadAndMesa(String dni, String mesa) {
        return this.padronElectoralRepository.findByDocumentoIdentidadAndCodigoMesa(dni, mesa);
    }

    @Override
	public List<PadronElectoral> findPadronElectoralByCodigoMesaOrderByOrden(String codigoMesa) {
		return padronElectoralRepository.findPadronElectoralByCodigoMesaOrderByOrden(codigoMesa);
	}

    @Override
    public boolean existsByActivo(Integer activo) {
        return padronElectoralRepository.existsByActivo(activo);
    }

    @Override
    public Optional<PadronElectoral> findByDocumentoIdentidadAndMesaId(String dni, Integer mesaId) {
        return this.padronElectoralRepository.findByDocumentoIdentidadAndMesaId(dni, mesaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PadronElectoralResponse> buscarElectores(PadronElectoralBusquedaDto criterios, int page, int size) {
        if (!validarCriteriosBusqueda(criterios)){
            throw new BusinessValidationException("Debe proporcionar al menos un criterio de búsqueda.");
        }

        // Crear Pageable con ordenamiento
        Pageable pageable = PageRequest.of(page, size, Sort.by("apellidoPaterno", "apellidoMaterno", "nombres"));

        Specification<PadronElectoral> spec = PadronElectoralSpecification.buscarPorCriterios(criterios);
        Page<PadronElectoral> resultados = padronElectoralRepository.findAll(spec, pageable);

        return resultados.map(this::mapToDTO);
    }

    private boolean validarCriteriosBusqueda(PadronElectoralBusquedaDto criterios) {
        return (criterios.getDni() != null && !criterios.getDni().trim().isEmpty()) ||
                criterios.getNumeroMesa() != null ||
                (criterios.getNombres() != null && !criterios.getNombres().trim().isEmpty()) ||
                (criterios.getApellidoPaterno() != null && !criterios.getApellidoPaterno().trim().isEmpty()) ||
                (criterios.getApellidoMaterno() != null && !criterios.getApellidoMaterno().trim().isEmpty());
    }

    private PadronElectoralResponse mapToDTO(PadronElectoral padron) {
        return PadronElectoralResponse.builder()
                .id(padron.getId())
                .documentoIdentidad(padron.getDocumentoIdentidad())
                .nombres(padron.getNombres())
                .apellidoPaterno(padron.getApellidoPaterno())
                .apellidoMaterno(padron.getApellidoMaterno())
                .codigoMesa(padron.getCodigoMesa())
                .mesaId(padron.getMesaId())
                .orden(padron.getOrden())
                .build();
    }


}
