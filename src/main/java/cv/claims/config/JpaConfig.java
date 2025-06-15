package cv.claims.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = JpaRepositoryFactoryBean.class, basePackages = "cv.claims.infrastructure.db")
@EntityScan(basePackages = "cv.claims.infrastructure.db")
public class JpaConfig {
}
