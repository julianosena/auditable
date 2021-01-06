package br.com.zup.itau.auditable.spring.boot.mongo;

import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("br.com.zup.itau.auditable.spring.boot.mongo")
public class TestApplication {

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                if (domainObject instanceof DummyEntity) {
                    return Maps.of("dummyEntityId", ((DummyEntity)domainObject).getId() + "");
                }
                return Collections.emptyMap();
            }
        };
    }
}
