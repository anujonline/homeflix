package com.homeflix.app.views.netflix;

import com.homeflix.app.views.common.Broadcaster;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

@Route("admin")
@PageTitle("Homeflix")
@Slf4j
public class AdminMessage extends VerticalLayout {

    public AdminMessage(Broadcaster broadcaster) {
        var passphrase = new PasswordField("Enter passphrase to get through");
        var textField = new TextField("message");
        var broadcast = new Button("Broadcast", e -> {
            passphrase.getOptionalValue()

                    .ifPresentOrElse(s -> {
                        if (!s.equals("adm")) {
                            showError();
                        }
                        broadcaster.broadcast(textField.getValue());
                        showNotification("Broadcast successful", NotificationVariant.LUMO_SUCCESS);
                    }, AdminMessage::showError);

        });
        add(passphrase, textField, broadcast);
    }

    private static void showError() {
        showNotification("What are you doing??", NotificationVariant.LUMO_ERROR);
        log.error("ALERT SOMEONE TRIED IT");
    }

    private static void showNotification(String message, NotificationVariant notificationVariant) {

        var notification = Notification.show(message);
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(Notification.Position.TOP_STRETCH);
        notification.open();
    }
}
