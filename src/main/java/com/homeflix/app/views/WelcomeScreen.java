package com.homeflix.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@Route(value = "")

public class WelcomeScreen extends VerticalLayout {

    public WelcomeScreen() {
        UI.getCurrent().getPage().setLocation("homeflix");
    }
}