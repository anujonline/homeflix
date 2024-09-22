package com.homeflix.app.views.common;

import com.homeflix.app.views.netflix.NewHome;
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
            var notification = Notification.show("Oops, something went wrong, let's start again 😊");
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.setPosition(Notification.Position.TOP_STRETCH);
        });
        event.forwardTo(NewHome.class);
        return HttpServletResponse.SC_NOT_FOUND;
    }
}