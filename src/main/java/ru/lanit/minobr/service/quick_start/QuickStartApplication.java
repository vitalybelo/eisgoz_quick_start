package ru.lanit.minobr.service.quick_start;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class QuickStartApplication {

    public static void main(String[] args) {

//        System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
//        System.setProperty("sun.security.krb5.debug", "true");
//        System.setProperty("java.security.auth.login.config", "/etc/jdbc-driver.conf");

        SpringApplication app = new SpringApplication(QuickStartApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
