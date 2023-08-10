package com.homeflix.app.views;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@Tag("embed")
public class Embed extends HtmlContainer {
    private final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault(
            "src",
            ""
    );


    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
    public Embed(String parameter) {
        setSrc(parameter);
        setSizeFull();
        getElement().setAttribute("type", "text/html");
    }
}
