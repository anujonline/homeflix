package com.homeflix.app.views.netflix;

import com.homeflix.app.views.Embed;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import static com.vaadin.flow.component.html.AnchorTarget.BLANK;

@Route(value = "now-playing", layout = TimerLayout.class)
@PageTitle("Homeflix")
public class PlayUI extends VerticalLayout {
    private final Embed embed = new Embed();
    private final Div div = new Div();

    public PlayUI() {
        try {
            setSizeFull();
            var anchor = new Anchor("https://brave.com/download/");
            anchor.setText("For ad free experience we recommend using Brave Browser");
            anchor.setClassName("Button");
            anchor.setTarget(BLANK);
            add(anchor);
            embed.setSrc((String) VaadinSession.getCurrent().getAttribute("url"));
            setAlignItems(FlexComponent.Alignment.CENTER);
            var closeDialog = new Button("Back", VaadinIcon.ARROW_LEFT.create(), e -> {
                UI.getCurrent().getPage().getHistory().back();
            });
            closeDialog.setWidthFull();
            embed.setSizeFull();
            div.add(closeDialog, embed);
            div.setWidthFull();
            add(div);
        } catch (Exception e) {
            UI.getCurrent().navigate("");
            Notification.show("There seems to be an error in the app, please report this via instagram on homepage");
        }
    }
}
