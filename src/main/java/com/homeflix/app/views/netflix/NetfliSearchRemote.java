package com.homeflix.app.views.netflix;

import com.homeflix.app.data.Broadcaster;
import com.homeflix.app.data.RemoteAccessDTO;
import com.homeflix.app.data.controllers.AdminController;
import com.homeflix.app.data.models.VideoDataWrapper;
import com.homeflix.app.data.service.FeedbackData;
import com.homeflix.app.data.service.tmdb.TMDBService;
import com.homeflix.app.views.common.MainLayout;
import com.homeflix.app.views.common.Marquee;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;

import static com.homeflix.app.views.netflix.PlayConstants.PLAY_URL;
import static com.homeflix.app.views.netflix.PlayConstants.REMOTE_VIEWING_IS_STILL_IN_BETA;


@Route(value = "search-remote", layout = MainLayout.class)
@PageTitle("Homeflix")
@PermitAll
class NetfliSearchRemote extends VerticalLayout implements HasUrlParameter<String> {
    private final TMDBService service;
    private final AdminController adminController;
    private final Notification notification = new Notification("Movie will now be playing on your remote device.", 3000, Notification.Position.TOP_STRETCH);
    private final Notification errorNotification = new Notification("Your remote session is ended. Please scan the QR again.", 3000, Notification.Position.TOP_STRETCH);

    public NetfliSearchRemote(TMDBService service, AdminController adminController) {
        add(new Marquee(REMOTE_VIEWING_IS_STILL_IN_BETA));
        this.service = service;
        this.adminController = adminController;
        setSizeFull();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        addAttachListener(e -> UI.getCurrent().access(() -> {
            setSizeFull();
            getStyle().set("overflow-x", "scroll");
            getStyle().set("display", "block");
            scrollIntoView();
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
        back.addClickListener(event -> UI.getCurrent().navigate("/watch-remote/%s".formatted(UI.getCurrent().getSession().getAttribute("id"))));
        var component = new HorizontalLayout(back);
        verticalLayout.add(component);
    }


    private void addContent(VideoDataWrapper videoDataWrapper, Div div) {
        if (videoDataWrapper.videoData().isEmpty()) {
            div.add(new NativeLabel("Nope, nothing available with this title."));
        } else {
            div.add(new H2(videoDataWrapper.label()));
            VaadinSession.getCurrent().access(() -> videoDataWrapper.videoData().forEach(videoFile -> {
                var image = NetfliInterface.getImage(videoFile);
                div.add(image);
                image.addClickListener(event -> {
                    var ui = UI.getCurrent();
                    ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
                        var data = new FeedbackData(videoFile.id(), extendedClientDetails.getCurrentDate(), extendedClientDetails.getTimeZoneId(), extendedClientDetails.isTouchDevice(), ui.getSession().getBrowser().getBrowserApplication());
                        adminController.addHistory(ui.getSession().getBrowser().getAddress(), data.toString());
                    });
                    var formatted = PLAY_URL.formatted(videoFile.type(), videoFile.id());
                    var message = new RemoteAccessDTO();
                    var id = UI.getCurrent().getSession().getAttribute("id").toString();
                    message.setId(id);
                    message.setUrl(formatted);
                    if (Broadcaster.broadcast(id, message)) {
                        notification.open();
                    } else {
                        errorNotification.open();
                    }
                });
            }));
        }
    }
}
