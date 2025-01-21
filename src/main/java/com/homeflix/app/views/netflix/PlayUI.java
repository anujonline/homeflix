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
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;

import static com.vaadin.flow.component.html.AnchorTarget.BLANK;

@Route(value = "now-playing/:" + PlayUI.PARAMETER_MOVIE_ID, layout = TimerLayout.class)
@PageTitle("Homeflix")
@Slf4j
public class PlayUI extends VerticalLayout implements HasUrlParameter<String> {
    protected static final String PARAMETER_MOVIE_ID = "movieId";

    private final Embed embed = new Embed();
    private final Div div = new Div();

    public PlayUI() {
        try {
            setHeightFull();
            setSizeFull();
            div.setHeight("500px");
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
            UI.getCurrent().navigateToClient("");
            Notification.show("There seems to be an error in the app, please report this via instagram on homepage");
        }
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String movieName) {
        var routeParameters = beforeEvent.getRouteParameters();
        log.info("playing {}", routeParameters.get(PARAMETER_MOVIE_ID));
    }

}
