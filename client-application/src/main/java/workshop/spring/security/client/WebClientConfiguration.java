package workshop.spring.security.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfiguration {

    private final EnvironmentConfiguration config;

    public WebClientConfiguration(EnvironmentConfiguration config) {
        this.config = config;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(config.getResourceUrl()))
                .build();
    }
}
