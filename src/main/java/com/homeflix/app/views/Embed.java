package com.homeflix.app.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.spring.annotation.UIScope;

@Tag("iframe")
@UIScope
public class Embed extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault("src", "");

    public Embed() {
        setWidthFull();
        setHeight("70%");
        if (!UI.getCurrent().getSession().getBrowser().isChrome()) {
            getElement().setProperty("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts");
        }
        getElement().setProperty("allow", "autoplay");
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
