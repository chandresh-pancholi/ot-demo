package com.ot.demo.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ToString
public class AppConfig {

    @Value("${mongo.database.host}")
    private String mongoDBHost;

    @Value("${mongo.database.port}")
    private int mongoDBPort;

    @Value("${mongo.database.username}")
    private String mongoDBUsername;

    @Value("${mongo.database.password}")
    private String mongoDBPassword;

    @Value("${mongo.database.name}")
    private String mongoDBName;

    @Value("${mongo.database.min.connection}")
    private int mongoMinConnectionPerHost;

    @Value("${mongo.database.max.connection}")
    private int mongMaxConnectionPerHost;

}
