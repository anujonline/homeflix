package com.homeflix.app.views.common;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;

@Tag("marquee")
public class Marquee extends HtmlContainer {
    public Marquee(String message) {
        this.getElement().setText(message);
    }
}
