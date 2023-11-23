package com.homeflix.app.views.netflix;

import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.Embed;
import com.homeflix.app.views.RemoteView;
import com.homeflix.app.views.common.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;

import static com.homeflix.app.views.netflix.PlayConstants.PLAY_URL;

@Route(value = "watch", layout = MainLayout.class)
@PageTitle("Homeflix")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js")
@Slf4j
public class NetfliView extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final Button searchButton = new Button("Search", VaadinIcon.SEARCH.create());
    private final TextField searchBar = new TextField();
    private final Embed embed;
    private final DataSaver adminController;
    private final Button qr = new Button("Remote Watch?", VaadinIcon.ASTERISK.create(), e -> UI.getCurrent().navigate(RemoteView.class));

    public NetfliView(TMDBService service, DataSaver adminController) {
        this.adminController = adminController;
        this.embed = new Embed();
        setSizeFull();
        add(qr);
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-x", "scroll");
        getStyle().set("display", "block");
        scrollIntoView();
        addDialog();
        var verticalLayoutHeader = new Div();
        verticalLayoutHeader.setWidthFull();
        verticalLayoutHeader.setHeight("10%");
        var verticalLayout = new Div();
        addHeader(verticalLayoutHeader);
        addAttachListener(event -> {
            try {
                addContent(service.popularMovies(), verticalLayout);
                addContent(service.trendingMovies(), verticalLayout);
                addContent(service.topRatedTVSeries(), verticalLayout);
                addContent(service.homeflixFavMovies(), verticalLayout);
                addContent(service.homeflixFavTv(), verticalLayout);
                addContent(service.onTheAir(), verticalLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        add(verticalLayoutHeader, verticalLayout);

        searchButton.addClickListener(event -> UI.getCurrent().navigateToClient("search/%s".formatted(searchBar.getValue())));
    }

    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        var accordionTV = new VerticalLayout();
        var newTv = new HorizontalLayout();
        newTv.getStyle().set("overflow", "scroll");
        VaadinSession.getCurrent().access(() -> {
            videoDataWrapper.videoData().forEach(videoFile -> {
                var image = NetfliInterface.getImage(videoFile);
                newTv.add(image);
                image.addClickListener(event -> {
                    var ui = UI.getCurrent();
                    adminController
                            .saveData(ui, videoFile);
                    ui.access(() -> {
                        var formatted = PLAY_URL.formatted(videoFile.type(), videoFile.id());
                        embed.setSrc(formatted);
                    });
                    dialog.open();
                });
            });
            accordionTV.add(new H3(videoDataWrapper.label()));
            accordionTV.add(newTv);
        });
        div.add(accordionTV);
    }

    private void addHeader(Div verticalLayout) {
        var header = new H1(new NativeLabel("HomeFlix"));
        var component = new HorizontalLayout(searchBar, searchButton);
        component.setFlexGrow(1.0, searchBar);
        searchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
        header.add(component);
        verticalLayout.add(header);
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
