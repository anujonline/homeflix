package com.homeflix.app.views.common;

import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTargetValue;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import static com.vaadin.flow.component.html.AnchorTarget.BLANK;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private static final String MESSAGE = "Chrome browser might show some ads. For ad free experience use any other browser";
    private final HorizontalLayout horizontalLayout = new HorizontalLayout();

    public MainLayout() {
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.add(new Svg("""
                <svg width="120" height="120" xmlns="http://www.w3.org/2000/svg">
                                           <g id="Homeflix">
                                            <title>Homeflix</title>
                                            <ellipse fill="#ffffff" cx="52.63636" cy="39.38636" id="svg_1" rx="28" ry="27.97727" stroke="#ffffff"/>
                                            <ellipse fill="none" cx="52.63636" cy="39.38636" id="svg_4" rx="22" ry="22" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="52.75131" cy="25.47832" id="svg_5" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="64.88506" cy="34" id="svg_6" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="65" cy="47" id="svg_7" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="52.75131" cy="54" id="svg_8" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="41.25705" cy="47" id="svg_9" rx="5" ry="5" stroke="#000000"/>
                                            <ellipse fill="#ffffff" cx="41.25705" cy="34" id="svg_10" rx="5" ry="5" stroke="#000000"/>
                                            <line fill="none" stroke="#ffffff" x1="52.29885" y1="67.47126" x2="22.18391" y2="67.35632" id="svg_11"/>
                                            <text fill="#ff0000" stroke="#000" x="7.30659" y="86.38968" id="svg_13" stroke-width="0" font-size="17" font-family="Noto Sans JP" text-anchor="start" xml:space="preserve">HOMEFLIX</text>
                                            <text fill="#ff7f00" stroke="#000" stroke-width="0" x="26.5043" y="92.12034" id="svg_14" font-size="4" font-family="Noto Sans JP" text-anchor="start" xml:space="preserve">ENTERTAINMENT AT HOME</text>
                                           </g>
                                          
                                          </svg>
                """));
        horizontalLayout.addClickListener(horizontalLayoutClickEvent -> UI.getCurrent().navigate(""));
        horizontalLayout.getStyle().set("cursor", "pointer");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle().set("border-radius", "25px");
        getStyle().set("overflow", "hidden");
        var ui = UI.getCurrent();
        var current = ui.getSession();
        ui.getPage().retrieveExtendedClientDetails(extendedClientDetails -> {
            if (current.getBrowser().isChrome() && !extendedClientDetails.isTouchDevice()) {
                horizontalLayout.add(new Marquee(MESSAGE));
                if (current.getAttribute("message-read") == null) {
                    var show = new Dialog();
                    show.add(new VerticalLayout(new Text(MESSAGE), new Button("Ok", event -> show.close())));
                    show.open();
                    current.setAttribute("message-read", true);
                }
            }
        });
        var instagram  = new Anchor("https://www.instagram.com/homeflixofficial");
        instagram.setClassName("button");
        instagram.setText("Let's connect over Instagram");
        instagram.setWidthFull();
        instagram.setTarget(BLANK);
        var verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        verticalLayout.add(instagram,horizontalLayout);
        addToNavbar(verticalLayout);
    }
}


