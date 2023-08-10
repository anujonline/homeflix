package com.homeflix.app.data.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;


@RestController
@RequestMapping("/video")
public class VideoController<T> {
    private final RestTemplate restTemplate = new RestTemplate();

    private static boolean isMetaDataRequest(String identifier) {
        return identifier.contains(".m3u8");
    }

    private static URI generateURI(URI uri, String newFilename) {
        try {
            String path = uri.getPath();
            int lastSlashIndex = path.lastIndexOf('/');
            String newPath = path.substring(0, lastSlashIndex + 1) + newFilename;
            return new URI(uri.getScheme(), uri.getAuthority(), newPath, uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/identifier/{identifier}")
    ResponseEntity<?> getsrc(@CookieValue("mainURL") String cookie, @PathVariable("identifier") String identifier) {
        try {
            identifier = URLDecoder.decode(URLDecoder.decode(identifier, Charset.defaultCharset()), Charset.defaultCharset());
            if (isMetaDataRequest(identifier)) {
                return restTemplate.getForEntity(identifier, String.class);
            } else {
                var uri = generateURI(URI.create(cookie), identifier);
                return restTemplate.getForEntity(uri, byte[].class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
