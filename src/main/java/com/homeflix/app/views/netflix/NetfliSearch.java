package com.homeflix.app.views.netflix;

import com.homeflix.app.data.controllers.AdminController;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.FeedbackData;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.Embed;
import com.homeflix.app.views.common.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;

import static com.homeflix.app.views.netflix.PlayConstants.PLAY_URL;
import static com.homeflix.app.views.netflix.PlayConstants.POSTER_URL;


@Route(value = "search", layout = MainLayout.class)
@PageTitle("Homeflix")
@PermitAll
class NetfliSearch extends VerticalLayout implements HasUrlParameter<String> {
    private final TMDBService service;
    private final AdminController adminController;
    private final Dialog dialog = new Dialog();
    private final Embed embed;


    public NetfliSearch(TMDBService service, AdminController adminController) {
        this.service = service;
        this.adminController = adminController;
        this.embed = new Embed("https://vidsrc.to/embed/movie/tt17048514");
        setSizeFull();
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
        back.addClickListener(event -> UI.getCurrent().navigate(NetfliView.class));
        var component = new HorizontalLayout(back);
        verticalLayout.add(component);
    }


    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        if (videoDataWrapper.videoData().isEmpty()) {
            div.add(new NativeLabel("Nope, nothing available with this title."));
        } else {
            div.add(new H2(videoDataWrapper.label()));
            var newTv = new Div();
            newTv.getStyle().set("margin", "10");
            newTv.getStyle().set("padding", "10");
            newTv.getStyle().set("display", "inline-block");
            newTv.getStyle().set("position", "relative");
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
                        var ui = UI.getCurrent();
                        ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
                            var data = new FeedbackData(videoFile.id(), extendedClientDetails.getCurrentDate(), extendedClientDetails.getTimeZoneId(), extendedClientDetails.isTouchDevice(), ui.getSession().getBrowser().getBrowserApplication());
                            adminController.addHistory(ui.getSession().getBrowser().getAddress(), data.toString());
                        });

                        ui.access(() -> {
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
