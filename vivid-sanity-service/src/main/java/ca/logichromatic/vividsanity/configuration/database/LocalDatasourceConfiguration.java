package ca.logichromatic.vividsanity.configuration.database;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.repository.local.LocalImageInfoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "localEntityManager", basePackageClasses = LocalImageInfoRepository.class )
@ConditionalOnProperty(prefix="vivid", name="serverMode", havingValue = "local")
public class LocalDatasourceConfiguration {

    @Bean(name = "localDatasource")
    @ConfigurationProperties(prefix = "vivid.local.datasource")
    @Primary
    public DataSource localDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean localEntityManager( EntityManagerFactoryBuilder builder, @Qualifier("localDatasource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(ImageInfo.class)
                .persistenceUnit("local").build();
    }

}
