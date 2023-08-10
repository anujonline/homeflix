package com.homeflix.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("/playing")
@AnonymousAllowed
public class Embedder extends Div {
    TextField textField = new TextField("Enter IMDB ID");
    Button showMovieButton = new Button("Watch Movie");

    public Embedder() {
        try {

//            setAlignItems(Alignment.CENTER);

            add(textField);
            add(showMovieButton);
            var embed = new Embed("https://vidsrc.to/embed/movie/");
            embed.setHeight("100%");
            embed.setWidthFull();
            add(embed);
            var url = "https://vidsrc.to/embed/movie/%s";
            showMovieButton.addClickListener(event -> {
                var show = Notification.show("Now playing your movie with ID %s".formatted(textField.getValue().toLowerCase()));
                show.setPosition(Notification.Position.TOP_STRETCH);
                show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().access(() -> {

                    remove(embed);
                    var value = textField.getValue().toLowerCase();
                    embed.setSrc(url.formatted(value));
                    add(embed);

                });
            });
        } catch (Exception e) {
            var notification = Notification.show("Something went wrong");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_STRETCH);
        }
    }
}
