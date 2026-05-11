package pe.gob.onpe.scebackend.multitenant;

import java.util.Map;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.AbstractDataSource;

import com.zaxxer.hikari.HikariDataSource;


@Slf4j
public class LocationRoutingDatasource extends AbstractDataSource {
	

	private Map<Object, Object> targetDataSources;
	
	@Autowired
	private DataSourceConfig dataSourceConfig;

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }

    protected DataSource determineTargetDataSource() {
        Object lookupKey = CurrentTenantId.get();
        String effectiveKey = (lookupKey != null) ? lookupKey.toString() : null;
        log.info("lookupKey= {} ", effectiveKey);
        return (HikariDataSource) this.targetDataSources.get(effectiveKey);
    }
    
    
    

	
}
