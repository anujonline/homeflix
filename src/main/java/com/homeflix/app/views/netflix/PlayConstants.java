package com.homeflix.app.views.netflix;

import com.homeflix.app.data.models.VideoData;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.function.Function;

import static com.vaadin.flow.server.VaadinSession.getCurrent;

public final class PlayConstants {
    public static final String PLAY_URL = "https://vidsrc.to/embed/%s/%s";
    public static final String POSTER_URL = "https://image.tmdb.org/t/p/w200/%s";
    public static final String REMOTE_VIEWING_IS_STILL_IN_BETA = "Remote Viewing is still in Beta";

    public static void playContent(VideoData videoData, Function<String, Boolean> function) {
        var dialog = new Dialog();
        dialog.setSizeFull();
        dialog.add(new Button(VaadinIcon.CLOSE.create(), e -> dialog.close()));
        var imgAndDesc = new VerticalLayout();
        var image = NetfliInterface.getImage(videoData);
        image.setWidth("200px");
        image.setHeight("200px");
        imgAndDesc.add(new HorizontalLayout(image, new VerticalLayout(new H4(videoData.title()), new NativeLabel(videoData.overview()))));
        var ui = UI.getCurrent();
        var url = PLAY_URL.formatted(videoData.type(), videoData.id());
        getCurrent().setAttribute("url", url);

        Button play;
        if (!function.apply(url)) {
            play = new Button("Sorry this title is not available to watch", VaadinIcon.FILE_REMOVE.create());
            play.addThemeVariants(ButtonVariant.LUMO_ERROR);
        } else {
            play = new Button("Play", VaadinIcon.PLAY.create());
            play.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            play.addClickListener(clickEvent -> {
                ui.navigate(PlayUI.class);
                dialog.close();

            });
        }
        imgAndDesc.add(play);
        dialog.add(imgAndDesc);
        dialog.open();
    }
}
