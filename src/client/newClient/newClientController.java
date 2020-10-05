package client.newClient;

import JCode.CommonTasks;
import JCode.Toast;
import JCode.mysql.mySqlConn;
import JCode.trayHelper;
import client.dash.clientView.clientViewController;
import client.dashBaseController;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import objects.ClientProperty;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class newClientController implements Initializable {

    @FXML
    private JFXTextField txt_name;
    @FXML
    private JFXTextField txt_owner;
    @FXML
    private JFXButton btn_email;
    @FXML
    private JFXButton btn_phone;
    @FXML
    private JFXButton btn_back;
    @FXML
    private JFXTextField txt_website;
    @FXML
    private JFXTextField txt_addr;
    @FXML
    private JFXTextField txt_city;
    @FXML
    private JFXTextField txt_country;
    @FXML
    private Label txt_heading;
    @FXML
    private JFXDatePicker joining_date;
    @FXML
    private JFXComboBox<String> combo_type;
    //    @FXML
//    private JFXComboBox<Client> combo_client;
    @FXML
    private JFXButton btn_save;

    private mySqlConn sql;

    private List<ClientProperty> clientList;
    private List<String> types;
    private int nClient;    //CL_ID for new Client

    private ClientProperty clientSel;

    public static char stInstance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Image image = new Image(this.getClass().getResourceAsStream("/res/img/left-arrow.png"));
        btn_back.setGraphic(new ImageView(image));
        btn_back.setAlignment(Pos.CENTER_LEFT);
        btn_back.setTooltip(new Tooltip("Back to Contacts"));
        btn_back.setOnAction(event -> {
            try {
                dashBaseController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("client/dash/clientView/clientView.fxml")));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sql = new mySqlConn();

        types = sql.getClientTypes();
        combo_type.getItems().setAll(types);

        switch (stInstance) {
            case 'N': {      //New
                txt_heading.setText("New Client");
                btn_save.setText("Create");
                init();
                break;
            }
            case 'U': {      //Update
                txt_heading.setText("Update Client");
                clientSel = clientViewController.staticClient;
                btn_save.setText("Update");
                populateDetails(clientSel);
                break;
            }
            default:
                break;
        }
    }

    private void init() {
        clientList = sql.getAllClients(null);

        try {
            nClient = clientList.get(clientList.size() - 1).getCode() + 1; //Get CL_ID for new client
        } catch (NullPointerException e) {
            nClient = 1;
        }

        clientSel = new ClientProperty();
        clientSel.setCode(nClient);
        clientSel.setName(" + Create New");
        clientSel.setOwner("");
        clientSel.setWebsite("");
        clientSel.setAddr("");
        clientSel.setCity("");
        clientSel.setCountry("");
        clientSel.setType(1);
        clientSel.setEmails(new String[noOfFields]);
        clientSel.setPhones(new String[noOfFields]);

        combo_type.getSelectionModel().select(0);

    }

    private void populateDetails(ClientProperty newValue) {
        if (newValue == null)
            return;
        else if (newValue.getName().equals(" + Create New"))
            txt_name.setText("");
        else
            txt_name.setText(newValue.getName());

        txt_owner.setText(newValue.getOwner());
        txt_website.setText(newValue.getWebsite());
        txt_addr.setText(newValue.getAddr());
        txt_city.setText(newValue.getCity());
        txt_country.setText(newValue.getCountry());
        if (newValue.getJoinDate() != null)
            joining_date.setValue(CommonTasks.createLocalDate(newValue.getJoinDate()));
        else
            joining_date.setValue(null);

        combo_type.getSelectionModel().select(newValue.getType() - 1);  //Types in database start from 1

    }

    public void saveChanges(ActionEvent actionEvent) {
        String name = txt_name.getText(),
                owner = txt_owner.getText(),
                website = txt_website.getText(),
                addr = txt_addr.getText(),
                city = txt_city.getText(),
                country = txt_country.getText(),
                jdate = String.valueOf(joining_date.getValue());

        System.out.println(owner);

        int type = combo_type.getSelectionModel().getSelectedIndex() + 1;

        if (name.equals("") || city.equals("") || country.equals("")) {
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Required Fields Are Empty");
            return;
        } else {
            String msg = "";
            switch (stInstance) {
                case 'N': {
                    msg = "Are you sure you want to add Client?";
                    break;
                }
                case 'U': {
                    msg = "Are you sure you want to update Client?";
                    break;
                }
                default: {
                    break;
                }
            }
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, msg,
                    ButtonType.YES, ButtonType.NO);
            alert2.showAndWait();

            if (alert2.getResult() == ButtonType.YES) {

                clientSel.setName(name);
                clientSel.setOwner(owner);
                clientSel.setWebsite(website);
                clientSel.setAddr(addr);
                clientSel.setCity(city);
                clientSel.setCountry(country);
                clientSel.setJoinDate(jdate);
                clientSel.setType(type);

                switch (stInstance) {
                    case 'N': {
                        sql.insertClient(clientSel);
                        break;
                    }
                    case 'U': {
                        sql.updateClient(clientSel);
                        break;
                    }
                }
//                init();   //WHere to after

            } else {
                return;
            }
        }

    }

    public void inflateEmail(ActionEvent actionEvent) {
        inflateBOX(1);
    }

    public void inflatePhone(ActionEvent actionEvent) {
        inflateBOX(2);
    }

    public static int noOfFields = 30;

    private void inflateBOX(int c) {

        String[] Emails = new String[noOfFields];
        String[] Phones = new String[noOfFields];

        switch (stInstance) {
            case 'N': {
                Emails = new String[noOfFields];
                Phones = new String[noOfFields];
                break;
            }
            case 'U': {
                Emails = clientSel.getEmails();
                Phones = clientSel.getPhones();
                break;
            }
            default: {
                break;
            }
        }


        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        String title = (c == 1) ? "Email" : "Phone";
        stage.setTitle(title);
        VBox pane = new VBox();
        pane.setMinWidth(200);
        pane.setMinHeight(230);
        for (int i = 0; i < noOfFields; i++) {
            JFXTextField txt_data = new JFXTextField();
            txt_data.setMinWidth(pane.getWidth());
            txt_data.setPadding(new Insets(2, 2, 2, 2));
            txt_data.setFocusColor(Paint.valueOf("#006e0e"));
            pane.getChildren().add(txt_data);
        }

        //On inflating
        for (int i = 0; i < noOfFields; i++) {
            if (c == 1)        //Email
                ((JFXTextField) pane.getChildren().get(i)).setText(Emails[i]);
            else if (c == 2)    //Phone
                ((JFXTextField) pane.getChildren().get(i)).setText(Phones[i]);
        }

        stage.setScene(new Scene(pane));
        trayHelper tray = new trayHelper();
        tray.createIcon(stage);
        Platform.setImplicitExit(true);

        stage.setOnHiding(event -> {

            String array[] = new String[noOfFields];

            for (int i = 0; i < noOfFields; i++) {
                String t = ((JFXTextField) pane.getChildren().get(i)).getText();
                if (t != null) {
                    if (!t.equals("")) {
                        if (c == 1)        //Email
                            array[i] = t;
                        else if (c == 2)    //Phone
                            array[i] = t;
                    }
                }
            }
            if (c == 1) {
                clientSel.setEmails(array);
            }else if (c == 2)
                clientSel.setPhones(array);
        });

        stage.show();
    }
}
