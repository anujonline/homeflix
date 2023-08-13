package com.homeflix.app.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        getStyle().set("background-image", "url('icons/bg.jpg')");

        VaadinService.getCurrentRequest().setAttribute("type", "movie");
        setClassName("animate-area");
        setId("animate-area");
        getStyle().set("border-radius", "25px");
        getStyle().set("overflow", "hidden");
    }

}
