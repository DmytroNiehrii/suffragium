package io.suffragium.CustomerService;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.persistence.Entity;
import java.util.Set;

/*
* It finds all beans with the @Entity annotation and exposes id field for them.
* */

@Configuration
@EntityScan("io.suffragium.common.entity.customer")
public class RepositoryRestConfig extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(final RepositoryRestConfiguration config) {

        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        final Set<BeanDefinition> beans = provider.findCandidateComponents("io.suffragium.CustomerService");

        for (final BeanDefinition bean : beans) {
            try {
                config.exposeIdsFor(Class.forName(bean.getBeanClassName()));
            } catch (final ClassNotFoundException e) {
                // Can't throw ClassNotFoundException due to the method signature. Need to cast it
                throw new IllegalStateException("Failed to expose `id` field due to", e);
            }
        }
    }
}