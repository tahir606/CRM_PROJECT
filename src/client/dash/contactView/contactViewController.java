package client.dash.contactView;

import Email.EResponse.EResponseController;
import JCode.mysql.mySqlConn;
import JCode.trayHelper;
import client.dashBaseController;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import objects.ContactProperty;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class contactViewController implements Initializable {

    mySqlConn sql;

    @FXML
    AnchorPane contactAnchor;
    @FXML
    AnchorPane toolbar_contacts;
    @FXML
    Label txt_no;
    @FXML
    JFXButton btn_email;
    @FXML
    TableView<ContactProperty> table_contact;
    @FXML
    TableColumn<ContactProperty, String> col_name;
    @FXML
    TableColumn<ContactProperty, String> col_age;
    @FXML
    TableColumn<ContactProperty, String> col_email;
    @FXML
    TableColumn<ContactProperty, String> col_mobile;
    @FXML
    TableColumn<ContactProperty, String> col_city;
    @FXML
    TableColumn<ContactProperty, String> col_country;

    List<ContactProperty> selectedContacts;

    public static ContactProperty staticContact;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sql = new mySqlConn();

        toolbar_contacts.setVisible(false);

        table_contact.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        col_name.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        col_age.setCellValueFactory(new PropertyValueFactory<>("age"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_mobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        col_city.setCellValueFactory(new PropertyValueFactory<>("city"));
        col_country.setCellValueFactory(new PropertyValueFactory<>("country"));

        table_contact.getItems().setAll(sql.getAllContactsProperty(null));

        table_contact.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            selectedContacts = table_contact.getSelectionModel().getSelectedItems();

            if (selectedContacts == null || selectedContacts.size() == 0) {
                toolbar_contacts.setVisible(false);
                return;
            } else {
                toolbar_contacts.setVisible(true);
                txt_no.setText(String.valueOf(selectedContacts.size()));
            }

        });

        table_contact.setRowFactory(tv -> {
            TableRow<ContactProperty> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    staticContact = row.getItem();
                    try {
                        dashBaseController.main_paneF.setCenter(
                                FXMLLoader.load(
                                        getClass().getClassLoader().getResource
                                                ("client/dash/contactView/contactDetails/contactDetails.fxml")));
                    } catch (IOException e) {
                        e.getLocalizedMessage();
//                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

    }

    public void onEmailSending(ActionEvent actionEvent) {

        String email = "";

        for (ContactProperty contact : selectedContacts) {
            if (contact.getEmail() != null || contact.getEmail().equals(""))
                email = email + contact.getEmail() + ",";
        }

        System.out.println(email);

        EResponseController.stTo = email;
        EResponseController.stInstance = 'R';

        inflateEResponse(1);

    }

    private void inflateEResponse(int i) {
        try {
            EResponseController.choice = i;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../Email/EResponse/EResponse.fxml"));
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
