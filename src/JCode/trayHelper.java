package JCode;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class trayHelper {

    public static TrayIcon trayIcon;

    java.awt.Image image = null;
    public static SystemTray tray;

    public trayHelper() {
    }

    public void createIcon(Stage stage) {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/res/img/icon.png")));
    }

    public void createTrayIcon(Stage stage) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (tray != null) {
                    tray.remove(trayIcon);
                }

                image = new ImageIcon(this.getClass().getResource("/res/img/icon-tray.png")).getImage();

                if (SystemTray.isSupported()) {
                    tray = SystemTray.getSystemTray();
                }

                Platform.runLater(() -> stage.getIcons().add(new Image(getClass().getResourceAsStream("/res/img/icon.png"))));



                stage.setOnCloseRequest(event -> hide(stage));

                final ActionListener closeListener = e -> {
                    Platform.exit();
                    System.exit(0);
                    tray.remove(trayIcon);
                };

                ActionListener showListener = e -> Platform.runLater(() -> stage.show());

                PopupMenu popup = new PopupMenu();

                MenuItem showItem = new MenuItem("Open");
                showItem.addActionListener(showListener);
                popup.add(showItem);

                MenuItem closeItem = new MenuItem("Close");
                closeItem.addActionListener(closeListener);
                popup.add(closeItem);

                trayIcon = new TrayIcon(image, "BITS-CRM", popup);
                trayIcon.setImageAutoSize(true);

                trayIcon.addActionListener(showListener);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    e.getLocalizedMessage();
//                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void hide(final Stage stage) {
        Platform.runLater(() -> {
            if (SystemTray.isSupported()) {
                stage.hide();

            } else {
                System.exit(0);
            }
        });
    }

    public void removeTray() {
        tray.remove(trayIcon);
    }

    private static int times = 0;   //Notification will be tried 3 times

    public void displayNotification(String caption, String msg) {
        if (times == 3) {
            times = 0;
            return;
        }
        try {
            trayIcon.displayMessage(caption, msg, TrayIcon.MessageType.INFO);
            times = 0;
        } catch (NullPointerException e) {
            System.out.println("Tray Helper: " + e);
            times++;
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                displayNotification(caption, msg);
            }).start();
        }
    }

}
