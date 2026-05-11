package pe.gob.onpe.scebackend.multitenant;

import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
	basePackages = {
			"pe.gob.onpe.scebackend.model.orc.repository",
			"pe.gob.onpe.scebackend.repository"},
    entityManagerFactoryRef="locationEntityManagerFactory",
    transactionManagerRef = "locationTransactionManager")
public class LocationPersistenceConfig {

	private final DataSourceConfig dataSourceConfig;

    private final LocationDataSourceMap locationDataSourceMap;

    public LocationPersistenceConfig(DataSourceConfig dataSourceConfig, LocationDataSourceMap locationDataSourceMap) {
        this.dataSourceConfig = dataSourceConfig;
        this.locationDataSourceMap = locationDataSourceMap;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean locationEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(locationDataSource());
        em.setPackagesToScan("pe.gob.onpe.scebackend.model.orc.entities");
        em.setPersistenceUnitName("location_pu");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource locationDataSource() {
        LocationRoutingDatasource ds = new LocationRoutingDatasource();
        ds.setTargetDataSources(locationDataSourceMap);
        return ds;
    }

    @Bean
    public PlatformTransactionManager locationTransactionManager(@Qualifier("locationEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor locationExceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", dataSourceConfig.getShowSql()); // Para mostrar las consultas SQL
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("org.springframework.jdbc.core.JdbcTemplate", "TRACE");
        properties.setProperty("org.springframework.jdbc.core", "TRACE");
        return properties;
    }
    
    @Bean(name = "namedParameterJdbcTemplateNacion")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateNacion(@Qualifier("locationDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Bean(name = "jdbcTemplateNacion")
    public JdbcTemplate jdbcTemplateNacion(@Qualifier("locationDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
	
	
}
