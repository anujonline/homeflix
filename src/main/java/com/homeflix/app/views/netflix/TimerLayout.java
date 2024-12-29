package com.homeflix.app.views.netflix;

import com.homeflix.app.JC;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.homeflix.app.views.common.Broadcaster;

public class TimerLayout extends AppLayout {
    private final JC jc;
    private final Broadcaster broadcaster1;
    private Registration broadcaster;

    public TimerLayout(JC jc, Broadcaster broadcaster1) {
        this.broadcaster1 = broadcaster1;
        this.jc = jc;
        jc.startSession(VaadinSession.getCurrent().getSession().getId());
        UI.getCurrent().addDetachListener(detachEvent -> {
            jc.stopSession(VaadinSession.getCurrent().getSession().getId());
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        this.broadcaster = broadcaster1.register(message -> ui.access(() -> {
            showNotification(message);
        }));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.broadcaster.remove();
    }

    private void showNotification(String newOrder) {
        var show = Notification.show(newOrder);
        show.setPosition(Notification.Position.TOP_STRETCH);
        show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        show.open();
    }
}
