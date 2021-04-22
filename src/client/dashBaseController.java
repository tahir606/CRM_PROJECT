package client;

import client.newClient.newClientController;
import client.newContact.newContactController;
import com.jfoenix.controls.JFXButton;
import dashboard.dController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class dashBaseController implements Initializable {

    @FXML
    private VBox vbox_menu;
    @FXML
    private BorderPane main_pane;
    @FXML
    private MenuBar menu_clients;

    public static BorderPane main_paneF;

    private ImageView img_loader = dController.img_load;
    private int currentPane = 0;

    private static int dashType = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        main_paneF = main_pane;

        populateCategoryBoxes();
    }

    JFXButton contacts = new JFXButton("Contacts");
    JFXButton clients = new JFXButton("Clients");

    private void populateCategoryBoxes() {

        contacts.setMinHeight(50);
        contacts.setMinWidth(65);
        contacts.getStyleClass().add("btnMenuBox");
        contacts.setAlignment(Pos.CENTER);
        contacts.setOnAction(event -> changeViewType(1, contacts));

        clients.setMinHeight(50);
        clients.setMinWidth(65);
        clients.getStyleClass().add("btnMenuBox");
        clients.setAlignment(Pos.CENTER);
        clients.setOnAction(event -> changeViewType(2, clients));

        vbox_menu.getChildren().addAll(contacts, clients);

        contacts.fire();

    }

    private void changeViewType(int i, JFXButton btn) {

        menu_clients.getMenus().clear();

        contacts.getStyleClass().remove("btnMenuBoxPressed");
        clients.getStyleClass().remove("btnMenuBoxPressed");

        btn.getStyleClass().add("btnMenuBoxPressed");

        dashType = i;

        switch (i) {
            case 1: {
                try {
                    populateContactsMenuBar();
                    main_pane.setCenter(
                            FXMLLoader.load(
                                    getClass().getClassLoader().getResource("client/dash/contactView/contactView.fxml")));

                } catch (IOException e) {
                    e.getLocalizedMessage();
//                    e.printStackTrace();
                }
                break;
            }
            case 2: {
                populateClientsMenuBar();
                try {
                    main_pane.setCenter(
                            FXMLLoader.load(
                                    getClass().getClassLoader().getResource("client/dash/clientView/clientView.fxml")));
                } catch (IOException e) {
                    e.getLocalizedMessage();
//                    e.printStackTrace();
                }
                break;
            }
        }

    }

    private void populateContactsMenuBar() {

        Menu menuNew = new Menu("New");

        MenuItem newContact = new MenuItem("New Contact");
        newContact.setOnAction(event -> {
            newContactController.stInstance = 'N';
            inflatePane("newContact/newContact.fxml", 2);
        });
        menuNew.getItems().addAll(newContact);

        menu_clients.getMenus().add(menuNew);
    }

    private void populateClientsMenuBar() {

        Menu menuNew = new Menu("New");

        MenuItem newClient = new MenuItem("New Client");
        newClient.setOnAction(event -> {
            newClientController.stInstance = 'N';
            inflatePane("newClient/newClient.fxml", 1);
        });
        menuNew.getItems().addAll(newClient);

        menu_clients.getMenus().add(menuNew);
    }

    private void inflatePane(String pane, int p) {
        img_loader.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        main_pane.setCenter(FXMLLoader.load(getClass().getResource(pane)));
                    } catch (IOException e) {
                        e.getLocalizedMessage();
//                        e.printStackTrace();
                    }
//                        pane_client.getChildren().add(admin_pane);
                    currentPane = p;
                    img_loader.setVisible(false);
                });
            }
        }).start();
    }
}
