package pe.gob.onpe.scebackend.multitenant;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.repository.ConfiguracionProcesoElectoralRepository;

@SuppressWarnings("serial")
@Slf4j
@Component
public class LocationDataSourceMap extends HashMap<Object, Object> implements ApplicationContextAware, DisposableBean {

	private ApplicationContext applicationContext;

	private final DataSourceConfig dataSourceConfig;
	
	private static final String DEFAULT="DEFAULT";

    public LocationDataSourceMap(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
    public Object get(Object key) {
		
		if(key == null){
			
			HikariDataSource hikariDS = (HikariDataSource) super.get(DEFAULT);
			
			if(hikariDS==null){
				HikariDataSource newPool = createHikariPool(DEFAULT, dataSourceConfig.getSchemaOrcDefault());
				super.put(DEFAULT, newPool);
		        return newPool;
			} // end-if
			
			return hikariDS;

		}
		
		
        Object value = super.get(key);
        if (value == null) {

        	ConfiguracionProcesoElectoralRepository repo = applicationContext.getBean(ConfiguracionProcesoElectoralRepository.class);
        	ConfiguracionProcesoElectoral tenant = repo.findByProceso((String) key);
            if (tenant != null) {
            	log.info("El usuario tiene registrado el esquema = {}", tenant.getNombreEsquemaPrincipal());
                HikariDataSource hikariDS = createHikariPool(key.toString(), tenant.getNombreEsquemaPrincipal());
                super.put(key, hikariDS);
                return hikariDS;
            }
        } else {
        	log.info("El datasource ya se encuentra mapeado producto de una consulta anterior");
        }
        return value;
    }
	
	private HikariDataSource createHikariPool(String effectiveKey, String esquema) {
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl(dataSourceConfig.getUrl());
        config.setUsername(dataSourceConfig.getUsername());
        config.setPassword(dataSourceConfig.getPassword());
        config.setSchema(esquema.toLowerCase());
        config.setDriverClassName("org.postgresql.Driver");
        config.setConnectionInitSql("SET search_path TO "+esquema.toLowerCase());
        config.setPoolName("HikariPool-"+effectiveKey+"-"+hostname());
        
        // Configuración del pool
        config.setMaximumPoolSize(dataSourceConfig.getMaximumPoolSize());
        config.setMinimumIdle(dataSourceConfig.getMinimumIdle());             
        config.setConnectionTimeout(dataSourceConfig.getConnectionTimeout());     
        config.setIdleTimeout(dataSourceConfig.getIdleTimeout());       
        config.setMaxLifetime(dataSourceConfig.getMaxLifetime());   
        config.setConnectionTestQuery(dataSourceConfig.getConnectionTestQuery());
        
        return new HikariDataSource(config);
    }
	
	private String hostname(){
    	InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			return localhost.getHostName();
		} catch (UnknownHostException e) {
			return "uknow";
		}
        
    }
	
	@Override
    public void destroy() throws Exception {
        log.info("Destruyendo LocationDataSourceMap - Cerrando todos los pools");
        
        this.values().forEach(obj -> {
            if (obj instanceof HikariDataSource) {
                HikariDataSource ds = (HikariDataSource) obj;
                if (!ds.isClosed()) {
                    log.info("Cerrando pool: {}", ds.getPoolName());
                    ds.close();
                }
            }
        });
        
        this.clear();
        log.info("Todos los pools cerrados correctamente");
    }

}
