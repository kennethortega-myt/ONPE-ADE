package pe.gob.onpe.scebackend.model.service;


public interface UtilSceService {

    String getVersionSistema();

    String getSinValorOficial();

    String getSinValorOficial(Long idProceso);

    String getSinValorOficial(Integer idProceso);

    String getSinValorOficial(String acronimoProceso);

    String getResolucionJne();
}
