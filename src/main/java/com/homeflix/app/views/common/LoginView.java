package com.homeflix.app.views.common;

import com.homeflix.app.views.netflix.NewHome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

import static com.vaadin.flow.component.html.AnchorTarget.BLANK;

@Route("login")
public class LoginView extends VerticalLayout {
    public LoginView() {
        setSizeFull();
        getStyle().set("background-image", "url('icons/bg.jpg')");
        setClassName("animate-area");
        setId("animate-area");
        Div div = new Div(loginForm());
        div.setSizeFull();
        div.addClassName("login-div");
        add(div);
    }

    private Component loginForm() {
        var formLayout = new VerticalLayout();
        formLayout.setSizeFull();

        formLayout.add(addHeader());
        formLayout.setAlignItems(Alignment.CENTER);
        TextField username = new TextField("Username");
        formLayout.add(username);
        PasswordField password = new PasswordField("Password");
        formLayout.add(password);
        var button = new Button("Login");
        button.addClickListener(e -> {
            var loggedInUser = new LoggedInUser(username.getValue(), password.getValue());
            VaadinSession.getCurrent().setAttribute(LoggedInUser.class, loggedInUser);
            UI.getCurrent().navigate(NewHome.class);
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        formLayout.add(button);
        return formLayout;
    }

    private VerticalLayout addHeader() {
        var instagram = new Anchor("https://www.instagram.com/homeflixofficial");
        instagram.setClassName("button");
        instagram.setText("Let's connect over Instagram");
        instagram.setWidthFull();
        instagram.setTarget(BLANK);
        var verticalLayout = new VerticalLayout();
        verticalLayout.setClassName(LumoUtility.TextAlignment.CENTER);
        verticalLayout.setWidthFull();
        var homeflix = new H1("Homeflix");
        homeflix.setWidthFull();
        verticalLayout.add(homeflix, instagram);
        return verticalLayout;
    }
}
