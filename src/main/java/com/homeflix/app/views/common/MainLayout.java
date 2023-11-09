package com.homeflix.app.views.common;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    public MainLayout() {
        getStyle().set("border-radius", "25px");
        getStyle().set("overflow", "hidden");
        var bellBtn = new MessagesButton();
        bellBtn.setUnreadMessages(1);

        var menu = new ContextMenu();
        menu.setOpenOnClick(true);
        menu.setTarget(bellBtn);
        menu.addItem("Chrome issue is now resolved, you can continue using your favourite browser");

        addToNavbar(bellBtn);
    }

    private static class MessagesButton extends Button {

        private final Element numberOfNotifications;

        private MessagesButton() {
            super(VaadinIcon.BELL_O.create());
            numberOfNotifications = new Element("span");
            numberOfNotifications.getStyle().setPosition(Style.Position.ABSOLUTE).setTransform("translate(-40%, -85%)");

        }

        void setUnreadMessages(int unread) {
            numberOfNotifications.setText(unread + "");
            if (unread > 0 && numberOfNotifications.getParent() == null) {
                getElement().appendChild(numberOfNotifications);
            } else if (numberOfNotifications.getNode().isAttached()) {
                numberOfNotifications.removeFromParent();
            }
        }

    }
}

