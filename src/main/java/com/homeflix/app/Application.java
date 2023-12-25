package com.homeflix.app;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@NpmPackage(value = "@fontsource/montserrat", version = "4.5.0")
@Theme(value = "homeflix", variant = Lumo.DARK)
@PWA(name = "HomeFlix", shortName = "HomeFlix", backgroundColor = "#000000")
@Viewport("width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=no")
@Meta(name = "HandheldFriendly", content = "true")
@Meta(name = "mobile-web-app-capable", content = "yes")
@Meta(name = "apple-mobile-web-app-capable", content = "yes")
@Meta(name = "apple-mobile-web-app-status-bar-style", content = "black-translucent")
@EnableScheduling
@Push
@Slf4j
public class Application implements AppShellConfigurator {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Scheduled(fixedDelay = 10000L)
    void call() {
        var response = REST_TEMPLATE.getForEntity("https://homeflix.onrender.com/actuator/app-health", String.class);
        log.info("Response code {}", response.getStatusCode());
    }
}
