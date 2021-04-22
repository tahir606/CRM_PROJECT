package client.dash.clientView.clientDetails;

import Email.EResponse.EResponseController;
import com.jfoenix.controls.JFXTextArea;
import gui.EventsConstructor;
import gui.TasksConstructor;
import gui.NotesConstructor;
import JCode.mysql.mySqlConn;
import JCode.trayHelper;
import client.dash.clientView.clientViewController;
import client.dashBaseController;
import client.newClient.newClientController;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.ClientProperty;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class clientDetailsController implements Initializable {

    @FXML
    private Label txt_fname, txt_website, txt_owner, txt_joinDate, txt_city, txt_country;
    @FXML
    private JFXTextArea txt_address;
    @FXML
    private ListView<String> email_list, phone_list;
    @FXML
    private JFXButton btn_back, btn_email, btn_edit;
    @FXML
    private VBox vbox_main;

    private mySqlConn sql;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sql = new mySqlConn();

        Image image = new Image(this.getClass().getResourceAsStream("/res/img/left-arrow.png"));
        btn_back.setGraphic(new ImageView(image));
        btn_back.setAlignment(Pos.CENTER_LEFT);
        btn_back.setTooltip(new Tooltip("Back to Clients"));
        btn_back.setOnAction(event -> {
            try {
                dashBaseController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("client/dash/clientView/clientView.fxml")));

            } catch (IOException e) {
                e.getLocalizedMessage();
//                e.printStackTrace();
            }
        });

        ClientProperty client = clientViewController.staticClient;

        btn_email.setOnAction(event -> {
            EResponseController.stTo = client.getEmailsString();
            EResponseController.stInstance = 'N';
            inflateEResponse(1);
        });

        btn_edit.setOnAction(event -> {
            newClientController.stInstance = 'U';
            try {
                dashBaseController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("client/newClient/newClient.fxml")));
            } catch (IOException e) {
                e.getLocalizedMessage();
//                e.printStackTrace();
            }
        });

        txt_fname.setText(client.getName());
        txt_website.setText(client.getWebsite());
        txt_owner.setText(client.getOwner());
        txt_joinDate.setText(client.getJoinDate());
        txt_city.setText(client.getCity());
        txt_country.setText(client.getCountry());

        txt_address.setText(client.getAddr());

        for (String email : client.getEmails()) {
            if (email != null)
                email_list.getItems().add(email);
        }

        for (String phone : client.getPhones()) {
            if (phone != null)
                phone_list.getItems().add(phone);
        }
        
        TabPane tabPane = new TabPane();
        tabPane.setMinWidth(600);
        new NotesConstructor(tabPane, sql, client).generalConstructor(2);
        new TasksConstructor(tabPane, client).generalConstructor(2);
        new EventsConstructor(tabPane, client).generalConstructor(2);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);

        vbox_main.getChildren().add(tabPane);

    }

    private void inflateEResponse(int i) {
        try {
            EResponseController.choice = i;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../Email/EResponse/EResponse.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage2 = new Stage();
            stage2.setTitle("New Email");
            stage2.setScene(new Scene(root1));
            trayHelper tray = new trayHelper();
            tray.createIcon(stage2);
            Platform.setImplicitExit(true);
            stage2.show();
        } catch (IOException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }
}
