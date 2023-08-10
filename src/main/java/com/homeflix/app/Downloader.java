package com.homeflix.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflix.app.views.Item;
import com.homeflix.app.views.Response;
import org.apache.commons.io.FileUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Downloader {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public static void main(String[] args) throws Exception{
        var objectMapper = new ObjectMapper();
        var items = new ArrayList<Item>();
        IntStream
                .range(1,100)
                .forEach(value -> {
                    try{
                        var response = REST_TEMPLATE.getForObject("https://vidsrc.to/vapi/movie/add/%s".formatted(value), Response.class);
                        assert response != null;
                        items.addAll(response.result().items());
                        System.out.println(LocalDateTime.now() + " Running, total size so far :" +items.size());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                });

        FileUtils.write(new File("/Users/anuj/IdeaProjects/homeflix 2/src/main/resources/moviedb.json"), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items), StandardCharsets.UTF_8);
    }
}
