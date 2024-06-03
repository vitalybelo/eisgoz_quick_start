package ru.lanit.minobr.service.quick_start.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class SpringJdbcConfig {

    @Value("${spring.datasource.url}")
    private String jdbcURL;
    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    @Bean
    public DataSource postgresqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

//        System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
//        System.setProperty("java.security.krb5.realm", "TEST.LAN");
//        System.setProperty("sun.security.krb5.debug", "true");
//        System.setProperty("java.security.krb5.kdc", "server.test.lan");
//        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
//        System.setProperty("java.security.auth.login.config", "/etc/jaas.conf");

        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(jdbcURL);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

}
