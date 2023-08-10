//package com.homeflix.app.views;
//
//import com.homeflix.app.data.service.VAccess;
//import com.homeflix.app.views.browse.BrowseView;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.orderedlayout.Scroller;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.tabs.Tab;
//import com.vaadin.flow.component.tabs.Tabs;
//import com.vaadin.flow.router.PreserveOnRefresh;
//import com.vaadin.flow.router.Route;
//
//@Route("home")
//@PreserveOnRefresh
//public class HomeView extends Scroller {
//
//    private static final String HINDI = "Hindi";
//    private static final String ENGLISH = "English";
//
//    public HomeView(VAccess videoService) {
//        var b =new BrowseView(videoService);
////        var l =new ListView();
//        var verticalLayout = new VerticalLayout();
//        Tabs tabs
//                = new Tabs();
//        var hindi = new Tab(HINDI);
//        var english = new Tab(ENGLISH);
//        tabs.add(english, hindi);
//        tabs.setSizeFull();
//
//        Div d = new Div();
//        tabs.addSelectedChangeListener(event -> {
//            d.setHeight("50%");
//            d.setWidth("50%");
//            d.removeAll();
//            if (event.getSelectedTab().getLabel().contentEquals(HINDI)) {
//                UI.getCurrent().access(() -> d.add(b));
//            } else {
//                UI.getCurrent().access(() -> d.add(l));
//            }
//
//        });
////        tabs.setSelectedTab(english);
//        verticalLayout.add(tabs,d);
//        setContent(verticalLayout);
////        setContent(d);
//    }
//}
