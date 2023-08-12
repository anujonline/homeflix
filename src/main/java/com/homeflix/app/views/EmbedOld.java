package com.homeflix.app.views;

import com.homeflix.app.data.controllers.AdminController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("old")
@PageTitle("Homeflix")
public class EmbedOld extends VerticalLayout {
    private static final String URL = "https://vidsrc.to/embed/%s/%s";
    private final String ERROR_MESSAGE = "Can not be empty";
    private final String HELP_FINDING_TITLE_ID = "Help Finding IMDB ID";
    private final Button HELP_FINDING_TITLE_BUTTON = new Button(HELP_FINDING_TITLE_ID, VaadinIcon.QUESTION.create());
    private final Image HELP_IMAGE = new Image("/icons/showhelp.jpg", "");
    private final NativeLabel HELP_TEXT_1 = new NativeLabel("1. Open any browser and open imdb.com");
    private final NativeLabel HELP_TEXT_2 = new NativeLabel("2. search Movie of wanna watch.");
    private final NativeLabel HELP_TEXT_3 = new NativeLabel("3. Copy encircled title ID and input in the field");
    private final Button SHOW_MOVIE_BUTTON = new Button("Watch", VaadinIcon.PLAY_CIRCLE.create());
    private final TextField imdbId = new TextField("Enter IMDB ID");
    private final RadioButtonGroup<String> type = new RadioButtonGroup<>("Movie or Series?", List.of("movie", "tv"));

    public EmbedOld(AdminController adminController) {
        setClassName("hero");
        setAlignItems(Alignment.CENTER);
        var image = new Image("icons/icon.png", "");

        image.setMaxHeight("10%");
        add(image);
        getStyle().set("background-image", "url('icons/bg.jpg')");
        imdbId.setMinLength(1);
        imdbId.setErrorMessage(ERROR_MESSAGE);
        type.setErrorMessage(ERROR_MESSAGE);

        add("To get an IMDB ID, go to IMDB and select any movie.");
        add("On the Browser address bar copy the title ID.");
        var helpDialog = new Dialog();
        add(helpDialog);
        add(HELP_FINDING_TITLE_BUTTON);
        HELP_FINDING_TITLE_BUTTON.setWidthFull();
        HELP_FINDING_TITLE_BUTTON.addClickListener(event -> {
            var verticalLayout = new VerticalLayout();
            HELP_IMAGE.setSizeFull();
            verticalLayout.add(HELP_TEXT_1, HELP_TEXT_2, HELP_TEXT_3, HELP_IMAGE);
            helpDialog.add(verticalLayout);
            helpDialog.open();
        });
        imdbId.setWidthFull();
        imdbId.setRequired(true);
        imdbId.setRequiredIndicatorVisible(true);
        SHOW_MOVIE_BUTTON.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        SHOW_MOVIE_BUTTON.setWidthFull();
        add(imdbId);
        type.setRequired(true);
        type.setRequiredIndicatorVisible(true);
        type.setSizeFull();
        add(type);

        add(SHOW_MOVIE_BUTTON);
        var horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        var parameter = "https://vidsrc.to/embed/movie/tt17048514";
        var embed = new Embed(parameter);
        var closeDialog = new Button("Close Player", VaadinIcon.ARROW_LEFT.create());
        var dialog = new Dialog();
        dialog.setSizeFull();
        closeDialog.addClickListener(event -> dialog.close());
        add(dialog);
        dialog.add(horizontalLayout, embed);
        setSizeFull();
        horizontalLayout.add(closeDialog);

        SHOW_MOVIE_BUTTON.addClickListener(event -> {
            if ((imdbId.getValue() == null || imdbId.getValue().isEmpty()) || (type.getValue() == null || type.getValue().isEmpty())) {
                var show = Notification.show("You need to add imdbId and select type (movie or tv)");
                show.setPosition(Notification.Position.TOP_STRETCH);
                show.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                var ui = UI.getCurrent();
                adminController.addHistory(ui.getSession().getBrowser().getAddress(), imdbId.getValue());
                ui.access(() -> {
                    var showType = type.getValue();
                    if (showType == null || showType.isEmpty()) {
                        showType = "movie";
                    }
                    var value = imdbId.getValue().toLowerCase();
                    embed.setSrc(URL.formatted(showType, value));
                    dialog.open();
                });
            }
        });
    }
}
