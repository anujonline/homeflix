package com.homeflix.app.views.netflix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.RemoteView;
import com.homeflix.app.views.common.MaintainHistory;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.homeflix.app.views.netflix.PlayConstants.POSTER_URL;
import static com.vaadin.flow.component.html.AnchorTarget.BLANK;

@Route(value = "", layout = TimerLayout.class)
@PageTitle("Homeflix")
@StyleSheet("./nm.css")
@Slf4j
public class NewHome extends Div {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TMDBService service;
    private final DataSaver dataSaver;
    private final MaintainHistory maintainHistory;
    private final UI ui;

    public NewHome(TMDBService service, DataSaver dataSaver, MaintainHistory maintainHistory) {
        this.ui = UI.getCurrent();
        this.service = service;
        this.dataSaver = dataSaver;
        this.maintainHistory = maintainHistory;

        add(getRemoteWatch());
        loginInfo();
        addHeader();
        addSearch();
        addClassName("home");
        addMovies();


    }

    private static Button getRemoteWatch() {
        var remoteWatch = createButton("Remote Watch?", e -> UI.getCurrent().navigate(RemoteView.class));
        remoteWatch.setSuffixComponent(VaadinIcon.COMPRESS_SQUARE.create());
        return remoteWatch;
    }

    private static Button createButton(String text, ComponentEventListener<ClickEvent<Button>> clickEventComponentEventListener) {
        return new Button(text, clickEventComponentEventListener);
    }

    public static Div getMovieDiv(String src, String movieTitle, String rating, ComponentEventListener<ClickEvent<Div>> clickAction) {
        var movie = new Div();
        movie.addClickListener(clickAction);
        movie.addClassName("movie");
        var image = new Image(POSTER_URL.formatted(src), movieTitle);
        image.addClassName("poster");
        var title = new Div();
        title.addClassName("title");
        title.add(movieTitle);
        var info = new Div();
        info.addClassName("info");
        Span length;
        try {
            length = new Span("Rating : " + "%.2f".formatted(Double.valueOf(rating)));
        } catch (Exception e) {
            length = new Span("Rating : --");
        }
        length.addClassName("length");
        info.add(length);
        movie.add(image, title, info);
        return movie;
    }

    private void loginInfo() {
        var loginButton = new Button("Login", e -> {
            log.info("login clicked");
            var notification = Notification.show("");
            UI.getCurrent().getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
                if (extendedClientDetails.isTouchDevice()) {
                    notification.setPosition(Notification.Position.BOTTOM_STRETCH);
                } else {
                    notification.setPosition(Notification.Position.TOP_CENTER);
                }
            });
            notification.add("""
                    Coming soon!!
                    
                    Enjoy unlimited streaming..
                    Save watch history..
                    Create playlists..
                    and many more ...
                    """);

            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        });
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(loginButton);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

    }

    private void addHeader() {
        var instagram = new Anchor("https://www.instagram.com/homeflixofficial");
        instagram.setClassName("button");
        instagram.setText("Let's connect over Instagram");
        instagram.setWidthFull();
        instagram.setTarget(BLANK);
        var verticalLayout = new VerticalLayout();
        verticalLayout.setClassName(LumoUtility.TextAlignment.CENTER);
        verticalLayout.setWidthFull();
        var homeflix = new H1("Homeflix");
        homeflix.setWidthFull();
        verticalLayout.add(homeflix, instagram);
        add(addSettings(), verticalLayout);

    }

    private Component addSettings() {
        var themeList = this.getElement().getThemeList();
        WebStorage.getItem("theme", value -> {
            if (value != null && value.equals("dark")) {
                themeList.add(Lumo.DARK);
            }
        });
        var svg = new Svg(PlayConstants.SETTINGS_SVG);
        var button = new Button("Settings", svg);
        button.addClickListener(buttonClickEvent -> {
            var dialog = new Dialog();
            dialog.add("Select Theme");
            var stringSelect = new Select<String>();
            stringSelect.setItems(List.of("Light", "Dark"));
            stringSelect.addValueChangeListener(s -> {
                var selectedTheme = s.getValue();
                log.info("changed theme to {}", selectedTheme);
                if (selectedTheme.equals("Light")) {
                    themeList.remove(Lumo.DARK);
                    WebStorage.removeItem("theme");
                } else {
                    themeList.add(Lumo.DARK);
                    WebStorage.setItem("theme", "dark");
                }
                dialog.close();
            });
            dialog.add(stringSelect);
            dialog.open();
        });
        return button;
    }

    private void addSearch() {
        var spotlightWrapper = new HorizontalLayout();
        spotlightWrapper.setWidthFull();
        spotlightWrapper.addClassNames("spotlight_wrapper");
        //<input type="text" id="spotlight" placeholder="Spotlight-Search" />
        var textField = new TextField();
        textField.setWidthFull();
        spotlightWrapper.setFlexGrow(1.0, textField);
        var component = new Button(LumoIcon.SEARCH.create());
        component.addClickListener(iconClickEvent -> UI.getCurrent().navigateToClient("search/%s".formatted(textField.getValue())));
        textField.setPlaceholder("Search ... ");
        spotlightWrapper.add(textField, component);
        add(spotlightWrapper);

//        textField.addKeyPressListener(Key.ENTER, keyPressEvent -> UI.getCurrent().navigateToClient("search/%s".formatted(textField.getValue())));
    }

    private void addMovies() {
        var recentlyWatched = new Section();
        WebStorage.getItem(WebStorage.Storage.LOCAL_STORAGE, "watched", s -> {
            ui.access(() -> {
                if (!StringUtils.isEmpty(s)) {
                    try {
                        var localStorage = OBJECT_MAPPER.readValue(s, new TypeReference<List<VideoData>>() {
                        });
                        recentlyWatched.add(getSection(new VideoDataWrapper("", localStorage)));

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        add(recentlyWatched);
        add(getSection(service.popularMovies()));
        add(getSection(service.trendingMovies()));
        add(getSection(service.homeflixFavMovies()));
        add(getSection(service.homeflixFavTv()));
    }

    public Component getSection(VideoDataWrapper videoDataWrapper) {
        var div = new Div();
        div.add(new H3(videoDataWrapper.label()));
        var list = new Section();
        list.addClassName("movies");
        videoDataWrapper.videoData().forEach(videoData -> list.add(getMovieDiv(videoData.poster(), videoData.title(), videoData.voteAverage(), divClickEvent -> {
            dataSaver.saveData(UI.getCurrent(), videoData);
            PlayConstants.playContent(videoData, maintainHistory.checkAvailability(), getSection(service.similarMovies(videoData.id(), videoData.type())));
        })));
        div.add(list);
        return div;
    }
}

