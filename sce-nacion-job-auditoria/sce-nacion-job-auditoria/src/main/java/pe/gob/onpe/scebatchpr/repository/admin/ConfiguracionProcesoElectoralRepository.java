package pe.gob.onpe.scebatchpr.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.gob.onpe.scebatchpr.entities.admin.ConfiguracionProcesoElectoral;

import java.util.List;

public interface ConfiguracionProcesoElectoralRepository extends JpaRepository<ConfiguracionProcesoElectoral, Integer>{

    List<ConfiguracionProcesoElectoral> findByActivo(Integer activo);

    //@Transactional("tenantTransactionManager")
    @Query("SELECT cg FROM ConfiguracionProcesoElectoral cg WHERE cg.acronimo = ?1")
    public ConfiguracionProcesoElectoral findByProceso(String proceso);
    
    @Query("SELECT distinct c FROM ConfiguracionProcesoElectoral c WHERE c.vigente = 1")
    List<ConfiguracionProcesoElectoral> findByVigente();

    ConfiguracionProcesoElectoral findByAcronimo(String acronimo);
    
    @Query("SELECT distinct c FROM ConfiguracionProcesoElectoral c WHERE c.vigente = 1")
	public List<ConfiguracionProcesoElectoral> buscarVigentes();
    
    @Query("SELECT distinct c FROM ConfiguracionProcesoElectoral c WHERE UPPER(TRIM(c.nombreEsquemaPrincipal)) = ?1 and c.vigente = 1")
	public ConfiguracionProcesoElectoral findByEsquema(String esquema);



}
