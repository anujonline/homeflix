package com.homeflix.app.views.netflix;

import com.homeflix.app.data.models.VideoData;
import com.vaadin.flow.component.html.Image;

import static com.homeflix.app.views.netflix.PlayConstants.POSTER_URL;


public final class NetfliInterface {

    static Image getImage(VideoData videoFile) {
        var image = new Image(videoFile.poster() == null ? "icons/icon.png" : POSTER_URL.formatted(videoFile.poster()), "");
        image.setMaxHeight("20%");
        image.setWidth("20%");
        image.getStyle().set("margin", "3px");
        image.getStyle().set("display", "inline-flex");
        return image;
    }
}
