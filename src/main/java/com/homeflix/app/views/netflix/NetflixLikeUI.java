package com.homeflix.app.views.netflix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.RemoteView;
import com.homeflix.app.views.browse.BrowseView;
import com.homeflix.app.views.common.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;


@Route(value = "watch", layout = MainLayout.class)
@PageTitle("Homeflix")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js")
public class NetflixLikeUI extends VerticalLayout {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TMDBService service;
    private final DataSaver adminController;

    public NetflixLikeUI(TMDBService service, DataSaver adminController) {
        this.adminController = adminController;
        this.service = service;

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        var videoDataWrapper = new AtomicReference<VideoDataWrapper>();
        WebStorage.getItem(WebStorage.Storage.LOCAL_STORAGE, "watched", s -> {
            try {
                videoDataWrapper.set(new VideoDataWrapper("Recently watched", new ArrayList<>()));
                var localStorage = OBJECT_MAPPER.readValue(s, VideoDataWrapper.class);
                videoDataWrapper.get().videoData().addAll(localStorage.videoData());
            } catch (Exception e) {

            }
            addUI(videoDataWrapper);
        });
    }

    private void addUI(AtomicReference<VideoDataWrapper> videoDataWrapper) {
        add(new HorizontalLayout(new Button("Remote Watch?", VaadinIcon.ASTERISK.create(), e -> UI.getCurrent().navigate(RemoteView.class)), new Button("Bollywood", VaadinIcon.ASTERISK.create(), e -> UI.getCurrent().navigate(BrowseView.class))));
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

        //service response
        var videoDataWrappers = new ArrayList<VideoDataWrapper>();
        videoDataWrappers.add(videoDataWrapper.get());
        videoDataWrappers.add(service.trendingMovies());
        videoDataWrappers.add(service.popularMovies());
        videoDataWrappers.add(service.homeflixFavMovies());
        videoDataWrappers.add(service.homeflixFavTv());
        videoDataWrappers.add(service.topRatedTVSeries());

        // Header
        // Category content
        videoDataWrappers.stream().filter(Objects::nonNull).forEach(wrapper -> {
            var header = new HorizontalLayout();
            header.setWidthFull();
            header.setAlignItems(Alignment.CENTER);
            var headerLabel = new H4(wrapper.label());
            header.add(headerLabel);
            add(header);
            var categoryLayout = new HorizontalLayout();
            categoryLayout.setWidthFull();
            categoryLayout.setAlignItems(Alignment.CENTER);
            var videoDataList = wrapper.videoData();
            videoDataList.forEach(videoData -> {
                var videoLayout = new VerticalLayout();
                var movieImage = NetfliInterface.getImage(videoData);
                movieImage.addClickListener(event -> {
                    var vd = videoDataWrapper.get().videoData();
                    if (!vd.contains(videoData)) {
                        vd.add(videoData);
                    }
                    try {
                        WebStorage.setItem(WebStorage.Storage.LOCAL_STORAGE, "watched", OBJECT_MAPPER.writeValueAsString(videoDataWrapper));
                    } catch (JsonProcessingException e) {

                    }
                    adminController.saveData(UI.getCurrent(), videoData);
                    PlayConstants.playContent(videoData, function());
                });
                movieImage.setWidth("120px");
                var titleLabel = new NativeLabel(videoData.title());
                titleLabel.getStyle().set("font-size", "0.9em");
                titleLabel.setWidth("100px"); // Set the width as needed
                titleLabel.getStyle().setOverflow(Style.Overflow.HIDDEN);
                titleLabel.getElement().getStyle().set("text-overflow", "ellipsis");
                titleLabel.getElement().getStyle().set("white-space", "nowrap");

                videoLayout.add(movieImage, titleLabel);
                categoryLayout.add(videoLayout);
            });
            categoryLayout.getStyle().set("overflow-x", "auto");
            add(categoryLayout);
        });
    }

    Function<String, Boolean> function() {
        return service::checkAvailability;
    }
}
