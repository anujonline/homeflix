package com.homeflix.app.views.netflix;

import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.views.RemoteView;
import com.homeflix.app.views.browse.BrowseView;
import com.homeflix.app.views.common.MainLayout;
import com.homeflix.app.views.common.MaintainHistory;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


@Route(value = "watch", layout = MainLayout.class)
@PageTitle("Homeflix")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js")
public class NetflixLikeUI extends VerticalLayout {
    private final DataSaver adminController;
    private final MaintainHistory maintainHistory;

    public NetflixLikeUI(DataSaver adminController, MaintainHistory maintainHistory) {
        this.adminController = adminController;
        this.maintainHistory = maintainHistory;
        addHeader();

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
            layout.add(new Svg("""
                    <svg width="100" height="100" xmlns="http://www.w3.org/2000/svg">
                                               <g id="Layer_1">
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
                    """));
            layout.add(new H1("Homeflix"));
            layout.add("Welcome to HOMEFLIX, a personal journey through the world of cinema");
            layout.add(new H1("About Me"));
            layout.add("Hi there, I am the cinephile behind HOMEFLIX. As a movie enthusiast, I embarked on this solo adventure to create a space where fellow film lovers can explore and enjoy the magic of cinema");
            layout.add(new H1("The Project"));
            layout.add("HOMEFLIX is a passion project built on open-source movie databases and viewing APIs. It's my way of celebrating the art of storytelling through film. From classic masterpieces to hidden gems, this platform is a curated collection of movies that have left a lasting impression on me.");
            layout.add(new H1("Why Movies?"));
            layout.add("Movies have the power to transport us to different worlds, evoke emotions, and spark conversations. With [Your Project Name], I aim to share my love for cinema and provide a space for others to discover and rediscover remarkable films.");
            layout.add(new H1("Explore, Discover, Enjoy"));
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
                var videoLayout = new VerticalLayout();
                videoLayout.setWidth("120px");
                videoLayout.getStyle().set("cursor", "pointer");
                var movieImage = NetfliInterface.getImage(videoData);
                videoLayout.addClickListener(event -> {
                    adminController.saveData(UI.getCurrent(), videoData);
                    PlayConstants.playContent(videoData, maintainHistory.checkAvailability());
                });
                movieImage.setWidth("120px");
                var titleLabel = new NativeLabel(videoData.title());
                titleLabel.getStyle().set("font-size", "0.9em");
                titleLabel.getStyle().set("cursor", "pointer");
                titleLabel.setWidth("100px"); // Set the width as needed
                titleLabel.getStyle().setOverflow(Style.Overflow.HIDDEN);
                titleLabel.getElement().getStyle().set("text-overflow", "ellipsis");
                titleLabel.getElement().getStyle().set("white-space", "nowrap");

                videoLayout.add(movieImage, titleLabel);
                Tooltip.forComponent(videoLayout).withText(videoData.title()).withHoverDelay(1000).withPosition(Tooltip.TooltipPosition.TOP_START);
                categoryLayout.add(videoLayout);
            });
            categoryLayout.getStyle().set("overflow-x", "auto");
            add(categoryLayout);
        });
    }

    private void addHeader() {
        var accordionPanel = new AccordionPanel(new HorizontalLayout(new NativeLabel("More")));
        accordionPanel.addThemeVariants(DetailsVariant.SMALL);
        accordionPanel.setWidthFull();
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
        });
        clearBrowsingHistory.setSuffixComponent(VaadinIcon.FILE_REMOVE.create());
        clearBrowsingHistory.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return clearBrowsingHistory;
    }
}
