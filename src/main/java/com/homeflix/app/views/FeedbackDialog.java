package com.homeflix.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FeedbackDialog extends Dialog {

    private final TextArea field0Nuy = new TextArea("Message for creators of Homeflix");
    private final Checkbox fieldCrUG = new Checkbox("I want to signin and save my history, create playlists and more.");
    private final Checkbox fieldY8Ov = new Checkbox("I want a community review and chat option with fellow homeflixers");
    private final Checkbox fieldK4bb = new Checkbox("I want to see 'only' content which is available to watch");
    private final RadioButtonGroup<String> fieldZwY9 = new RadioButtonGroup<>("I have followed Homeflix on Instagram (Link on homepage).");

    public FeedbackDialog() {
        createDialogLayout();
        this.setSizeFull();
        field0Nuy.setPlaceholder("Do you know you can install Homeflix as an App on your phone and PC?");
        field0Nuy.setWidthFull();
    }

    private void createDialogLayout() {
        Button closeDialog = new Button(LumoIcon.CROSS.create(), e -> this.close());
        closeDialog.addThemeVariants(ButtonVariant.MATERIAL_OUTLINED);
        add(closeDialog);
        setWidth("400px");
        setHeight("400px");
        fieldZwY9.setItems(List.of("yes", "no"));
        Button submitButton = new Button("Submit", e -> handleSubmit());
        add(new H2("Help with a small feedback survey."));
        VerticalLayout layout = new VerticalLayout(new H2("Future what do you wanna see? Select what you want."), fieldCrUG, fieldY8Ov, fieldK4bb, new H2("Provide Your Feedback"), field0Nuy, fieldZwY9, submitButton);

        add(layout);
    }

    private void handleSubmit() {
        // Collect values of all checkboxes
        try {
            String isEnjoying = field0Nuy.getValue();
            boolean isSignin = fieldCrUG.getValue();
            boolean isChat = fieldY8Ov.getValue();
            boolean isMyList = fieldK4bb.getValue();
            boolean isFollowed = "yes".equals(fieldZwY9.getValue());
            WebStorage.setItem("clnzoxcy10001vy2ohi4obbi0", "true");
            // Execute the client-side POST request
            executeClientSideCurl(isEnjoying, isSignin, isChat, isMyList, isFollowed);

            // Close the dialog
        } catch (Exception e) {
            log.error("Error submitting survey", e);
        } finally {
            close();
        }
    }

    private void executeClientSideCurl(String isEnjoying, boolean isSignin, boolean isChat, boolean isMyList, boolean isFollowed) {
        String js = """
                (function() {
                    const payload = {
                        payload: {
                            field_0Nuy: $0,
                            field_crUG: $1,
                            field_Y8Ov: $2,
                            field_k4bb: $3,
                            field_ZwY9: $4
                        }
                    };
                
                    fetch("https://admin.pcitrix.com/open/workspace/clnzoxcy10001vy2ohi4obbi0/survey/cm5sa0n0kaob4rkk2uquhjwho/submit", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(payload)
                    })
                    .then(response => {
                        if (!response.ok) {
                            throw new Error("Network response was not ok");
                        }
                        return response.json();
                    })
                    .then(data => console.log("Success:", data))
                    .catch(error => console.error("Error:", error));
                })();
                """;

        // Inject JavaScript with boolean values
        UI.getCurrent().getPage().executeJs(js, String.valueOf(isEnjoying), String.valueOf(isSignin), String.valueOf(isChat), String.valueOf(isMyList), String.valueOf(isFollowed));
    }
}