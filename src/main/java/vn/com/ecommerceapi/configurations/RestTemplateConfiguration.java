package vn.com.ecommerceapi.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateConfiguration.class);

    @Value("${rest-template.timeout}")
    private long timeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(timeout))
                .setReadTimeout(Duration.ofSeconds(timeout))
                .build();
        LOGGER.info("[REST TEMPLATE] Initializing RestTemplate. Timeout: {} Seconds.", timeout);
        return restTemplate;
    }
}
