//package com.homeflix.app.views;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.Image;
//import com.vaadin.flow.component.html.NativeLabel;
//import com.vaadin.flow.component.orderedlayout.Scroller;
//import com.vaadin.flow.component.virtuallist.VirtualList;
//import com.vaadin.flow.data.renderer.ComponentRenderer;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.server.VaadinService;
//import jakarta.servlet.http.Cookie;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Route("list")
//public class ListView extends Scroller {
//    private static final List<Item> ITEMS;
//
//    static {
//        try {
//            var items = new ObjectMapper().readValue(new File("/Users/anuj/IdeaProjects/homeflix 2/src/main/resources/moviedb.json"), Item[].class);
//            ITEMS = Arrays.stream(items).toList();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    private final ComponentRenderer<Component, List<Item>> renderer = new ComponentRenderer<>(videoFile -> {
//        Div div = new Div();
//        videoFile.forEach(item -> {
//            var movieDiv = new Div();
//            movieDiv.getStyle().set("display", "inline-block");
//            movieDiv.setHeight("30%");
//            movieDiv.setWidth("30%");
//
//            Image image = new Image((item.posterLink() == null || item.posterLink().isBlank()) ? "icons/icon.png" : item.posterLink(), "");
//
//
//            image.setText(item.title());
//            image.setAlt(item.title());
//
//            image.setHeight("100%");
//            image.setWidth("90%");
//            image.getStyle().set("object-fit", "cover");
//            image.getStyle().set("margin", "10px");
//            image.getStyle().set("opacity", "0.5");
//            image.getStyle().set("cursor", "pointer");
//            image.getStyle().set("box-shadow", "10px 10px 10px black");
//            image.getStyle().set("transition", "all 0.3s ease 0s");
//
//            image.addClickListener(clickEvent -> {
//                VaadinService.getCurrentResponse().addCookie(new Cookie("mainURL", item.embed_url_imdb()));
//                UI.getCurrent().getPage().setLocation("p");
//            });
//            var text = new NativeLabel(item.title());
//
//            text.getStyle().set("color", "white");
//            text.getStyle().set("display", "inline-block");
//            text.getStyle().set("overflow", "hidden");
//            text.getStyle().set("text-overflow", "ellipsis");
//            text.getStyle().set("white-space", "nowrap");
//
//            text.setSizeFull();
//            text.setWidthFull();
//
//            movieDiv.add(image);
//            movieDiv.add(text);
//            div.add(movieDiv);
//        });
//        return div;
//    });
//
//    public ListView() {
//        setSizeFull();
//        VirtualList<List<Item>> itemVirtualList = new VirtualList<>();
//
//        List<List<Item>> griding = new ArrayList<>();
//        List<Item> items = new ArrayList<>();
//        var list = ITEMS;
//
//        for (int i = 0; i < list.size(); i++) {
//            items.add(list.get(i));
//            if (i % 3 == 0) {
//                griding.add(items);
//                items = new ArrayList<>();
//            }
//        }
//        itemVirtualList.setItems(griding);
//        itemVirtualList.setRenderer(renderer);
//        itemVirtualList.setSizeFull();
//        setContent(itemVirtualList);
//    }
//}
//
//
