package com.homeflix.app;

import com.homeflix.app.data.entity.MovieDatabase;
import com.homeflix.app.data.service.MovieRepository;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

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
@PWA(name = "HomeFlix", shortName = "HomeFlix")
@Viewport("width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=no")
@Meta(name = "HandheldFriendly", content = "true")
@Meta(name = "mobile-web-app-capable", content = "yes")
@Meta(name = "apple-mobile-web-app-capable", content = "yes")
@Meta(name = "apple-mobile-web-app-status-bar-style", content = "black-translucent")
@EnableScheduling
@Push
public class Application implements AppShellConfigurator {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    @Autowired
    private MovieRepository movieRepository;

    public static void main(String[] args) throws Exception {
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
        saveMovie("Rocky aur Rani KPK", "https://si.videoapne.co/hls/bdohxlip7bboxuzvtarp4gisrbfbjat4liobzm7rmxyae3yqswitw56vq2iq/index-v1-a1.m3u8", "https://cdn.bollywoodmdb.com/fit-in/movies/largethumb/2022/rocky-aur-rani-ki-prem-kahani/rocky-aur-rani-ki-prem-kahani-poster-10.jpg");
        saveMovie("Satya Prem Ki Katha", "https://s2.videoapne.co/hls/bdohwdij7bboxuzvtarp4gkw3mghkgbvcairvc34vemlslqs3os4sekeflha/index-v1-a1.m3u8", "https://assets.gadgets360cdn.com/pricee/assets/product/202302/story_1675872661.jpg?downsize=680:*");
        saveMovie("Gadar 2", "https://si.videoapne.co/hls/bdohwtij7bboxuzvtarp4bantkzijwybi667yrl4uhijukifkv4dmped6vqq/index-v1-a1.m3u8", "https://feeds.abplive.com/onecms/images/uploaded-images/2023/01/26/567e995f9865a78721f8978618c642701674718080896380_original.jpg?impolicy=abp_cdn&imwidth=720");
        saveMovie("Adipurush", "https://s2.videoapne.co/hls/bdohwvyj7bboxuzvtarp4fytrttxokylslwh7qcjkrqz466zpiemhkfyzqwq/index-v1-a1.m3u8", "https://cdn.123telugu.com/content/wp-content/uploads/2023/03/Adipurush.jpg");
        saveMovie("OMG 2", "https://si.videoapne.co/hls/bdohwtaj7bboxuzvtarp4tywrwdsfm37pm4mgqw3g4f3pfdpcmqhwt4sydva/index-v1-a1.m3u8", "https://cdn.bollywoodmdb.com/fit-in/movies/largethumb/2022/omg-2/poster.jpg");

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
