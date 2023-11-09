package com.homeflix.app.views;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.homeflix.app.data.Broadcaster;
import com.homeflix.app.data.RemoteAccessDTO;
import com.homeflix.app.views.common.Marquee;
import com.homeflix.app.views.netflix.NetfliView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.homeflix.app.views.netflix.PlayConstants.REMOTE_VIEWING_IS_STILL_IN_BETA;

@Route("remote")
@PageTitle("Homeflix")
public class RemoteView extends VerticalLayout {
    private static final String REMOTE_ACCESS_S = "http://192.168.178.248:8080/watch-remote/%s";
    private static final Marquee MARQUEE = new Marquee(REMOTE_VIEWING_IS_STILL_IN_BETA);
    private final Dialog dialog = new Dialog();
    private final QRCodeWriter qrCodeWriter = new QRCodeWriter();
    private final NativeLabel nativeLabel = new NativeLabel("You can change movies now only via remote. Click on play button here to start watching.");
    private final Button button = new Button("Go back to home", LumoIcon.CROSS.create(), e -> {
        dialog.close();
        this.removeAll();
        UI.getCurrent().navigate(NetfliView.class);
    });
    private final Embed embed = new Embed();
    private Registration broadcaster;

    public RemoteView() {
        add(MARQUEE);
        dialog.setSizeFull();
    }

    public byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig con = new MatrixToImageConfig(0xFF000002, 0xFFFFC041);

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
        UUID uuid = UUID.randomUUID();
        var url = REMOTE_ACCESS_S.formatted(uuid.toString());
        System.out.println(url);
        Image img = new Image(new StreamResource("", new InputStreamFactory() {
            @SneakyThrows
            @Override
            public InputStream createInputStream() {
                return new ByteArrayInputStream(getQRCodeImage(url, 540, 540));
            }
        }), "");
        add(img, new H2("Scan this QR code with your phone, and enjoy remote viewing."));
        UI ui = attachEvent.getUI();
        VaadinSession.getCurrent().setAttribute("id", uuid.toString());
        broadcaster = Broadcaster.register(remoteAccessDTO -> ui.access(() -> showMovie(remoteAccessDTO)));
    }

    private void showMovie(RemoteAccessDTO remoteAccessDTO) {
        if (remoteAccessDTO.getId().equals(VaadinSession.getCurrent().getAttribute("id"))) {
            dialog.removeAll();
            dialog.add(MARQUEE, button, nativeLabel);
            embed.setSrc(remoteAccessDTO.getUrl());
            dialog.add(embed);
            dialog.open();
        }

    }
}
