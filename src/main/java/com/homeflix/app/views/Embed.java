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
        setHeight("70%");
        if (!UI.getCurrent().getSession().getBrowser().isChrome()) {
            getElement().setProperty("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-top-navigation");
            getElement().setAttribute("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-top-navigation");
        }
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
