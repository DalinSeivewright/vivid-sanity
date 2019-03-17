package ca.logichromatic.vividsanity.configuration.database;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.repository.external.ExternalImageInfoRepository;
import ca.logichromatic.vividsanity.service.proxy.PublicImageProxyService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "externalEntityManager", transactionManagerRef = "externalTransactionManager", basePackageClasses = ExternalImageInfoRepository.class )
public class ExternalDatasourceConfiguration {

    @Bean(name = "externalDatasource")
    @ConfigurationProperties(prefix = "vivid.external.datasource")
    public DataSource externalDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "externalEntityManager")
    public LocalContainerEntityManagerFactoryBean externalEntityManager( EntityManagerFactoryBuilder builder, @Qualifier("externalDatasource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(ImageInfo.class)
                .persistenceUnit("external").build();
    }

    @Bean(name = "externalTransactionManager")
    public PlatformTransactionManager externalTransactionManager(@Qualifier("externalEntityManager") EntityManagerFactory externalEntityManager) {
        return new JpaTransactionManager(externalEntityManager);
    }

}
