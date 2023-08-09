package com.homeflix.app.views.video;

import com.vaadin.flow.component.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@Tag("video")
@AnonymousAllowed
@CrossOrigin
public class Video extends HtmlContainer {


    public Video(String src) {
        setSizeFull();
        getElement().setProperty("controls", true);
        getElement().setAttribute("preload", "auto");
        getElement().setAttribute("autoplay", true);
        getElement().setAttribute("autoplay", true);
        getElement().setAttribute("data-setup", "{'fluid': true}");
        getElement().setAttribute("type", "video/mp4");
        setId("my-video");
        setClassName("video-js vjs-default-skin");
        add(new Src(src));
    }
}

@Tag("source")
@AnonymousAllowed
@CrossOrigin
class Src extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault(
            "src",
            ""
    );

    public Src(String src) {
        setId("vid-source");
        setSizeFull();
        setSrc("video/identifier/" + URLEncoder.encode(URLEncoder.encode(src, Charset.defaultCharset()), Charset.defaultCharset()));
    }


    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}