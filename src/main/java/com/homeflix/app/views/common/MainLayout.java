package com.homeflix.app.views.common;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private static final String MESSAGE = "Chrome browser is not supported at this moment.";
    public MainLayout() {
        getStyle().set("border-radius", "25px");
        getStyle().set("overflow", "hidden");
        var ui = UI.getCurrent();
        var current = ui.getSession();
        ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
            if (current.getBrowser().isChrome() && !extendedClientDetails.isTouchDevice()) {
                addToNavbar(new Marquee(MESSAGE));
                if (current.getAttribute("message-read") == null) {
                    var show = new Dialog();
                    show.add(new VerticalLayout(new Text(MESSAGE), new Button("Ok", event -> show.close())));
                    show.open();
                    current.setAttribute("message-read", true);
                }
            }
        });

    }
}


