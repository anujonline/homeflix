package com.homeflix.app.views;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.spring.annotation.UIScope;

@Tag("iframe")
@UIScope
public class Embed extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault(
            "src",
            ""
    );


    public Embed(String parameter) {
        setSrc(parameter);
        setWidthFull();
        setHeight("70%");
        getElement().setProperty("allowfullscreen", "true");
        getElement().setProperty("sandbox", "allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-top-navigation");
        getElement().setProperty("frameborder", "0");
        getElement().setProperty("gesture", "media");
        getElement().setProperty("allow", "encrypted-media");
        getElement().setAttribute("allowfullscreen", "true");
        getElement().setAttribute("type", "text/html");
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}
