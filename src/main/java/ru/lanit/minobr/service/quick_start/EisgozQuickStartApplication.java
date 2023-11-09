package ru.lanit.minobr.service.quick_start;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EisgozQuickStartApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EisgozQuickStartApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
