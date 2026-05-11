package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.CargaDataRequestDTO;
import pe.gob.onpe.scebackend.model.dto.request.ConfiguracionProcesoElectoralRequestDTO;
import pe.gob.onpe.scebackend.model.dto.request.ProcesoElectoralOtherRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.ConfiguracionProcesoElectoralResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;

import java.util.List;
import java.util.Optional;

public interface IConfiguracionProcesoElectoralService {

    ConfiguracionProcesoElectoralResponseDTO guardarConfiguracionProcesoElectoral(ConfiguracionProcesoElectoralRequestDTO procesoElectoralRequestDTO, String usuario) throws GenericException;
    List<ConfiguracionProcesoElectoralResponseDTO> listAll(Integer activo);

    GenericResponse actualizarEstado(Integer estado, Integer id, String usuario);

    GenericResponse cargardatos(CargaDataRequestDTO request) throws GenericException;
    
    String getEsquema(String proceso);

    ConfiguracionProcesoElectoralResponseDTO getProcesoVigente();

    GenericResponse cargarUsuarios(String tentat, String usuario, String clave) throws GenericException;

    boolean actualizarPrincipal(CargaDataRequestDTO request) throws GenericException;

    List<DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO> listaTipoEleccionEscrutinio(ProcesoElectoralOtherRequestDTO request);

    List<ConfiguracionProcesoElectoral> listarActivos();
    
    List<ConfiguracionProcesoElectoral> listarVigentesYActivos();

    void actualizarEtapaProceso( Integer idProceso, boolean isUpdate);
    
    Optional<ConfiguracionProcesoElectoral> findByAcronimo(Integer idProceso);
    
    ConfiguracionProcesoElectoral findByProceso(String proceso);
}
