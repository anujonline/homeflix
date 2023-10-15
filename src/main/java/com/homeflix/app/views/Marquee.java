package com.homeflix.app.views;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;

@Tag("marquee")
public class Marquee extends HtmlContainer {
    public static final String MESSAGE = "We are currently experiencing problems playing movies on only on Google Chrome. Please use Edge, Firefox, Opera, Safari, or any other browser";

    public Marquee() {
        this.getElement().setText(MESSAGE);
    }
}
