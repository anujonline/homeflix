package com.homeflix.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;

import java.util.Arrays;

@Route("")
public class EmbedOld extends VerticalLayout {

    public EmbedOld() {
        add(new H3("FullScreen support only on Apple devices"));
        var nativeLabel = new NativeLabel("""
                To get a IMDB ID, go to IMDB and select any movie.
                                    
                On the Browser address bar copy the ID.
                Example:
                                    
                url: https://www.imdb.com/title/tt0420223
                                    
                ID to go in the text box should be tt0420223.
                                    
                No slashes '/' nothing else.
                If the ID is incorrect or movie is not available video won't play 
                """);
        add(nativeLabel);

        TextField textField = new TextField("Enter IMDB ID");
        textField.setWidthFull();
        Button showMovieButton = new Button("Watch Movie");
        showMovieButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        showMovieButton.setWidthFull();
        add(textField);
        add(showMovieButton);
        var dialog = new Dialog();
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.START);
        dialog.setSizeFull();
        var mainURL = Arrays.stream(VaadinService.getCurrentRequest().getCookies()).filter(cookie -> cookie.getName().contentEquals("mainURL")).map(Cookie::getValue).findAny();
        var parameter = mainURL.orElse("https://vidsrc.to/embed/movie/tt17048514");
        var embed = new Embed(parameter);
        var closeDialog = new Button("Close Player", VaadinIcon.ARROW_LEFT.create());
        closeDialog.addClickListener(event -> dialog.close());
        add(dialog);
        dialog.add(horizontalLayout, embed);
        setSizeFull();
        NativeLabel nowPlaying = new NativeLabel();
        horizontalLayout.add(closeDialog, nowPlaying);
        var url = "https://vidsrc.to/embed/movie/%s";
        showMovieButton.addClickListener(event -> UI.getCurrent().access(() -> {
            var value = textField.getValue().toLowerCase();
            embed.setSrc(url.formatted(value));
            nowPlaying.setText("Now Playing %s".formatted(value));
            dialog.open();
        }));
    }
}
