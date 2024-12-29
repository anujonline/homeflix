package com.homeflix.app.views.netflix;

import com.homeflix.app.views.common.Broadcaster;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Route("admin")
public class AdminMessage extends VerticalLayout {

    public AdminMessage(Broadcaster broadcaster) {
        var textField = new TextField("message");
        var broadcase = new Button("Broadcast", e -> {
            broadcaster.broadcast(textField.getValue());
        });
        add(textField, broadcase);
    }
}
