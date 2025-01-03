package com.homeflix.app.views;

import com.homeflix.app.views.common.PassWallView;
import com.homeflix.app.views.netflix.AdminMessage;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route("terminal")
@PageTitle("Homeflix")
public class TerminalViewS extends PassWallView {
    private static final List<MessageListItem> commands = new ArrayList<>();
    private final TextArea outputArea = new TextArea("Output");
    private final Dialog history = new Dialog();

    public TerminalViewS() {
        super();
    }

    public void showContent() {
        add(new Button("Show history", e -> {
            var verticalLayout = new VerticalLayout();
            verticalLayout.setWidthFull();
            verticalLayout.add(new Button("Clear history", e1 -> {
                commands.clear();
                addUserMessage("clear");
                history.close();
            }));
            verticalLayout.add(new MessageList(Collections.unmodifiableList(commands)));
            history.add(verticalLayout);
            history.open();
        }));
        history.addOpenedChangeListener(openedChangeEvent -> {
            if (!openedChangeEvent.isOpened()) {
                history.removeAll();
            }
        });
        history.setSizeFull();
        setAlignItems(Alignment.CENTER);
        outputArea.setSizeFull();

        setWidthFull();
        var textField = new TextField("Enter command");
        var run = new Button("Run", e -> {
            main(textField.getValue(), UI.getCurrent());
            textField.clear();
            textField.focus();
        });
        textField.addKeyUpListener(Key.ENTER, keyUpEvent -> run.clickInClient());
        var clear = new Button("Clear", e -> {
            outputArea.clear();
        });
        add(history, textField, new HorizontalLayout(run, clear));
        add(outputArea);
    }

    @SneakyThrows
    public void main(String command, UI ui) {
        // Create a separate thread to run the command so it doesn't block the UI thread
        new Thread(() -> {
            try {
                addUserMessage(command);
                // Create the process to run the command
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                processBuilder.redirectErrorStream(true); // Merge stdout and stderr

                // Start the process
                Process process = processBuilder.start();

                // Read the output of the command
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                // Clear previous output and start streaming the result
                ui.access(() -> {
                    ui.getChildren().filter(c -> c instanceof TextArea).forEach(c -> {
                        TextArea textArea = (TextArea) c;
                        textArea.clear(); // Clear existing content before streaming new data
                    });
                });

                // Stream the output line-by-line to the TextArea
                while ((line = reader.readLine()) != null) {
                    // Use ui.access() to safely update the UI from the background thread
                    String finalLine = line;
                    ui.access(() -> {
                        outputArea.setValue(outputArea.getValue() + finalLine + "\n");

                    });
                }
                addSystemMessage(outputArea.getValue());
                // Wait for the command to complete and exit
                process.waitFor();
            } catch (Exception e) {
                UI.getCurrent().access(() -> {
                    Notification.show(e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void addUserMessage(String text) {
        addMessage(text, "user");
    }

    private void addSystemMessage(String text) {
        addMessage(text, "system");
    }

    private void addMessage(String text, String user) {
        commands.add(new MessageListItem(text, Instant.now(), user));
    }
}