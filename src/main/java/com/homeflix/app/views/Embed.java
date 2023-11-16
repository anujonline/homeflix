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
            getElement().setProperty("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-top-navigation");
            getElement().setAttribute("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-top-navigation");
        }
        getElement().setProperty("referrerpolicy", "same-origin");
        getElement().setAttribute("referrerpolicy", "same-origin");
        getElement().setProperty("allow", "autoplay");
        getElement().setProperty("frameborder", "0");
        getElement().setProperty("allowfullscreen", "true");
        getElement().setAttribute("allowfullscreen", "true");
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        UI.getCurrent().getPage().executeJs("""
                window.alias_open = window.open;
                window.open = function(url, name, specs, replace) { 
                // Do nothing, or do something smart... 
                } ;
                ""","");
        set(srcDescriptor, src);
    }
}
