package com.diviso.graeshoppe.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.beans.factory.annotation.Value;

/**
 * Configures Spring Cloud Stream support.
 *
 * This works out-of-the-box if you use the Docker Compose configuration at "src/main/docker/kafka.yml".
 *
 * See http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/
 * for the official Spring Cloud Stream documentation.
 */
@EnableBinding(value = { MessageBinderConfiguration.class })
public class MessagingConfiguration {

    @Value("${spring.application.name:JhipsterService}")
    private String applicationName;

}
