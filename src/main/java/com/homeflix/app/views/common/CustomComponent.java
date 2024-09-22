package com.homeflix.app.views.common;

import com.homeflix.app.views.netflix.PlayConstants;
import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import org.apache.commons.lang3.StringUtils;

import static com.vaadin.flow.component.html.AnchorTarget.BLANK;

public class CustomComponent extends VerticalLayout {
    private static final String MESSAGE = "Chrome browser might show some ads. For ad free experience use any other browser";
    public CustomComponent() {
        addHeader();

    }
    private void addHeader() {
        var ui = UI.getCurrent();
        var current = ui.getSession();
        WebStorage.getItem(WebStorage.Storage.LOCAL_STORAGE, "message-read", s -> {
            ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
                if (current.getBrowser().isChrome() && !extendedClientDetails.isTouchDevice()) {
                    UI.getCurrent().access(() -> {
                        if(StringUtils.isEmpty(s)){
                            var show = new Dialog();
                            var anchor = new Anchor("https://brave.com/download/");
                            anchor.setText("We recommend using Brave Browser");
                            anchor.setClassName("Button");
                            anchor.setTarget(BLANK);
                            show.add(new VerticalLayout(new Text(MESSAGE), anchor, new Button("Ok", event -> show.close())));
                            show.open();
                            WebStorage.setItem(WebStorage.Storage.LOCAL_STORAGE, "message-read", "OK");
                        }
                    });
                }
            });

        });
        var instagram  = new Anchor("https://www.instagram.com/homeflixofficial");
        instagram.setClassName("button");
        instagram.setText("Let's connect over Instagram");
        instagram.setWidthFull();
        instagram.setTarget(BLANK);
        var verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        var homeflix = new H1("Homeflix");
        homeflix.setWidthFull();
        verticalLayout.add(homeflix, instagram);
        getStyle().set("background","f2f5f7");
        add(verticalLayout);

    }
}
