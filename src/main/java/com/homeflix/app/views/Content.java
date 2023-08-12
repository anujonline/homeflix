package com.homeflix.app.views;

import com.homeflix.app.data.controllers.AdminController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;

public class Content {
    private static final String PLAY_URL = "https://vidsrc.to/embed/%s/%s";
    private static final String POSTER_URL = "https://image.tmdb.org/t/p/original/%s";

    Div showContent(AdminController adminController, String type, Dialog dialog, Embed embed, Result1 videoFile) {
        var movieDiv = new Div();
        movieDiv.getStyle().set("display", "inline-block");
        movieDiv.setHeight("20%");
        movieDiv.setWidth("20%");

        Image image = new Image(videoFile.poster_path() == null ? "icons/icon.png" : POSTER_URL.formatted(videoFile.poster_path()), "");
        
        image.setHeight("100%");
        image.setWidth("90%");
        image.getStyle().set("object-fit", "cover");
        image.getStyle().set("margin", "10px");
        image.getStyle().set("opacity", "0.9");
        image.getStyle().set("cursor", "pointer");
        image.getStyle().set("box-shadow", "10px 10px 10px black");
        image.getStyle().set("transition", "all 0.3s ease 0s");

        image.addClickListener(clickEvent -> {
            var ui = UI.getCurrent();
            ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
                var data = new FeedbackData(videoFile.id(), extendedClientDetails.getCurrentDate(), extendedClientDetails.getTimeZoneId(), extendedClientDetails.isTouchDevice(), extendedClientDetails.getWindowName());
                adminController.addHistory(ui.getSession().getBrowser().getAddress(), data.toString());
            });

            ui.access(() -> {
                embed.setSrc(PLAY_URL.formatted(type, videoFile.id()));
                dialog.open();
            });

        });
        var text = new NativeLabel(videoFile.original_title() == null ? videoFile.name() : videoFile.original_title());

        text.getStyle().set("color", "white");
        text.getStyle().set("display", "inline-block");
        text.getStyle().set("overflow", "hidden");
        text.getStyle().set("text-overflow", "ellipsis");
        text.getStyle().set("white-space", "nowrap");

        text.setSizeFull();
        text.setWidthFull();

        movieDiv.add(image);
        movieDiv.add(text);
        return movieDiv;
    }
}
