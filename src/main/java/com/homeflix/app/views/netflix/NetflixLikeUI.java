package com.homeflix.app.views.netflix;

import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoData;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.views.Animated;
import com.homeflix.app.views.RemoteView;
import com.homeflix.app.views.browse.BrowseView;
import com.homeflix.app.views.common.CustomComponent;
import com.homeflix.app.views.common.MaintainHistory;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;


@Route(value = "watch-v4")
@PageTitle("Homeflix")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js")
public class NetflixLikeUI extends CustomComponent {
    protected static final Random RANDOM = new Random();
    private final DataSaver adminController;
    private final MaintainHistory maintainHistory;
    List<Animated.Animation> animations = List.of(Animated.Animation.HEADSHAKE, Animated.Animation.BOUNCE, Animated.Animation.WOBBLE, Animated.Animation.JELLO, Animated.Animation.TADA);

    public NetflixLikeUI(DataSaver adminController, MaintainHistory maintainHistory) {
        this.adminController = adminController;
        this.maintainHistory = maintainHistory;
        addHeader();
        this.addClassName("main-background");

    }

    private static ComponentEventListener<KeyPressEvent> submit(Button searchButton) {
        return keyPressEvent -> searchButton.clickInClient();
    }

    private static Button getBollywood() {
        var bollywood = createButton("Bollywood", e -> UI.getCurrent().navigate(BrowseView.class));
        bollywood.setSuffixComponent(VaadinIcon.MOVIE.create());
        return bollywood;
    }

    private static Button aboutUs() {
        var bollywood = createButton("About Us", e -> {
            var show = new Dialog();
            var layout = new VerticalLayout();
            layout.add(new Svg(PlayConstants.HOMEFLIX_SVG));
            layout.add(new H2("Homeflix"));
            layout.add("Welcome to HOMEFLIX, a personal journey through the world of cinema");
            layout.add(new H2("About Me"));
            layout.add("Hi there, I am the cinephile behind HOMEFLIX. As a movie enthusiast, I embarked on this solo adventure to create a space where fellow film lovers can explore and enjoy the magic of cinema");
            layout.add(new H2("The Project"));
            layout.add("HOMEFLIX is a passion project built on open-source movie databases and viewing APIs. It's my way of celebrating the art of storytelling through film. From classic masterpieces to hidden gems, this platform is a curated collection of movies that have left a lasting impression on me.");
            layout.add(new H2("Why Movies?"));
            layout.add("Movies have the power to transport us to different worlds, evoke emotions, and spark conversations. With [Your Project Name], I aim to share my love for cinema and provide a space for others to discover and rediscover remarkable films.");
            layout.add(new H2("Explore, Discover, Enjoy"));
            layout.add("Whether you're a seasoned cinephile or just starting your movie journey, HOMEFLIX is here for you. Explore the database, discover new favorites, and enjoy the cinematic experience.");
            layout.add(new H3("Disclaimer: We don't own the movies, all images, description and movie content is via third party APIs"));
            var close = new Button("Close", clickEvent -> show.close());
            close.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            layout.add(close);
            show.add(layout);
            show.open();
        });
        bollywood.setSuffixComponent(VaadinIcon.SPECIALIST.create());
        return bollywood;
    }

    private static Button getRemoteWatch() {
        var remoteWatch = createButton("Remote Watch?", e -> UI.getCurrent().navigate(RemoteView.class));
        remoteWatch.setSuffixComponent(VaadinIcon.COMPRESS_SQUARE.create());
        return remoteWatch;
    }

    private static Button createButton(String text, ComponentEventListener<ClickEvent<Button>> clickEventComponentEventListener) {
        return new Button(text, clickEventComponentEventListener);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        maintainHistory.reference(addUIInCallback());

    }

    private Consumer<Collection<VideoDataWrapper>> addUIInCallback() {
        return videoDataWrapper -> UI.getCurrent().access(() -> addUI(videoDataWrapper));
    }

    private void addUI(Collection<VideoDataWrapper> videoDataWrappers) {
        // Header
        // Category content
        videoDataWrappers.stream().filter(obj -> Objects.nonNull(obj) && !obj.videoData().isEmpty()).forEach(wrapper -> {
            var header = new HorizontalLayout();
            header.setAlignItems(Alignment.START);
            var headerLabel = new H4(wrapper.label());
            header.add(headerLabel);
            add(header);
            var categoryLayout = new HorizontalLayout();
            categoryLayout.setWidthFull();
            categoryLayout.setAlignItems(Alignment.START);
            var videoDataList = wrapper.videoData();
            videoDataList.forEach(videoData -> {
                VerticalLayout videoLayout = createVideoElement(videoData);
                categoryLayout.add(videoLayout);
            });
            categoryLayout.getStyle().set("overflow-x", "auto");
            add(categoryLayout);
        });
    }

    private VerticalLayout createVideoElement(VideoData videoData) {
        var videoLayout = new VerticalLayout();
        Animated.animate(videoLayout, animations.get(RANDOM.nextInt(animations.size())));
        videoLayout.addClassName("movie-container");
        videoLayout.setWidth("120px");
        videoLayout.getStyle().set("cursor", "pointer");
        var movieImage = NetfliInterface.getImage(videoData);
        videoLayout.addClickListener(event -> {
            adminController.saveData(UI.getCurrent(), videoData);
            PlayConstants.playContent(videoData, maintainHistory.checkAvailability(), new Div());
        });
        movieImage.setWidth("120px");
        var titleLabel = new NativeLabel(videoData.title());
        titleLabel.setClassName("movie-title");

        videoLayout.add(movieImage, titleLabel);
        Tooltip.forComponent(videoLayout).withText(videoData.title()).withHoverDelay(1000).withPosition(Tooltip.TooltipPosition.TOP_START);
        return videoLayout;
    }

    private void addHeader() {
        var accordionPanel = new HorizontalLayout();
        accordionPanel.add(List.of(getRemoteWatch(), getBollywood(), getClearBrowsingHistory(), aboutUs()));
        add(accordionPanel);
        setAlignItems(Alignment.CENTER);
        var search = new HorizontalLayout();
        search.setWidthFull();
        search.setAlignItems(Alignment.CENTER);

        var searchField = new TextField();
        searchField.setPlaceholder("Search...");
        searchField.setWidth("300px");

        var searchButton = new Button("Search", event -> {
            UI.getCurrent().navigateToClient("search/%s".formatted(searchField.getValue()));
        });
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        search.setFlexGrow(1.0, searchField);
        search.add(searchField, searchButton);
        add(search);
        searchField.addKeyPressListener(Key.ENTER, submit(searchButton));
    }

    private Button getClearBrowsingHistory() {
        var clearBrowsingHistory = createButton("Clear browsing history", e -> {
            maintainHistory.clearHistory();
            var show = Notification.show("You browsing history is cleared, refresh the page 😊");
            show.setPosition(Notification.Position.TOP_STRETCH);
            show.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            UI.getCurrent().getPage().reload();
        });
        clearBrowsingHistory.setSuffixComponent(VaadinIcon.FILE_REMOVE.create());
        clearBrowsingHistory.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return clearBrowsingHistory;
    }
}
