package pe.gob.onpe.scebackend.multitenant;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"pe.gob.onpe.scebackend.model.repository" },
                                        entityManagerFactoryRef = "tenantEntityManagerFactory",
                                        transactionManagerRef = "tenantTransactionManager")
public class PersistenceConfig {
	
	@Autowired
	private DataSourceConfig dataSourceConfig;
	
    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(tenantDataSource());
        //List<String> packageList = Arrays.asList("pe.gob.onpe.scebackend.model.entities", "pe.gob.onpe.scebackend.model.orc.entities");
        //em.setPackagesToScan(packageList.toArray(new String[0]));
        em.setPackagesToScan("pe.gob.onpe.scebackend.model.entities");
        em.setPersistenceUnitName("tenant_pu");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean(name = "tenantDataSource")
    public DataSource tenantDataSource() {
        return createHikariPoolAdmin();
    }

    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.show_sql", dataSourceConfig.getShowSql()); // Para mostrar las consultas SQL
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }
    
    @Bean(name = "namedParameterJdbcTemplateAdmin")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateAdmin(@Qualifier("tenantDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Bean(name = "jdbcTemplateAdmin")
    public JdbcTemplate jdbcTemplateAdmin(@Qualifier("tenantDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    private HikariDataSource createHikariPoolAdmin() {
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl(dataSourceConfig.getUrl());
        config.setUsername(dataSourceConfig.getUsername());
        config.setPassword(dataSourceConfig.getPassword());
        config.setSchema(dataSourceConfig.getSchemaAdmin());
        config.setDriverClassName("org.postgresql.Driver");
        config.setPoolName("HikariPool-admin"+"-"+hostname());
        
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
}