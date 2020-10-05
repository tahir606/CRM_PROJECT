package login;

import JCode.mysql.mySqlConn;
import JCode.trayHelper;
import SplashScreen.SplashScreenThread;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private JFXTextField user_field;
    @FXML
    private JFXPasswordField pass_field;
    @FXML
    private JFXButton login_btn;
    @FXML
    private Label error_message;
    @FXML
    private ImageView img_loader;

    trayHelper tHelper = new trayHelper();
    mySqlConn sql;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sql = new mySqlConn();

        //First time opening the CRM creates automatic admin user.
        sql.checkAndCreateUser();
        //And populate the rights list
        sql.checkAndPopulateRights();
        //And populate sources
        sql.checkAndPopulateSourcesOnCreation();
        //And populate Notification Settings
        sql.checkOnNotificationSettings();

        SplashScreenThread.hideSplashScreen();

        error_message.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(error_message, 0.0);
        AnchorPane.setRightAnchor(error_message, 0.0);
        error_message.setAlignment(Pos.CENTER);     //Setting Error Message Center

        img_loader.setImage(
                new Image(getClass().getResourceAsStream("/res/img/loader.gif")));     //Setting Loading Image to ImageView
        img_loader.setVisible(false);
//        Platform.setImplicitExit(false);

        pass_field.setOnAction(event -> {
            String users = user_field.getText(), pass = pass_field.getText();

            if (users.equals("") || pass.equals("")) {
                error_message.setText("Incomplete Values");
            } else {
                new Thread(() -> {
                    img_loader.setVisible(true);
                    authenticateLogin(user_field.getText(), pass_field.getText());
                }).start();
            }
        });
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        loginAction();
    }

    private void loginAction() {
        String users = user_field.getText(), pass = pass_field.getText();

        if (users.equals("") || pass.equals("")) {
            error_message.setText("Incomplete Values");
        } else {
            new Thread(() -> {
                img_loader.setVisible(true);
                authenticateLogin(user_field.getText(), pass_field.getText());
            }).start();
        }
    }

    private void authenticateLogin(String username, String password) {

        boolean succ = sql.authenticateLogin(username, password);

        Platform.runLater(
                () -> {
                    // Update UI here. Running on main Thread
                    if (succ == false) {     //Unsuccessful Login
                        error_message.setText("Incorrect ID or Password");
                        img_loader.setVisible(false);
                    } else {                //Successful Login
                        img_loader.setVisible(false);
                        Stage stage2 = (Stage) login_btn.getScene().getWindow();
                        stage2.close();

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource
                                ("dashboard/dashboard.fxml"));
                        Parent root1 = null;
                        try {
                            root1 = fxmlLoader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Stage stage = new Stage();
                        stage.setTitle("Burhani It Solutions - Customer Relationship Management");
                        stage.setScene(new Scene(root1, 1400, 600));
                        tHelper.createTrayIcon(stage);
                        tHelper.createIcon(stage);
                        stage.show();
                    }

                }
        );


    }

}
