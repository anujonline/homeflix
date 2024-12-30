package com.homeflix.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Route("terminal")
@PageTitle("Homeflix")
public class TerminalViewS extends VerticalLayout {
    public TerminalViewS() {

        var textField = new TextField("Enter command");
        add(textField, new Button("Run", e-> {
            main(textField.getValue(), UI.getCurrent());
        }));
        add(outputArea);
    }
    private static final TextArea outputArea = new TextArea("Output");
    @SneakyThrows
    public static void main(String command, UI ui){
        // Create a separate thread to run the command so it doesn't block the UI thread
        new Thread(() -> {
            try {
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

                // Wait for the command to complete and exit
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}