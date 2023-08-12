package com.homeflix.app.views;

import com.homeflix.app.data.controllers.AdminController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

@Route("search")
@PageTitle("Homeflix")
public class SearchView extends VerticalLayout {

    public SearchView(AdminController adminController, TMDBService service) {
        try {
            setAlignItems(FlexComponent.Alignment.CENTER);
            var backButton = new Button(VaadinIcon.ARROW_LEFT.create(), event -> UI.getCurrent().navigate(""));
            backButton.setWidthFull();
            add(backButton);
            add(new H3("Not all titles displayed can be played. Only English movies/series(still not all, but most)"));
            var title = (String) VaadinService.getCurrentRequest().getAttribute("title");
            var type = (String) VaadinService.getCurrentRequest().getAttribute("type");

            setClassName("animate-area");
            setId("animate-area");
            getStyle().set("border-radius", "25px");
            getStyle().set("overflow", "hidden");
            add(new NewView(adminController, false, DataProvider.ofCollection(service.getMoviesByName(title, type))));

            setSizeFull();
        } catch (Exception e) {
            var show = Notification.show("Something went wrong while looking for the request\n" + e.getMessage());
            show.setPosition(Notification.Position.TOP_STRETCH);
            show.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().navigateToClient("");
        }
    }


}

