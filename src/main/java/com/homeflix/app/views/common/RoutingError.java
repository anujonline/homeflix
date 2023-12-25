package com.homeflix.app.views.common;

import com.homeflix.app.views.netflix.NetflixLikeUI;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.*;
import jakarta.servlet.http.HttpServletResponse;

@ParentLayout(MainLayout.class)
public class RoutingError
        extends RouteNotFoundError {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {
        UI.getCurrent().access(() -> {
            var notification = Notification.show("Page does not exist, forwarding you to home page 😊");
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setPosition(Notification.Position.TOP_STRETCH);
        });
        event.forwardTo(NetflixLikeUI.class);
        return HttpServletResponse.SC_NOT_FOUND;
    }
}