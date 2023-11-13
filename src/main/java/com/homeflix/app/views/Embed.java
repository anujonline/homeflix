package com.homeflix.app.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.spring.annotation.UIScope;

@Tag("iframe")
@UIScope
public class Embed extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault("src", "");

    public Embed() {
        setId("hframe");
        setWidthFull();
        setHeight("70%");
        if (!UI.getCurrent().getSession().getBrowser().isChrome()) {
            getElement().setProperty("sandbox", "");
        }
        getElement().setProperty("referrerpolicy","same-origin");
        getElement().setAttribute("referrerpolicy","same-origin");
        getElement().setProperty("allow", "autoplay");
        getElement().setProperty("frameborder", "0");
        getElement().setProperty("allowfullscreen", "true");
        getElement().setAttribute("allowfullscreen", "true");
        getElement().addEventListener("click", domEvent -> {
            domEvent.getEventTarget().ifPresent(System.out::println);
        });
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}
