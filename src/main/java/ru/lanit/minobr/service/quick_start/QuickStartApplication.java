package ru.lanit.minobr.service.quick_start;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuickStartApplication {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(QuickStartApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
