package com.homeflix.app.views.netflix;

import com.homeflix.app.views.Embed;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("now-playing")
@PageTitle("Homeflix")
public class PlayUI extends VerticalLayout {
    private final Embed embed = new Embed();
    private final Div div = new Div();

    public PlayUI() {
        setSizeFull();
        embed.setSrc((String) VaadinSession.getCurrent().getAttribute("url"));
        setAlignItems(FlexComponent.Alignment.CENTER);
        var closeDialog = new Button("Back", VaadinIcon.ARROW_LEFT.create(), e -> {
            UI.getCurrent().getPage().getHistory().back();
        });
        closeDialog.setWidthFull();
        embed.setSizeFull();
        div.add(closeDialog, embed);
        div.setWidthFull();
        div.setHeight("400px");
        add(div);
    }
}
