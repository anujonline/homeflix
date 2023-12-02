package com.homeflix.app.views.browse;

import com.homeflix.app.data.DataSaver;
import com.homeflix.app.data.service.VAccess;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.Cookie;

@Route("/browse")
@PermitAll
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/2.2.4/jquery.min.js")
public class BrowseView extends Scroller {

    public BrowseView(VAccess videoService, DataSaver adminController) {
        super();
        var image1 = new Image("icons/icon.png", "");
        image1.setHeight(30, Unit.PIXELS);
        image1.setWidth(30, Unit.PIXELS);
        var homeFlix = new H1(image1, new NativeLabel("HomeFlix"));

        homeFlix.setWidthFull();
        homeFlix.setHeight("10%");
        setContent(homeFlix);

        var scroller = new Scroller();
        setHeightFull();
        setWidthFull();
        getStyle().set("background", """
                linear-gradient(black,#501414)
                """);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        var parentDiv = new Div();
        parentDiv.getStyle().set("margin", "10");
        parentDiv.getStyle().set("padding", "10");
        parentDiv.getStyle().set("display", "inline-block");
        scrollIntoView();
        var scrollOptions = new ScrollOptions(ScrollOptions.Behavior.SMOOTH);
        var dataProvider = new ListDataProvider<>(videoService.videoList());
        dataProvider.getItems().forEach(videoFile -> {
            var movieDiv = new Div();
            movieDiv.getStyle().set("display", "inline-block");
            movieDiv.setHeight("30%");
            movieDiv.setWidth("30%");

            Image image = new Image((videoFile.getPosterLink() == null || videoFile.getPosterLink().isBlank()) ? "icons/icon.png" : videoFile.getPosterLink(), "");
            parentDiv.scrollIntoView();

            image.setText(videoFile.getName());
            image.setAlt(videoFile.getName());

            image.setHeight("100%");
            image.setWidth("90%");
            image.getStyle().set("object-fit", "cover");
            image.getStyle().set("margin", "10px");
            image.getStyle().set("cursor", "pointer");
            image.getStyle().set("box-shadow", "10px 10px 10px black");
            image.getStyle().set("transition", "all 0.3s ease 0s");

            image.addClickListener(clickEvent -> {
                var ui = UI.getCurrent();
                adminController
                        .saveData(ui, videoFile);
                VaadinService.getCurrentResponse().addCookie(new Cookie("mainURL", videoFile.getFullPath()));
                ui.getPage().setLocation("play/" + videoFile.getIdentifier());
            });
            var text = new NativeLabel(videoFile.getName());

            text.getStyle().set("color", "white");
            text.getStyle().set("display", "inline-block");
            text.getStyle().set("overflow", "hidden");
            text.getStyle().set("text-overflow", "ellipsis");
            text.getStyle().set("white-space", "nowrap");

            text.setSizeFull();
            text.setWidthFull();

            movieDiv.add(image);
            movieDiv.add(text);
            parentDiv.add(movieDiv);
        });
        scrollIntoView(scrollOptions);
        scroller.setContent(parentDiv);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        setContent(scroller);
    }
}
