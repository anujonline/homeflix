package com.homeflix.app.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@Route(value = "")
@StyleSheet(value = "./splash.css", loadMode = LoadMode.EAGER)
public class WelcomeScreen extends VerticalLayout {

    public WelcomeScreen() {
        getElement().setProperty("innerHTML", """
                <input class="retrigger" type="radio" name="rerun" id="retrigger--2" checked="checked"/>
                <div class="bg"></div>
                <div class="buttons" onclick="document.location.href='/watch'">
                    <label class="button button--1">ENTER</label>
                </div>
                <p>You should be automatically redirected in <span id="seconds">8</span> seconds.
                    </p>
                <div class="pane">
                    <div class="rotate">
                        <div class="logo">HOMEFLIX</div>
                    </div>
                </div>
                """);
        UI.getCurrent().getPage().executeJs("""
                var seconds = 8; // seconds for HTML
                var foo; // variable for clearInterval() function
                                
                function redirect() {
                    document.location.href = '/watch';
                }
                                
                function updateSecs() {
                    document.getElementById("seconds").innerHTML = seconds;
                    seconds--;
                    if (seconds == -1) {
                        clearInterval(foo);
                        redirect();
                    }
                }
                                
                function countdownTimer() {
                    foo = setInterval(function () {
                        updateSecs()
                    }, 1000);
                }
                                
                countdownTimer();
                """, "");
    }
}