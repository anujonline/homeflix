package com.homeflix.app.views;

import com.homeflix.app.views.netflix.NetfliView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@Route(value = "")
@StyleSheet(value = "./splash.css",loadMode = LoadMode.EAGER)
public class WelcomeScreen extends VerticalLayout {

    public WelcomeScreen() {
        getElement().setProperty("innerHTML", "\n" +
                "<input class=\"retrigger\" type=\"radio\" name=\"rerun\" id=\"retrigger--2\" checked=\"checked\"/>\n" +
                "<div class=\"bg\"></div>\n" +
                "<div id=\"bb\" class=\"buttons\" onclick=\"window.location.href='/watch'\">\n" +
                "    <label class=\"button button--1\">ENTER</label>\n" +
                "</div>\n" +
                "<div class=\"pane\">\n" +
                "    <div class=\"rotate\">\n" +
                "        <div class=\"logo\">HOMEFLIX</div>\n" +
                "    </div>\n" +
                "</div>");
    }
}