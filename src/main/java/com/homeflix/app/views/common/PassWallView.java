package com.homeflix.app.views.common;

import com.homeflix.app.views.netflix.AdminMessage;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

public abstract class PassWallView extends VerticalLayout {
    private final PasswordField passphrase = new PasswordField("Enter passphrase to get through");
    private boolean passed;

    public PassWallView() {
        if (!passed) {
            var dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            var check = new Button("Check", e -> {
                passphrase.getOptionalValue().ifPresent(s -> {
                    if (s.equals("adm")) {
                        passed = true;
                        dialog.close();
                        showContent();
                    } else {
                        AdminMessage.showNotification("Incorrect password", NotificationVariant.LUMO_ERROR);
                    }
                });
            });
            passphrase.addKeyUpListener(Key.ENTER, keyUpEvent -> check.click());
            dialog.add(passphrase, check);
            dialog.open();
        }
    }

    public abstract void showContent();

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        passphrase.focus();
    }
}
