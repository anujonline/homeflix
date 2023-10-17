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
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
@PWA(name = "HomeFlix", shortName = "HomeFlix", offlinePath = "offline.html")
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
        saveMovie("Jawan", "https://si.videoapne.co/hls/bdohxbrx7bboxuzvtarp4eiy3gcl7lzplue6uz6fjhsd5au2ctafafv7ltnq/index-v1-a1.m3u8", "https://www.cinejosh.com/newsimg/newsmainimg/jawan-fan-made-poster_b_0105230625.jpg");
        saveMovie("Dream Girl 2", "https://si.videoapne.co/hls/bdohwezu7bboxuzvtarp4dye3rufzpcmzobglghawx6ixghi3qkgsqpafdoq/index-v1-a1.m3u8", "https://cdn.bollywoodmdb.com/fit-in/movies/largethumb/2023/dream-girl-2/dream-girl-2-poster-3.jpg");
        saveMovie("Kaun Banegi Shikharwati E1","https://si.videoapne.co/hls/bdohwp5q7fboxuzvtasp4giatcm5jma4fos7fczmla6bskpy4bfk2weyle7q/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E2","https://si.videoapne.co/hls/bdohwpvq7fboxuzvtasp4eaot23zbbbkmzddrgsyacw4ro6konzfnwy3h54q/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E3","https://si.videoapne.co/hls/bdohwpnq7fboxuzvtasp4ecurane2ewzqiifvgzc2rw6ze6jb6pvp63kzz5q/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E4","https://si.videoapne.co/hls/bdohwpfq7fboxuzvtasp4c2q3kt5jhkpfij4pc4d2kykhmwjdfbjfrmly4ga/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E5","https://si.videoapne.co/hls/bdohwo5q7fboxuzvtasp4hagtmhmybcap5wv2xuf7ssirnk27kj6yddqmpra/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E6","https://si.videoapne.co/hls/bdohwovq7fboxuzvtasp4rin3sjn6fvyaerlqwtpo2fg7s56tdggqmatgnqa/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E7","https://si.videoapne.co/hls/bdohwonq7fboxuzvtasp4tsx3vgvsnsh2y67vco46bst2hlcqny6bf2bmrbq/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E8","https://si.videoapne.co/hls/bdohwofq7fboxuzvtasp4dqnt6un5hipucj6ye63o3cbdiucsynxyols62kq/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E9","https://si.videoapne.co/hls/bdohwn5q7fboxuzvtasp4e2srnfkzwh2ipldckjfic3q7suouwpt5pdojlua/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Kaun Banegi Shikharwati E10","https://si.videoapne.co/hls/bdohwnvq7fboxuzvtasp4bkzqslrb3ouswrjrbirrvnmog2esxay3fwnrqbq/index-v1-a1.m3u8","https://media-cache.cinematerial.com/p/500x/djo2zouc/kaun-banegi-shikharwati-indian-movie-poster.jpg?v=1641936776");
        saveMovie("Fukrey 3","https://s2.videoapne.co/hls/bdohx4bt7bboxuzvtar74fql3bjzdzxyskpn5g4pa57yemaym2luwgavcpia/index-v1-a1.m3u8","https://cdn.bollywoodmdb.com/fit-in/movies/largethumb/2022/fukrey-3/fukrey-3-poster-6.jpg");

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

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.initialize();;
        return threadPoolTaskExecutor;
    }
}
