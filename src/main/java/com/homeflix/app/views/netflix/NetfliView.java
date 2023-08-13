package com.homeflix.app.views.netflix;

import com.homeflix.app.views.Embed;
import com.homeflix.app.views.MainLayout;
import com.homeflix.app.views.TMDBService;
import com.homeflix.app.views.viewers.home.VideoDataWrapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;


import static com.homeflix.app.views.Content.PLAY_URL;
import static com.homeflix.app.views.Content.POSTER_URL;

@Route(value = "v2", layout = MainLayout.class)
@PageTitle("Homeflix")
class NewBg extends Section {

    public NewBg(TMDBService service) {
        setSizeFull();
        addAttachListener(event -> {
            UI.getCurrent().access(() -> {
                add(new NetfliView(service));
            });
        });
    }
}

public class NetfliView extends VerticalLayout {
    private final Dialog dialog = new Dialog();
    private final Button searchButton = new Button("Search", VaadinIcon.SEARCH.create());
    private final TextField searchBar = new TextField();
    private final Embed embed;

    public NetfliView(TMDBService service) {
        this.embed = new Embed("https://vidsrc.to/embed/movie/tt17048514");
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        getStyle().set("overflow", "scroll");
        getStyle().set("display", "block");
        scrollIntoView();
        addDialog();
        var verticalLayoutHeader = new Div();
        setPadding(false);
        setSpacing(false);
        verticalLayoutHeader.getStyle().set("position", "fixed");
        verticalLayoutHeader.setWidthFull();
        verticalLayoutHeader.setHeight("10%");
        var verticalLayout = new Div();
        addHeader(verticalLayoutHeader);
        addAttachListener(event -> {
            try {
                addContent(service.popularMovies(), verticalLayout);
                addContent(service.trendingMovies(), verticalLayout);
                addContent(service.topRatedTVSeries(), verticalLayout);
                addContent(service.onTheAir(), verticalLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        verticalLayout.getStyle().set("padding-top", "102px");
        add(verticalLayoutHeader, verticalLayout);

        searchButton.addClickListener(event -> {
            UI.getCurrent().navigateToClient("v2/search/%s".formatted(searchBar.getValue()));
        });
    }

    private void addInstallButton() {
        var installButton = new Button("Want to load faster? Install the app", VaadinIcon.QUESTION.create(), event -> UI.getCurrent().access(() -> {
            var installDialog = new Dialog();
            var installInstructions = new VerticalLayout();
            installInstructions.add(new H3("Following instructions are application for ios/macos/chrome browser/android and WIndows Phone"));
            installInstructions.add(new HorizontalLayout(new NativeLabel("1. Click on Share button in navigator,"), VaadinIcon.SHARE_SQUARE.create()));
            installInstructions.add(new NativeLabel("2. Press Add to Home Screen"));
            installInstructions.add(new H4("Installation will give you faster load times"));

            add(installDialog);
            installDialog.add(installInstructions);
            installDialog.open();
        }));
        installButton.setWidthFull();
        add(installButton);
    }

    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        var accordionTV = new VerticalLayout();
        var newTv = new HorizontalLayout();
        newTv.getStyle().set("overflow", "scroll");
        VaadinSession.getCurrent().access(() -> {
            videoDataWrapper.videoData().forEach(videoFile -> {
                var image = new Image(videoFile.poster() == null ? "icons/icon.png" : POSTER_URL.formatted(videoFile.poster()), "");
                image.setMaxHeight("10%");
                image.setWidth("10%");
                image.getStyle().set("display", "inline-flex");
                newTv.add(image);
                image.addClickListener(event -> {
                    UI.getCurrent().access(() -> {
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
        var image1 = new Image("icons/icon.png", "");
        image1.setHeight(30, Unit.PIXELS);
        image1.setWidth(30, Unit.PIXELS);
        var header = new H1(image1, new NativeLabel("HomeFlix"));

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
        horizontalLayout.getStyle().set("position", "fixed");
    }
}
