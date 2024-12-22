package com.homeflix.app.views.netflix;

import com.homeflix.app.JC;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.server.VaadinSession;

public class TimerLayout extends AppLayout  {
    private final JC jc;
    public TimerLayout(JC jc) {
        this.jc = jc;
        jc.startSession(VaadinSession.getCurrent().getSession().getId());
        UI.getCurrent().addDetachListener(detachEvent -> {
            jc.stopSession(VaadinSession.getCurrent().getSession().getId());
        });
    }
}
