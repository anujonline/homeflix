package com.homeflix.app.views.netflix;

import com.homeflix.app.views.Embed;
import com.homeflix.app.views.MainLayout;
import com.homeflix.app.views.TMDBService;
import com.homeflix.app.views.viewers.home.VideoDataWrapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import static com.homeflix.app.views.Content.PLAY_URL;
import static com.homeflix.app.views.Content.POSTER_URL;

@Route(value = "v2/search", layout = MainLayout.class)
@PageTitle("Homeflix")
class SearchBg extends Section implements HasUrlParameter<String> {
    private final TMDBService service;

    public SearchBg(TMDBService service) {
        this.service = service;

        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        addAttachListener(e -> UI.getCurrent().access(() -> {
            add(new NetfliSearch(service, parameter));
        }));
    }
}

public class NetfliSearch extends Section {
    private final Dialog dialog = new Dialog();
    private final Embed embed;
    public NetfliSearch(TMDBService service, String query) {
        this.embed = new Embed("https://vidsrc.to/embed/movie/tt17048514");

        setSizeFull();
        getStyle().set("overflow", "scroll");
        getStyle().set("display", "block");
        scrollIntoView();
        addDialog();
        var verticalLayoutHeader = new Div();
        verticalLayoutHeader.getStyle().set("position", "fixed");
        verticalLayoutHeader.setWidthFull();
        verticalLayoutHeader.setHeight("10%");
        var verticalLayout = new Div();
        addHeader(verticalLayoutHeader);
        addAttachListener(event -> addContent(new VideoDataWrapper("Search Results", service.search(query)), verticalLayout));
        verticalLayout.getStyle().set("padding-top", "102px");
        add(verticalLayoutHeader, verticalLayout);
    }

    private void addHeader(Div verticalLayout) {
        var back = new Button("Back", VaadinIcon.ARROW_LEFT.create());
        back.setWidthFull();
        back.addClickListener(event -> UI.getCurrent().navigateToClient("v2"));
        var component = new HorizontalLayout(back);
        verticalLayout.add(component);
    }


    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        if(videoDataWrapper.videoData().isEmpty()){
            div.add(new NativeLabel("Nope, nothing available with this title."));
        }
        else {
            div.add(new H2(videoDataWrapper.label()));
            var newTv = new Div();
            newTv.getStyle().set("margin", "10");
            newTv.getStyle().set("padding", "10");
            newTv.getStyle().set("display", "inline-block");
            newTv.getStyle().set("position", "absolute");
            VaadinSession.getCurrent().access(() -> {
                videoDataWrapper.videoData().forEach(videoFile -> {
                    var movieDiv = new Div();
                    movieDiv.getStyle().set("display", "inline-block");
                    movieDiv.getStyle().set("color", "white");
                    movieDiv.getStyle().set("text-overflow", "ellipsis");
                    movieDiv.getStyle().set("overflow", "hidden");
                    movieDiv.getStyle().set("white-space", "nowrap");
                    movieDiv.setHeight("20%");
                    movieDiv.setWidth("20%");
                    var image = new Image(videoFile.poster() == null ? "icons/icon.png" : POSTER_URL.formatted(videoFile.poster()), "");
                    image.setHeight("100%");
                    image.setWidth("90%");
                    image.getStyle().set("object-fit", "cover");
                    image.getStyle().set("margin", "10px");
                    image.getStyle().set("cursor", "pointer");
                    image.getStyle().set("box-shadow", "10px 10px 10px black");
                    image.getStyle().set("transition", "all 0.3s ease 0s");

                    var text = new NativeLabel(videoFile.title());

                    text.getStyle().set("color", "white");
                    text.getStyle().set("display", "inline-block");
                    text.getStyle().set("text-overflow", "ellipsis");
                    text.getStyle().set("overflow", "hidden");
                    text.getStyle().set("white-space", "nowrap");

                    movieDiv.add(image);
                    movieDiv.add(text);
                    newTv.add(movieDiv);
                    image.addClickListener(event -> {
                        UI.getCurrent().access(() -> {
                            var formatted = PLAY_URL.formatted(videoFile.type(), videoFile.id());
                            embed.setSrc(formatted);
                        });
                        dialog.open();
                    });
                });
                div.add(newTv);
            });
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
