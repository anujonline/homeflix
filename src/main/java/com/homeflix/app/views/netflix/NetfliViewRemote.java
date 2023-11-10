package com.homeflix.app.views.netflix;

import com.homeflix.app.data.Broadcaster;
import com.homeflix.app.data.RemoteAccessDTO;
import com.homeflix.app.data.controllers.AdminController;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.FeedbackData;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.common.MainLayout;
import com.homeflix.app.views.common.Marquee;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.web.context.annotation.SessionScope;

import static com.homeflix.app.views.netflix.PlayConstants.*;

@Route(value = "watch-remote", layout = MainLayout.class)
@PageTitle("Homeflix")
@SessionScope
public class NetfliViewRemote extends VerticalLayout implements HasUrlParameter<String> {
    private final Button searchButton = new Button("Search", VaadinIcon.SEARCH.create());
    private final TextField searchBar = new TextField();
    private final AdminController adminController;
    private final Notification notification = new Notification("Movie will now be playing on your remote device.", 3000, Notification.Position.TOP_STRETCH);
    private final Notification errorNotification = new Notification("Your remote session is ended. Please scan the QR again.", 3000, Notification.Position.TOP_STRETCH);

    public NetfliViewRemote(TMDBService service, AdminController adminController) {
        this.adminController = adminController;
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        add(new Marquee(REMOTE_VIEWING_IS_STILL_IN_BETA));
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("overflow-x", "scroll");
        getStyle().set("display", "block");
        scrollIntoView();
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
                addContent(service.onTheAir(), verticalLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        add(verticalLayoutHeader, verticalLayout);

        searchButton.addClickListener(event -> UI.getCurrent().navigateToClient("search-remote/%s".formatted(searchBar.getValue())));
    }

    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        var accordionTV = new VerticalLayout();
        var newTv = new HorizontalLayout();
        newTv.getStyle().set("overflow", "scroll");
        VaadinSession.getCurrent().access(() -> {
            videoDataWrapper.videoData().forEach(videoFile -> {
                var image = new Image(videoFile.poster() == null ? "icons/icon.png" : POSTER_URL.formatted(videoFile.poster()), "");
                image.setMaxHeight("20%");
                image.setWidth("20%");
                image.getStyle().set("display", "inline-flex");
                newTv.add(image);
                image.addClickListener(event -> {
                    var ui = UI.getCurrent();
                    ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
                        var data = new FeedbackData(videoFile.id(), extendedClientDetails.getCurrentDate(), extendedClientDetails.getTimeZoneId(), extendedClientDetails.isTouchDevice(), "REMOTE " + ui.getSession().getBrowser().getBrowserApplication());
                        adminController.addHistory(ui.getSession().getBrowser().getAddress(), data.toString());
                    });
                    var formatted = PLAY_URL.formatted(videoFile.type(), videoFile.id());
                    var id = VaadinSession.getCurrent().getAttribute("id").toString();
                    var message = new RemoteAccessDTO(id, formatted);

                    if (Broadcaster.broadcast(id, message)) {
                        notification.open();
                    } else {
                        errorNotification.open();
                    }
                });
            });
            accordionTV.add(new H3(videoDataWrapper.label()));
            accordionTV.add(newTv);
        });
        div.add(accordionTV);
    }

    private void addHeader(Div verticalLayout) {
        var header = new H1(new NativeLabel("HomeFlix"));
        searchBar.addKeyPressListener(Key.ENTER, event -> searchButton.click());
        var component = new HorizontalLayout(searchBar, searchButton);
        component.setFlexGrow(1.0, searchBar);
        searchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
        header.add(component);
        verticalLayout.add(header);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        VaadinSession.getCurrent().setAttribute("id", s);
    }
}
