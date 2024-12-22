package com.homeflix.app.views.netflix;

import com.homeflix.app.data.models.VideoData;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.function.Function;

import static com.vaadin.flow.server.VaadinSession.getCurrent;

public final class PlayConstants {
    public static final String PLAY_URL = "https://vidsrc.in/embed/%s/%s";
    public static final String POSTER_URL = "https://image.tmdb.org/t/p/w200/%s";
    public static final String REMOTE_VIEWING_IS_STILL_IN_BETA = "Remote Viewing is still in Beta";
    public static final String HOMEFLIX_SVG = """
                <svg width="120" height="120" xmlns="http://www.w3.org/2000/svg">
                                           <g id="Homeflix">
                                            <title>Homeflix</title>
                                            <ellipse fill="#ffffff" cx="52.63636" cy="39.38636" id="svg_1" rx="28" ry="27.97727" stroke="#ffffff"/>
                                            <ellipse fill="none" cx="52.63636" cy="39.38636" id="svg_4" rx="22" ry="22" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="52.75131" cy="25.47832" id="svg_5" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="64.88506" cy="34" id="svg_6" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="65" cy="47" id="svg_7" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="52.75131" cy="54" id="svg_8" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="41.25705" cy="47" id="svg_9" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="41.25705" cy="34" id="svg_10" rx="5" ry="5" stroke="#000000"/>
                                            <line fill="none" stroke="#ffffff" x1="52.29885" y1="67.47126" x2="22.18391" y2="67.35632" id="svg_11"/>
                                            <text fill="#ff0000" stroke="#000" x="7.30659" y="86.38968" id="svg_13" stroke-width="0" font-size="17" font-family="Noto Sans JP" text-anchor="start" xml:space="preserve">HOMEFLIX</text>
                                            <text fill="#ff7f00" stroke="#000" stroke-width="0" x="26.5043" y="92.12034" id="svg_14" font-size="4" font-family="Noto Sans JP" text-anchor="start" xml:space="preserve">ENTERTAINMENT AT HOME</text>
                                           </g>
                                          
                                          </svg>
                """;

    public static void playContent(VideoData videoData, Function<String, Boolean> function, Component section) {
        var dialog = new Dialog();
        dialog.setSizeFull();
        dialog.add(new Button(VaadinIcon.CLOSE.create(), e -> dialog.close()));
        var imgAndDesc = new VerticalLayout();
        var image = NetfliInterface.getImage(videoData);
        image.setWidth("200px");
        image.setHeight("200px");
        imgAndDesc.add(new HorizontalLayout(image, new VerticalLayout(new H4(videoData.title()), new NativeLabel(videoData.overview()))));
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
                UI.getCurrent().removeAll();
                UI.getCurrent().navigateToClient(PlayUI.class.getAnnotation(Route.class).value());
            });
        }
        imgAndDesc.add(play);
        dialog.add(imgAndDesc);
        dialog.add(section);
        dialog.open();
    }
}
