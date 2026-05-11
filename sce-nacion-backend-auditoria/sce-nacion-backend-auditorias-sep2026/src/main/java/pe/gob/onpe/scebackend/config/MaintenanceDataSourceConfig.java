package pe.gob.onpe.scebackend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configuración del DataSource de Mantenimiento
 * Este datasource está dedicado exclusivamente para operaciones pesadas
 */
@Configuration
@EnableTransactionManagement
public class MaintenanceDataSourceConfig {

    @Bean
    @ConfigurationProperties("maintenance.datasource")
    public DataSourceProperties maintenanceDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "maintenanceDataSource")
    @ConfigurationProperties("maintenance.datasource.hikari")
    public DataSource maintenanceDataSource() {
        return maintenanceDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "maintenanceJdbcTemplate")
    public JdbcTemplate maintenanceJdbcTemplate(@Qualifier("maintenanceDataSource") DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // Timeout de 30 minutos para operaciones pesadas
        jdbcTemplate.setQueryTimeout(1800);
        return jdbcTemplate;
    }

    @Bean(name = "maintenanceTransactionManager")
    public PlatformTransactionManager maintenanceTransactionManager(
            @Qualifier("maintenanceDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
