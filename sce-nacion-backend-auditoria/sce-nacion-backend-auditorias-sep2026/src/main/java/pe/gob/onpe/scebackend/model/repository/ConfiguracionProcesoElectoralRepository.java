package pe.gob.onpe.scebackend.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ConfiguracionProcesoElectoralRepository extends JpaRepository<ConfiguracionProcesoElectoral, Integer>, MigracionRepository<ConfiguracionProcesoElectoral, String>{

    List<ConfiguracionProcesoElectoral> findByActivoOrderById(Integer activo);

    @Modifying
    @Query("update ConfiguracionProcesoElectoral pro set pro.activo = :estado where pro.id = :id")
    void updateEstado(@Param("estado") Integer estado, @Param("id") Integer id);

    @Query("SELECT cg FROM ConfiguracionProcesoElectoral cg WHERE cg.acronimo = ?1")
    List<ConfiguracionProcesoElectoral> findByCc(String acronimo);
    
    @Transactional("tenantTransactionManager")
    @Query("SELECT cg FROM ConfiguracionProcesoElectoral cg WHERE cg.acronimo = ?1")
    ConfiguracionProcesoElectoral findByProceso(String proceso);
    
    @Query("SELECT cg FROM ConfiguracionProcesoElectoral cg")
    List<ConfiguracionProcesoElectoral> findAll();
    
    @Query("SELECT cg.nombreEsquemaPrincipal FROM ConfiguracionProcesoElectoral cg WHERE cg.acronimo = ?1")
    String getEsquema(String proceso);

    List<ConfiguracionProcesoElectoral> findByVigente(Integer vigente);

    @Query("SELECT cg FROM ConfiguracionProcesoElectoral cg WHERE cg.activo = 1 and UPPER(cg.acronimo) = UPPER(:acronimo)")
    ConfiguracionProcesoElectoral findByAcronimo(@Param("acronimo") String acronimo);

    @Query("SELECT cg FROM ConfiguracionProcesoElectoral cg WHERE cg.activo = 1 and  UPPER(cg.nombreEsquemaPrincipal) = UPPER(:esquemaPrincipal)")
    ConfiguracionProcesoElectoral findByNombreEsquemaPrincipal(@Param("esquemaPrincipal") String esquemaPrincipal);

    @Query(value = "select * from fn_listar_tipo_eleccion_documento_electoral_escrutinio(:pi_esquema,:pi_tipo_eleccion,:pi_aud_usuario_consulta)", nativeQuery = true)
    List<Map<String, Object>> listaTipoEleccionEscrutinio(@Param("pi_esquema") String piEsquema,
                                              @Param("pi_tipo_eleccion") Integer piTipoEleccion,
                                              @Param("pi_aud_usuario_consulta") String piAudUsuarioConsulta);

    @Query(value = "SELECT CURRENT_DATE", nativeQuery = true)
    LocalDate getCurrentDate();
    
    List<ConfiguracionProcesoElectoral> findByVigenteAndActivoOrderById(Integer vigente, Integer activo);

    @Transactional("tenantTransactionManager")
    @Modifying
    @Query("update ConfiguracionProcesoElectoral pro set pro.fechaModificacion = :fecha, pro.etapa = :etapa where pro.id = :id")
    void updateEtapa(@Param("etapa") Integer etapa, @Param("id") Integer id, @Param("fecha") Date fecha);

}
