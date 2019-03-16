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
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "externalEntityManager", basePackageClasses = ExternalImageInfoRepository.class )
public class ExternalDatasourceConfiguration {

    @Bean(name = "externalDatasource")
    @ConfigurationProperties(prefix = "vivid.external.datasource")
    public DataSource externalDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean externalEntityManager( EntityManagerFactoryBuilder builder, @Qualifier("externalDatasource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(ImageInfo.class)
                .persistenceUnit("external").build();
    }

}
