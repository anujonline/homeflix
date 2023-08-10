package com.homeflix.app;

import com.homeflix.app.data.entity.MovieDatabase;
import com.homeflix.app.data.service.MovieRepository;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@NpmPackage(value = "@fontsource/montserrat", version = "4.5.0")
@Theme(value = "homeflix", variant = Material.DARK)
@PWA(name = "HomeFlix", shortName = "HomeFlix")
@Viewport("width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=no")
@Meta(name = "HandheldFriendly", content = "true")
@Meta(name = "mobile-web-app-capable", content = "yes")
@Meta(name = "apple-mobile-web-app-capable", content = "yes")
@Meta(name = "apple-mobile-web-app-status-bar-style", content = "black-translucent")
@EnableScheduling
public class Application implements AppShellConfigurator {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    @Autowired
    private MovieRepository movieRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static String createIdentifier(String url) {
        var uri = URI.create(url);
        var split = uri.getPath().split("/");
        return split[2];
    }

    @Scheduled(fixedDelay = 10000L)
    void call() {
        var response = REST_TEMPLATE.getForEntity("https://homeflix.onrender.com", String.class);
        System.out.println(response.getStatusCode());
    }

    @PostConstruct
    void addMovies() {
        saveMovie("Little Mermaid", "https://eerht.artdesigncdn.net/_v10/165ffb574f2d1baf64d5e0a7fabb9961f40581c41058d15d4d9a07b766853eef6560ec5d1b88da900ca632875404ea0413ed9f72a6ec06ce086348ebe03f97fd4b276180a6099d4bf8a0eadef73146e150534fcbe86f0419b09feb05136aad24ea31883549e52eb3abfb86c1dea4b106867b165a20f55ead06722cd2dcaf5e551f68816194d5c8a1661a455c923b033d/720/index.m3u8", "https://media-cache.cinematerial.com/p/500x/u0a2cwcc/little-mermaid-movie-poster.jpg?v=1479369946");
        saveMovie("Man from UNCLE", "https://eerht.artdesigncdn.net/_v10/c21283078281e4cbd1bfa4a356d7bf29a50a6dc6880b1cab2bbf7a60f8758afa831765638d5abbbd79b086db4a8fa7c99bffc882f67a6360002571a9bbb05423138d3cbedddf78fb743fa71e3f782d23f7b202f71f7d51b388d153fdc225367b860f452d333419316ac27b6ecafd7da3473ae970618eb4a1505b43369c0aaee1a6499a64a2b8ab40f402b34fcfa0262f/playlist.m3u8", "https://www.moviemeter.nl/images/cover/100000/100866.jpg?cb=1460039091");
        saveMovie("Oppenheimer", "https://xis.fifteennet.com/_v10/0db3086e7a22f9ed3b28cde10c2ebd49454fbf30f0a3acca80dc4a0e67d9493b7522ca49e837e91d14929942b074b49fc751e8f52948d9619cdceff79161cdd57ef9535b4b00468f85188e51527b849a0dbefc3956afe559a8adaf6fc8589fb62d87bcd5db5903dd0273d3138b2649d990881c4b2c04910ae242d2e8a927cc4999bf543291abdd0062c8d28608331831/720/index.m3u8", "https://media-cache.cinematerial.com/p/500x/2afqhdxx/oppenheimer-movie-poster.jpg?v=1683305737");
        saveMovie("Before Sunrise", "https://neves.jeffycontent.com/_v10/d7b50b552eb03ef2ed80bc8610b845c7ed6fbdf7d4ae29e4076216cc826f3e16c7804aecbf1fe0298cd15b34a8ce013d24d78d9d2bfa09ed317fc6f9a8ab79472b23002298aba8a380806a756a1ed541959a524647fac1af738b8c834d263fc90bd7676a700f10065154bbe27566b28658a918b15bbe9c8f02ff3a6d3fa5264dab206a5b29e8926b0cf2188219a36c2d/playlist.m3u8", "https://media-cache.cinematerial.com/p/500x/at0bkxjz/before-sunrise-movie-poster.jpg?v=1456716574");
    }

    private void saveMovie(String name, String url, String posterLink) {
        MovieDatabase movie = new MovieDatabase();
        movie.setName(name);
        movie.setIdentifier(createIdentifier(url));
        movie.setLink(URI.create(url));
        movie.setPosterLink(posterLink);
        movie.setActive(true);
        movieRepository.save(movie);
    }
}
