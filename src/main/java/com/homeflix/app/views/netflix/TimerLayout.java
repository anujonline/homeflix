package com.homeflix.app.views.netflix;

import com.homeflix.app.JC;
import com.homeflix.app.views.common.Broadcaster;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        WebStorage.getItem("setting-update", s -> {
            if(Optional.ofNullable(s).isEmpty()){
                var show = new Notification();
                show.addThemeVariants(NotificationVariant.LUMO_WARNING);
                show.add("Introducing Settings. Explore from the top of the page.");
                show.setPosition(Notification.Position.MIDDLE);
                show.setDuration(0);
                show.add(new Button("", VaadinIcon.CLOSE.create(), e->{
                    show.close();
                    WebStorage.setItem("setting-update","read");
                }));
                show.open();
            }
        });
        WebStorage.getItem("client-id", s -> {
            Optional.ofNullable(s).ifPresentOrElse(s1 -> {
                log.info("existing user {}", s1);
            }, () -> {
                String clientId = UUID.randomUUID().toString();
                log.info("New user, generating client-id {}", clientId);
                WebStorage.setItem("client-id", clientId);
            });
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
