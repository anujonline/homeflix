package com.homeflix.app;

import com.homeflix.app.views.JsonLd;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

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

    /**
     * Generates and injects Microdata, JSON-LD and Open Graph metadata into the head element of the HTML document.
     *
     * @param head          the head element of the HTML document
     * @param name          the name of the page
     *                      (e.g. "MyApp")
     * @param description   the description of the page
     *                      (e.g. "This is my application.")
     * @param url           the URL of the page
     *                      (e.g. "http://localhost:8080/")
     * @param imageUrl      the URL of the image to be used as a preview
     *                      (e.g. "http://localhost:8080/images/socialpreview.png")
     * @param datePublished the date when the page was published
     *                      (e.g. LocalDate.now())
     */
    public static void injectSeoAndSocialTags(Element head, String name, String description, String url, String imageUrl, LocalDate datePublished) {
        var jsonLd = JsonLd.webSite(name, description, url, imageUrl);
        head.appendElement("script").attr("type", "application/ld+json").appendText(jsonLd.toJson());

        addMicrodataMetaTag(head, "name", name);
        addMicrodataMetaTag(head, "description", description);
        addMicrodataMetaTag(head, "url", url);
        addMicrodataMetaTag(head, "datePublished", datePublished.toString());
        addMicrodataMetaTag(head, "image", imageUrl);

        addOpenGraphMetaTag(head, "title", name);
        addOpenGraphMetaTag(head, "description", description);
        addOpenGraphMetaTag(head, "url", url);
        addOpenGraphMetaTag(head, "image", imageUrl);
        addOpenGraphMetaTag(head, "type", "website");
        addOpenGraphMetaTag(head, "site_name", name);
    }

    static void addMicrodataMetaTag(Element head, String property, String content) {
        head.appendElement("meta").attr("itemprop", property).attr("content", content);
    }

    static void addOpenGraphMetaTag(Element head, String property, String content) {
        head.appendElement("meta").attr("property", "og:" + property).attr("content", content);
    }

    @Scheduled(fixedDelay = 10000L)
    void call() {
        var response = REST_TEMPLATE.getForEntity("https://homeflix.onrender.com", String.class);
        log.debug("Response code {}", response.getStatusCode());
    }

    @EventListener
    public void configureSeoAndSocialTags(ServiceInitEvent event) {
        log.info("SEO And social tags added");
        // Inject SEO and social tags to head html element when the index.html is requested
        event.addIndexHtmlRequestListener(response -> {
            Element head = response.getDocument().head();
            injectSeoAndSocialTags(head, "Homeflix", "Homeflix watch movies and web series online free.", "https://homeflix.onrender.com", "https://homeflix.onrender.com/icons/icon-144x144.png", LocalDate.now());
        });
    }
}
