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
        setWidthFull();
        setHeight("70%");
//        if(!UI.getCurrent().getSession().getBrowser().isChrome()){
        getElement().setProperty("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-top-navigation");
//        }
        getElement().setProperty("allow", "autoplay");
        getElement().setProperty("frameborder", "0");
        getElement().setProperty("allowfullscreen", "true");
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}
