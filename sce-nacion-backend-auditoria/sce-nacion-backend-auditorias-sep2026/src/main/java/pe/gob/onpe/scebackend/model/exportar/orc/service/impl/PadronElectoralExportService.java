package pe.gob.onpe.scebackend.model.exportar.orc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.PaginaDto;
import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PadronElectoralExportOrcDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PadronElectoralExportPrDto;
import pe.gob.onpe.scebackend.model.exportar.orc.mapper.IPadronExportMapper;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IPadronElectoralExportService;
import pe.gob.onpe.scebackend.model.orc.entities.PadronElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.PadronElectoralRepository;
import pe.gob.onpe.scebackend.utils.DateUtil;

import java.util.stream.Collectors;

@Service
public class PadronElectoralExportService implements IPadronElectoralExportService {

	@Autowired
    private PadronElectoralRepository padronElectoralRepository;
	
	@Autowired
	private IPadronExportMapper padronMapper;

	@Override
	@Transactional(
		    transactionManager = "tenantTransactionManager",
		    readOnly = true
		)
    public PaginaDto<PadronElectoralExportPrDto> importarPadronesPr(int numeroPagina, int tamanoPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, tamanoPagina);
        Page<PadronElectoral> pagina = padronElectoralRepository.findAll(pageable);
        PaginaDto<PadronElectoralExportPrDto> paginaDTO = new PaginaDto<PadronElectoralExportPrDto>();
        List<PadronElectoral> registros = pagina.getContent();
        List<PadronElectoralExportPrDto> registrosDTO = null;
        
        if(registros!=null) {
        	registrosDTO = registros.stream().map(registro -> {
        		PadronElectoralExportPrDto dto = new PadronElectoralExportPrDto();
        		dto.setId(registro.getId());
        		dto.setCodigoMesa(registro.getCodigoMesa());
        		dto.setIdTipoDocumentoIdentidad(registro.getIdTipoDocumentoIdentidad());
        		dto.setDocumentoIdentidad(registro.getDocumentoIdentidad());
        		return dto;
        	}).collect(Collectors.toList());
        }
        
        paginaDTO.setData(registrosDTO);
        paginaDTO.setTotalRegistros(pagina.getTotalElements());
        paginaDTO.setTotalPaginas(pagina.getTotalPages());
        paginaDTO.setNext(pagina.hasNext());
        
        if (pagina.hasNext()) {
            paginaDTO.setPaginaSiguiente("?numeroPagina=" + (pagina.getNumber() + 1) +
                    "&tamanoPagina=" + pagina.getSize());
        }

        if (pagina.hasPrevious()) {
            paginaDTO.setPaginaAnterior("?numeroPagina=" + (pagina.getNumber() - 1) +
                    "&tamanoPagina=" + pagina.getSize());
        }
        
        return paginaDTO;
    }
	
	@Override
	@Transactional(
		    transactionManager = "tenantTransactionManager",
		    readOnly = true
		)
    public PaginaDto<PadronElectoralExportOrcDto> importarPadronesOrc(String cc, int numeroPagina, int tamanoPagina) {
        Pageable pageable = PageRequest.of(numeroPagina, tamanoPagina);
        Page<PadronElectoral> pagina = padronElectoralRepository.importarOrc(cc,pageable);
        PaginaDto<PadronElectoralExportOrcDto> paginaDTO = new PaginaDto<>();
        List<PadronElectoral> registros = pagina.getContent();
        List<PadronElectoralExportOrcDto> registrosDTO = null;
        
        if(registros!=null) {
        	registrosDTO = registros.stream().map(registro -> {
        		PadronElectoralExportOrcDto dto = new PadronElectoralExportOrcDto();
        		dto.setId(registro.getId());
        		dto.setIdMesa(registro.getMesa().getId());
        		dto.setCodigoMesa(registro.getCodigoMesa());
        		dto.setIdTipoDocumentoIdentidad(registro.getIdTipoDocumentoIdentidad());
        		dto.setDocumentoIdentidad(registro.getDocumentoIdentidad());
        		dto.setNombres(registro.getNombres());
        		dto.setApellidoPaterno(registro.getApellidoPaterno());
        		dto.setApellidoMaterno(registro.getApellidoMaterno());
        		dto.setOrden(registro.getOrden());
        		dto.setUbigeo(registro.getUbigeo());
        		dto.setUbigeoReniec(registro.getUbigeoReniec());
        		dto.setSexo(registro.getSexo());
        		dto.setVd(registro.getVd());
        		dto.setActivo(registro.getActivo());
        		dto.setAudFechaCreacion(DateUtil.getDateString(registro.getFechaCreacion(), "dd-MM-yyyy HH:mm:ss"));
        		dto.setAudFechaModificacion(DateUtil.getDateString(registro.getFechaModificacion(), "dd-MM-yyyy HH:mm:ss"));
        		dto.setAudUsuarioCreacion(registro.getUsuarioCreacion());
        		dto.setAudUsuarioModificacion(registro.getUsuarioModificacion());
        		return dto;
        	}).collect(Collectors.toList());
        }
        
        paginaDTO.setData(registrosDTO);
        paginaDTO.setTotalRegistros(pagina.getTotalElements());
        paginaDTO.setTotalPaginas(pagina.getTotalPages());
        paginaDTO.setNext(pagina.hasNext());
        
        if (pagina.hasNext()) {
            paginaDTO.setPaginaSiguiente("?numeroPagina=" + (pagina.getNumber() + 1) +
                    "&tamanoPagina=" + pagina.getSize());
        }

        if (pagina.hasPrevious()) {
            paginaDTO.setPaginaAnterior("?numeroPagina=" + (pagina.getNumber() - 1) +
                    "&tamanoPagina=" + pagina.getSize());
        }
        
        return paginaDTO;
    }
	
	@Override
	@Transactional(
		    transactionManager = "tenantTransactionManager",
		    readOnly = true
		)
	public PaginaOptDto<PadronElectoralExportOrcDto> importarOpPadronesOrc(
			String cc, Long lastId, int tamanoPagina
			) {
		Pageable limit = PageRequest.of(0, tamanoPagina);
        List<PadronElectoral> registros = padronElectoralRepository.importarOptOrc(cc, lastId, limit);

        PaginaOptDto<PadronElectoralExportOrcDto> dto = new PaginaOptDto<>();
        dto.setData(registros.stream().map(this::toDto).toList());
        dto.setNext(registros.size() == tamanoPagina);

        Long nextLastId = registros.isEmpty() ? lastId : registros.get(registros.size()-1).getId();
        dto.setLastId(nextLastId);

        return dto;
    }
	
	private PadronElectoralExportOrcDto toDto(PadronElectoral entity){
		return this.padronMapper.toDto(entity);
	}
	
	@Override
	@Transactional(
		    transactionManager = "tenantTransactionManager",
		    readOnly = true
		)
    public int contabilizar(String cc) {
        return padronElectoralRepository.cantidadPorCc(cc);
    }

}
