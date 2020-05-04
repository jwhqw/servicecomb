package com.cosin.servicecomb.provider.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * <p>ClassName: servicecomb-demo</p>
 * <p>Description: ${TODO} </p>
 * <p>Company:华云数据 </p>
 * <p>@Author: wanqian </p>
 * <p>@Date:  Created in 10:36 2019/11/1</p>
 */
@Slf4j
@Configuration
public class DataSourcesConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource memberDataSource() {
        log.debug(" data-source init...");
        return DataSourceBuilder.create().build();
    }
}
