package settings.network;

import JCode.Toast;
import JCode.FileHelper;
import JCode.mysql.mySqlConn;
import JCode.trayHelper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.Network;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class networkSetController implements Initializable {

    @FXML
    private JFXTextField txt_ip;
    @FXML
    private JFXTextField txt_port;
    @FXML
    private JFXButton btn_save;

    public static boolean fromMain;     //To check if network setting is from main or not

    FileHelper fHelper;
    trayHelper tHelper;

//    private final static String DB_NAME = "bits_crm",
//            USER = "root",
//            PASS = "tahir123!@#";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fHelper = new FileHelper();
        tHelper = new trayHelper();

        Network network = fHelper.getNetworkDetails();
        if (network != null) {
            txt_ip.setText(network.getHost());
            txt_port.setText(String.valueOf(network.getPort()));
        }
    }

    public void saveChanges(ActionEvent actionEvent) {

        String ip = txt_ip.getText(),
                port = txt_port.getText();

        if (ip.equals("") || port.equals("")) {
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Fields cannot be null");
            return;
        }

        boolean ping = mySqlConn.pingHost(ip, Integer.parseInt(port), 2000);

        if (ping) {
            fHelper.WriteNetwork(new Network(ip, Integer.parseInt(port)));

            if (fromMain == true) {

                Stage stage = (Stage) btn_save.getScene().getWindow();
                stage.close();

                Stage primaryStage = new Stage();

                if (fHelper.ReadUserDetails() == null) {
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("../../login/login.fxml"));
                    } catch (IOException e) {
                        e.getLocalizedMessage();
//                        e.printStackTrace();
                    }
                    primaryStage.setTitle("Login- BITS-CRM");
                    primaryStage.setScene(new Scene(root, 900, 400));;
                    tHelper.createIcon(primaryStage);
                    primaryStage.show();
                } else {
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("../../dashboard/dashboard.fxml"));
                    } catch (IOException e) {
                        e.getLocalizedMessage();
//                        e.printStackTrace();
                    }
                    primaryStage.setTitle("Dashboard- BITS-CRM");
                    primaryStage.setScene(new Scene(root, 1200, 500));
                    tHelper.createTrayIcon(primaryStage);
                    tHelper.createIcon(primaryStage);
                    primaryStage.show();
                }
            } else if (fromMain == false) {
                fHelper.WriteNetwork(new Network(ip, Integer.parseInt(port)));
            }
        } else
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Not able to connect to server!");
    }
}
