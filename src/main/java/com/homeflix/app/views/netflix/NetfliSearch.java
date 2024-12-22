package com.homeflix.app.views.netflix;

import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.Embed;
import com.homeflix.app.views.common.CustomComponent;
import com.homeflix.app.views.common.MaintainHistory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;

import static com.homeflix.app.views.netflix.NewHome.getMovieDiv;


@Route(value = "search", layout = TimerLayout.class)
@PageTitle("Homeflix")
@PermitAll
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js")
class NetfliSearch extends CustomComponent implements HasUrlParameter<String> {
    private final TMDBService service;
    private final DataSaver adminController;
    private final MaintainHistory maintainHistory;
    private final Dialog dialog = new Dialog();
    private final Embed embed;

    public NetfliSearch(TMDBService service, DataSaver adminController, MaintainHistory maintainHistory) {
        this.service = service;
        this.adminController = adminController;
        this.embed = new Embed();
        this.maintainHistory = maintainHistory;
        setSizeFull();

        this.addClassName("main-background");
    }
    public Component getSection(VideoDataWrapper videoDataWrapper) {
        add(new H3(videoDataWrapper.label()));
        var list = new Section();
        list.addClassName("movies");
        videoDataWrapper.videoData().forEach(videoData ->
                list.add(getMovieDiv(videoData.poster(), videoData.title(), videoData.voteAverage(),
                        divClickEvent -> {
                            adminController.saveData(UI.getCurrent(), videoData);
                            PlayConstants.playContent(videoData, maintainHistory.checkAvailability(),
                                    getSection(service.similarMovies(videoData.id(), videoData.type())));
                        })));
        return list;
    }
    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        addAttachListener(e -> UI.getCurrent().access(() -> {
            setSizeFull();
            getStyle().set("overflow-x", "scroll");
            getStyle().set("display", "block");
            scrollIntoView();
            addDialog();
            var verticalLayoutHeader = new Div();
            verticalLayoutHeader.setWidthFull();
            verticalLayoutHeader.setHeight("10%");
            var verticalLayout = new Div();
            addHeader(verticalLayoutHeader);
            addContent(new VideoDataWrapper("Search Results", service.search(parameter)), verticalLayout);

            add(verticalLayoutHeader, verticalLayout);
        }));
    }

    private void addHeader(Div verticalLayout) {
        var back = new Button("Back", VaadinIcon.ARROW_LEFT.create());
        back.setWidthFull();
        back.addClickListener(event -> UI.getCurrent().navigateToClient(NewHome.class.getAnnotation(Route.class).value()));
        var component = new HorizontalLayout(back);
        verticalLayout.add(component);
    }


    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        if (videoDataWrapper.videoData().isEmpty()) {
            div.add(new NativeLabel("Nope, nothing available with this title."));
        } else {
            div.add(new H2(videoDataWrapper.label()));
            VaadinSession.getCurrent().access(() -> videoDataWrapper.videoData().forEach(videoData -> {
                var image = NetfliInterface.getImage(videoData);
                image.setWidth("80px");
                image.getStyle().set("margin", "20px");
                image.addClickListener(event -> {
                    adminController.saveData(UI.getCurrent(), videoData);
                    PlayConstants.playContent(videoData, maintainHistory.checkAvailability(), getSection(service.similarMovies(videoData.id(), videoData.type())));
                });
                div.add(image);
            }));
        }

    }

    private void addDialog() {
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        var closeDialog = new Button("Close Player", VaadinIcon.ARROW_LEFT.create());
        horizontalLayout.add(closeDialog);
        dialog.setSizeFull();
        closeDialog.addClickListener(event -> dialog.close());
        add(dialog);
        dialog.add(horizontalLayout, embed);
        setSizeFull();
    }
}
