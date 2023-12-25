package com.homeflix.app.views.netflix;

import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.views.RemoteView;
import com.homeflix.app.views.browse.BrowseView;
import com.homeflix.app.views.common.MainLayout;
import com.homeflix.app.views.common.MaintainHistory;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collection;
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
            header.setWidthFull();
            header.setAlignItems(Alignment.CENTER);
            var headerLabel = new H4(wrapper.label());
            header.add(headerLabel);
            add(header);
            var categoryLayout = new HorizontalLayout();
            categoryLayout.setWidthFull();
            categoryLayout.setAlignItems(Alignment.CENTER);
            var videoDataList = wrapper.videoData();
            videoDataList.stream().limit(20).forEach(videoData -> {
                var videoLayout = new VerticalLayout();
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
                categoryLayout.add(videoLayout);
            });
            categoryLayout.getStyle().set("overflow-x", "auto");
            add(categoryLayout);
        });
    }

    private void addHeader() {
        add(new HorizontalLayout(new Button("Remote Watch?", VaadinIcon.ASTERISK.create(), e -> UI.getCurrent().navigate(RemoteView.class)), new Button("Bollywood", VaadinIcon.ASTERISK.create(), e -> UI.getCurrent().navigate(BrowseView.class))), new Button("Clear browsing history", e -> {
            maintainHistory.clearHistory();
            Notification.show("You browsing history is cleared, refresh the page 😊");
        }));
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
}
