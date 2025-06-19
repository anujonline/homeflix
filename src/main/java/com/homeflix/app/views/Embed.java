package com.homeflix.app.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.spring.annotation.UIScope;

@Tag("iframe")
@UIScope
public class Embed extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault("src", "");

    public Embed() {
        setId("hframe");
        setSizeFull();
        setHeight("100%");

        getElement().setProperty("allow", "autoplay");

        getElement().setAttribute("allowfullscreen", "true");
        getElement().setAttribute("frameBorder", "0");
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}
