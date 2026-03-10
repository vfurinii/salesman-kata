package org.vitorfurini.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class DataSourceConfig {

    @PostConstruct
    public void init() throws InterruptedException {
        log.info("Waiting 3 seconds before initializing database connection...");
        Thread.sleep(3000);
        log.info("Database connection initialization starting...");
    }
}

