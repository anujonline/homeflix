package com.homeflix.app.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        addHeaderContent();
    }

    private void addHeaderContent() {
        var appIcon = new Image("icons/icon.png", "");
        appIcon.setHeight(30, Unit.PIXELS);
        appIcon.setWidth(30, Unit.PIXELS);
        viewTitle = new H2(appIcon, new NativeLabel("HomeFlix"));
        viewTitle.getStyle().set("align-items", "center");
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        viewTitle.setWidthFull();
        viewTitle.setHeight("10%");
        addToNavbar(true, viewTitle);
        getStyle().set("align-items", "center");
    }
}
