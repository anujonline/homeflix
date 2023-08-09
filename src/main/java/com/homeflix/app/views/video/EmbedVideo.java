package com.homeflix.app.views.video;

import com.homeflix.app.data.service.VAccess;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;


@PageTitle("HomeFlix")
@Route(value = "play")
@JavaScript(value = "https://vjs.zencdn.net/7.14.3/video.min.js", loadMode = LoadMode.EAGER)
@StyleSheet(value = "https://vjs.zencdn.net/7.14.3/video-js.css", loadMode = LoadMode.EAGER)
@PermitAll
public class EmbedVideo extends VerticalLayout implements HasUrlParameter<String> {
    private final VAccess vAccess;

    public EmbedVideo(VAccess vAccess) {
        super();
        this.vAccess = vAccess;
        getStyle().set("background", """
                linear-gradient(black,#501414)
                """);
        setWidthFull();
        setHeightFull();
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String s) {
        var videoFile = vAccess.getVideoFile(s);
        getStyle().set("background", """
                black)
                """);
        var back = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        back.addThemeVariants(ButtonVariant.LUMO_ICON);
        back.setWidthFull();
        add(back);

        back.addClickListener(event -> UI.getCurrent().navigate(""));
        addAttachListener(event -> {
            UI.getCurrent().access(() -> {
                var video = new Video(videoFile.getFullPath());

                video.setSizeFull();
                H1 header = new H1("Now Playing ");
                header.getStyle()
                        .set("color", "white");
                H1 movieName = new H1("name");
                movieName
                        .getStyle()
                        .set("color", "white");
                header.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);
                add(header);
                setHeightFull();
                setWidthFull();
                add(back);
                add(video);
            });

        });
    }
}
//<video class="vjs-tech" data-setup="{'fluid': true}" id="my-video_html5_api" type="video/mp4" preload="auto" style="width: 100%; height: 100%;" tabindex="-1" src="blob:http://localhost:8080/a663c1d0-5b56-4e0f-923e-af495d283cfc"><source src="video/identifier/https%253A%252F%252Fxis.fifteennet.com%252F_v10%252F0db3086e7a22f9ed3b28cde10c2ebd49454fbf30f0a3acca80dc4a0e67d9493b7522ca49e837e91d14929942b074b49fc751e8f52948d9619cdceff79161cdd57ef9535b4b00468f85188e51527b849a0dbefc3956afe559a8adaf6fc8589fb62d87bcd5db5903dd0273d3138b2649d990881c4b2c04910ae242d2e8a927cc4999bf543291abdd0062c8d28608331831%252F720%252Findex.m3u8" type="application/x-mpegURL" style="width: 100%; height: 100%;"></video>
//<source src="video/identifier/https%253A%252F%252Fxis.fifteennet.com%252F_v10%252F0db3086e7a22f9ed3b28cde10c2ebd49454fbf30f0a3acca80dc4a0e67d9493b7522ca49e837e91d14929942b074b49fc751e8f52948d9619cdceff79161cdd57ef9535b4b00468f85188e51527b849a0dbefc3956afe559a8adaf6fc8589fb62d87bcd5db5903dd0273d3138b2649d990881c4b2c04910ae242d2e8a927cc4999bf543291abdd0062c8d28608331831%252F720%252Findex.m3u8" type="application/x-mpegURL" style="width: 100%; height: 100%;">
