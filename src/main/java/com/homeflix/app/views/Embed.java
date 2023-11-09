package com.homeflix.app.views;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.spring.annotation.UIScope;

@Tag("iframe")
@UIScope
public class Embed extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault("src", "");

    public Embed() {
        setId("if");
        setWidthFull();
        setHeight("70%");
        getElement().setProperty("sandbox", "");
        getElement().setProperty("allow", "autoplay");
        getElement().setAttribute("allow", "autoplay");
        getElement().setProperty("frameborder", "0");
        getElement().setProperty("allowfullscreen", "true");
        getElement().setAttribute("allowfullscreen", "true");
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}
