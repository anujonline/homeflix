package com.homeflix.app.views;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.homeflix.app.data.Broadcaster;
import com.homeflix.app.data.RemoteAccessDTO;
import com.homeflix.app.views.common.Marquee;


import com.homeflix.app.views.netflix.NewHome;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import lombok.SneakyThrows;
import org.springframework.web.context.annotation.SessionScope;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.homeflix.app.views.netflix.PlayConstants.REMOTE_VIEWING_IS_STILL_IN_BETA;

@Route("remote")
@PageTitle("Homeflix")
@SessionScope
public class RemoteView extends VerticalLayout {
    private static final String REMOTE_ACCESS_S = "https://homeflix.onrender.com/watch-remote/%s";
    private static final String INFORMATION_MESSAGE = "Unlock movie magic! Scan the QR code with your phone, open the link, and choose any movie to play on the screen where the QR code is shown or click the link above.";
    //        private static final String REMOTE_ACCESS_S = "http://192.168.178.248:8080/watch-remote/%s";
    private final Marquee MARQUEE = new Marquee(REMOTE_VIEWING_IS_STILL_IN_BETA);
    private final Dialog dialog = new Dialog();
    private final QRCodeWriter qrCodeWriter = new QRCodeWriter();
    private final MatrixToImageConfig con = new MatrixToImageConfig(0xFFfb1c13, MatrixToImageConfig.WHITE);
    private final NativeLabel nativeLabel = new NativeLabel("You can change movies now only via remote. Click on play button here to start watching.");
    private final Button button = new Button("Home", VaadinIcon.HOME.create(), e -> {
        dialog.close();
        this.removeAll();
        UI.getCurrent().navigate(NewHome.class);
    });
    private final Embed embed = new Embed();
    private Registration broadcaster;

    public RemoteView() {
        add(MARQUEE);
        dialog.setSizeFull();
    }

    public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        var pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
        return pngOutputStream.toByteArray();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.broadcaster.remove();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        var id = UUID.randomUUID().toString();
        var url = REMOTE_ACCESS_S.formatted(id);
        add("Scan or enter this url on other device: " + url);
        Image img = new Image(new StreamResource("", new InputStreamFactory() {
            @SneakyThrows
            @Override
            public InputStream createInputStream() {
                return new ByteArrayInputStream(getQRCodeImage(url, 340, 340));
            }
        }), "");
        add(new Button("End remote viewing",

                        VaadinIcon.ARROW_LEFT.create(),
                        e -> UI.getCurrent().navigate(NewHome.class)),
                img,
                new H2(INFORMATION_MESSAGE));

        UI ui = attachEvent.getUI();
        VaadinSession.getCurrent().setAttribute("id", id);
        broadcaster = Broadcaster.register(id, remoteAccessDTO -> ui.access(() -> showMovie(remoteAccessDTO)));
    }

    private void showMovie(RemoteAccessDTO remoteAccessDTO) {
        dialog.removeAll();
        dialog.add(MARQUEE, button, nativeLabel);
        embed.setSrc(remoteAccessDTO.url());
        dialog.add(embed);
        dialog.open();
    }
}
