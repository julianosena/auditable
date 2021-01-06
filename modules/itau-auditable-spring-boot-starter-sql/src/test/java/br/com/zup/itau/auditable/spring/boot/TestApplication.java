package br.com.zup.itau.auditable.spring.boot;

import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Map;

/**
 * @author pawelszymczyk
 */
@SpringBootApplication
@ComponentScan("br.com.zup.itau.auditable.spring.boot.sql")
public class TestApplication {
    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provide() {
                return Maps.of("deprecated commitPropertiesProvider.provide()", "still works");
            }
        };
    }
}
