package com.homeflix.app.views.visualviewbuilder;

import com.homeflix.app.components.avataritem.AvatarItem;
import com.homeflix.app.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@PageTitle("Visual View Builder")
@Route(value = "visual-view-builder", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class VisualViewBuilderView extends Composite<VerticalLayout> {

    private AvatarItem avatarItem = new AvatarItem();

    private Hr hr = new Hr();

    private AvatarItem avatarItem2 = new AvatarItem();

    private Button buttonPrimary = new Button();

    public VisualViewBuilderView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
        setAvatarItemSampleData(avatarItem);
        setAvatarItemSampleData(avatarItem2);
        buttonPrimary.setText("Button");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(avatarItem);
        getContent().add(hr);
        getContent().add(avatarItem2);
        getContent().add(buttonPrimary);
    }

    private void setAvatarItemSampleData(AvatarItem avatarItem) {
        avatarItem.setHeading("Aria Bailey");
        avatarItem.setDescription("Endocrinologist");
        avatarItem.setAvatar(new Avatar("Aria Bailey"));
    }
}
