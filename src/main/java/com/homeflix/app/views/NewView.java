package com.homeflix.app.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.homeflix.app.data.controllers.AdminController;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

import java.util.List;

@Route("")
@PageTitle("Homeflix")
class ViewBG extends VerticalLayout {

    public ViewBG(AdminController adminController, TMDBService service) {
        getStyle().set("background-image", "url('icons/bg.jpg')");
        VaadinService.getCurrentRequest().setAttribute("type", "movie");
        setClassName("animate-area");
        setId("animate-area");
        getStyle().set("border-radius", "25px");
        getStyle().set("overflow", "hidden");
        add(new NewView(adminController, true, DataProvider.ofCollection(service.getMovies())));
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }
}

public class NewView extends VerticalLayout {


    private final Dialog dialog = new Dialog();
    private final Button searchButton = new Button(VaadinIcon.SEARCH.create());
    private final RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>("", "movie", "tv");
    TextField searchBar = new TextField();
    private Embed embed;

    public NewView(AdminController adminController, boolean addHeader, ListDataProvider<Result1> dataProvider) {
        add(new Button("Try new UI", VaadinIcon.ASTERISK.create(), event -> UI.getCurrent().navigateToClient("v2")));
        if (addHeader) {
            addHeader();
        }

        var scroller = new Scroller();
        setHeightFull();
        setMaxWidth("800px");
        setMaxHeight("800px");

        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        var parentDiv = new Div();
        parentDiv.getStyle().set("margin", "10");
        parentDiv.getStyle().set("padding", "10");
        parentDiv.getStyle().set("display", "inline-block");
        scrollIntoView();
        var scrollOptions = new ScrollOptions(ScrollOptions.Behavior.SMOOTH);
        addDialog();
        dataProvider.getItems().forEach(videoFile -> {
            parentDiv.add(new Content().showContent(adminController, (String) VaadinService.getCurrentRequest().getAttribute("type"), dialog, embed, videoFile));
        });
        scrollIntoView(scrollOptions);
        scroller.setContent(parentDiv);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        add(scroller);
        searchButton.addClickListener(event -> {
            var searchTitle = searchBar.getValue();
            var selectedType = radioGroup.getValue();
            if (!(searchTitle.isEmpty() || selectedType.isEmpty())) {
                VaadinService.getCurrentRequest().setAttribute("title", searchTitle);
                VaadinService.getCurrentRequest().setAttribute("type", selectedType);
                UI.getCurrent().navigate(SearchView.class);
            } else {
                var show = Notification.show("You need select the title and and type.");
                show.setPosition(Notification.Position.MIDDLE);
                show.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        //
    }

    private void addHeader() {
        var image1 = new Image("icons/icon.png", "");
        image1.setHeight(30, Unit.PIXELS);
        image1.setWidth(30, Unit.PIXELS);
        var header = new H1(image1, new NativeLabel("HomeFlix"));

        var component = new HorizontalLayout(searchBar, radioGroup, searchButton);
        component.setWidthFull();
        component.setFlexGrow(1.0, searchBar);
        component.setAlignItems(Alignment.CENTER);
        searchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
        header.add(component);

        header.setWidthFull();
        header.setHeight("10%");
        add(header);
    }

    private void addDialog() {
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        var parameter = "https://vidsrc.to/embed/movie/tt17048514";
        embed = new Embed(parameter);
        var closeDialog = new Button("Close Player", VaadinIcon.ARROW_LEFT.create());
        dialog.setSizeFull();
        closeDialog.addClickListener(event -> dialog.close());
        add(dialog);
        dialog.add(horizontalLayout, embed);
        setSizeFull();
        horizontalLayout.add(closeDialog);
    }
}


record Response1(@JsonProperty("results") List<Result1> results) {
}

