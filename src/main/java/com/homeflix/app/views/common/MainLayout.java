package com.homeflix.app.views.common;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinService;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    public MainLayout() {
        getStyle().set("border-radius", "25px");
        getStyle().set("overflow", "hidden");
        var current = UI.getCurrent().getSession();
        if(current.getBrowser().isChrome()){
            addToNavbar(new Marquee());
            if(current.getAttribute("message-read")== null){
                var show = new Dialog();
                show.add(new VerticalLayout(new Text(Marquee.MESSAGE), new Button("Ok", event -> show.close())));
                show.open();
                current.setAttribute("message-read", true);
            }
        }
    }
}

