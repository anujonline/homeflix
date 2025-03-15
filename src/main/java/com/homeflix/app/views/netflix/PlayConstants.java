package com.homeflix.app.views.netflix;

import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.views.common.LoggedInUser;
import com.homeflix.app.views.common.LoginView;
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
import com.vaadin.flow.server.VaadinSession;

import java.util.Optional;
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
    public static final String SETTINGS_SVG = """
            <svg viewBox="0 0 24 24" width='24' fill="none" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <circle cx="12" cy="12" r="3" stroke="#1C274C" stroke-width="1.5"></circle> <path d="M13.7654 2.15224C13.3978 2 12.9319 2 12 2C11.0681 2 10.6022 2 10.2346 2.15224C9.74457 2.35523 9.35522 2.74458 9.15223 3.23463C9.05957 3.45834 9.0233 3.7185 9.00911 4.09799C8.98826 4.65568 8.70226 5.17189 8.21894 5.45093C7.73564 5.72996 7.14559 5.71954 6.65219 5.45876C6.31645 5.2813 6.07301 5.18262 5.83294 5.15102C5.30704 5.08178 4.77518 5.22429 4.35436 5.5472C4.03874 5.78938 3.80577 6.1929 3.33983 6.99993C2.87389 7.80697 2.64092 8.21048 2.58899 8.60491C2.51976 9.1308 2.66227 9.66266 2.98518 10.0835C3.13256 10.2756 3.3397 10.437 3.66119 10.639C4.1338 10.936 4.43789 11.4419 4.43786 12C4.43783 12.5581 4.13375 13.0639 3.66118 13.3608C3.33965 13.5629 3.13248 13.7244 2.98508 13.9165C2.66217 14.3373 2.51966 14.8691 2.5889 15.395C2.64082 15.7894 2.87379 16.193 3.33973 17C3.80568 17.807 4.03865 18.2106 4.35426 18.4527C4.77508 18.7756 5.30694 18.9181 5.83284 18.8489C6.07289 18.8173 6.31632 18.7186 6.65204 18.5412C7.14547 18.2804 7.73556 18.27 8.2189 18.549C8.70224 18.8281 8.98826 19.3443 9.00911 19.9021C9.02331 20.2815 9.05957 20.5417 9.15223 20.7654C9.35522 21.2554 9.74457 21.6448 10.2346 21.8478C10.6022 22 11.0681 22 12 22C12.9319 22 13.3978 22 13.7654 21.8478C14.2554 21.6448 14.6448 21.2554 14.8477 20.7654C14.9404 20.5417 14.9767 20.2815 14.9909 19.902C15.0117 19.3443 15.2977 18.8281 15.781 18.549C16.2643 18.2699 16.8544 18.2804 17.3479 18.5412C17.6836 18.7186 17.927 18.8172 18.167 18.8488C18.6929 18.9181 19.2248 18.7756 19.6456 18.4527C19.9612 18.2105 20.1942 17.807 20.6601 16.9999C21.1261 16.1929 21.3591 15.7894 21.411 15.395C21.4802 14.8691 21.3377 14.3372 21.0148 13.9164C20.8674 13.7243 20.6602 13.5628 20.3387 13.3608C19.8662 13.0639 19.5621 12.558 19.5621 11.9999C19.5621 11.4418 19.8662 10.9361 20.3387 10.6392C20.6603 10.4371 20.8675 10.2757 21.0149 10.0835C21.3378 9.66273 21.4803 9.13087 21.4111 8.60497C21.3592 8.21055 21.1262 7.80703 20.6602 7C20.1943 6.19297 19.9613 5.78945 19.6457 5.54727C19.2249 5.22436 18.693 5.08185 18.1671 5.15109C17.9271 5.18269 17.6837 5.28136 17.3479 5.4588C16.8545 5.71959 16.2644 5.73002 15.7811 5.45096C15.2977 5.17191 15.0117 4.65566 14.9909 4.09794C14.9767 3.71848 14.9404 3.45833 14.8477 3.23463C14.6448 2.74458 14.2554 2.35523 13.7654 2.15224Z" stroke="#1C274C" stroke-width="1.5"></path> </g></svg>
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
                Optional.ofNullable(VaadinSession.getCurrent().getAttribute(LoggedInUser.class)).ifPresentOrElse(loggedInUserClass -> {
                    closeAllDialogs();
                    UI.getCurrent().navigateToClient("now-playing/%s".formatted(videoData.id()));
                }, () -> {
                    Dialog noti = new Dialog();
                    noti.add(new NativeLabel("You will need to login first"));
                    noti.add(new HorizontalLayout(new Button("Ok", e -> {
                        closeAllDialogs();
                        UI.getCurrent().navigate(LoginView.class);
                        noti.close();
                    }), new Button("Cancel", e -> {
                        noti.close();
                    })));
                    noti.setCloseOnOutsideClick(false);
                    noti.setCloseOnEsc(false);
                    noti.open();
                });
            });
        }
        imgAndDesc.add(play);
        dialog.add(imgAndDesc);
        dialog.add(section);
        dialog.open();
    }

    // Method to close all dialogs in the UI
    private static void closeAllDialogs() {
        UI.getCurrent().getChildren().forEach(component -> {
            if (component instanceof Dialog) {
                ((Dialog) component).close();
            }
        });
    }
}
