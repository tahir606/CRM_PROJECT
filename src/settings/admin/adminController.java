package settings.admin;

import JCode.Toast;
import JCode.FileHelper;
import JCode.mysql.mySqlConn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dashboard.dController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.Users;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class adminController implements Initializable {

    @FXML
    private JFXTextField txt_uname, txt_fname, txt_email, txt_password;
    @FXML
    private Label label_message;
    @FXML
    private VBox vbox_rights;
    @FXML
    private JFXCheckBox check_freeze;
    @FXML
    private JFXCheckBox check_email;
    @FXML
    private JFXComboBox<Users> combo_users;
    @FXML
    private JFXButton btn_save, btn_archive, btn_log_out_user;

    private mySqlConn sql;
    private FileHelper fileHelper;
    private ImageView imm_load = dController.img_load;

    private List<Users> usersList = null;
    private List<Users.uRights> rightsList = null;

    private Users userSel;

    private int nUcode = 0;
    private int UcodeisEmail = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sql = new mySqlConn();

        init();

        vbox_rights.setSpacing(10);
        vbox_rights.setPadding(new Insets(5, 10, 5, 2));

//        btn_archive.setOnAction(event -> {
//            if (userSel != null) {
//                sql.archiveUser(userSel);
//                init();
//            }
//        });

        btn_log_out_user.setVisible(false);
        btn_log_out_user.setOnAction(event -> {
            if (fileHelper == null)
                fileHelper = new FileHelper();

            if (userSel.getUCODE() == fileHelper.ReadUserDetails().getUCODE()) {
                Toast.makeText((Stage) btn_save.getScene().getWindow(), "You can't log yourself out.");
                return;
            }

            sql.logUserOut(userSel);
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "User will be logged out the next time he/she opens KiT");
            populateDetails(userSel);

        });
    }

    private void init() {
        combo_users.getItems().clear();

        usersList = sql.getAllUsers();
        rightsList = sql.getAllUserRights();

        nUcode = usersList.get(usersList.size() - 1).getUCODE() + 1;    //Get Ucode for new User
        UcodeisEmail = getIsEmail((ArrayList<Users>) usersList);        //Check if this user is receiving email

        Users u = new Users();
        u.setUCODE(nUcode);
        u.setFNAME(" + Create New");
        u.setUright("New");
        combo_users.getItems().add(u);

        combo_users.getItems().addAll(usersList);
        comboListener();
        combo_users.getSelectionModel().select(0);
    }

    private void comboListener() {
        combo_users.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateDetails(newValue);
            userSel = newValue;
        });
    }

    private void populateDetails(Users newValue) {

        boolean isLoggedIn = sql.checkIfUserIsLoggedIn(newValue);
        if (isLoggedIn) {
            label_message.setVisible(true);
            btn_log_out_user.setVisible(true);
        } else {
            label_message.setVisible(false);
            btn_log_out_user.setVisible(false);
        }

        vbox_rights.getChildren().clear();

        JFXCheckBox f = new JFXCheckBox("Award All Rights");

        if (newValue == null)
            return;

        if (newValue.getUright().equals("Admin")) {
            f.setSelected(true);
        }

        vbox_rights.getChildren().add(f);

        for (Users.uRights ur : rightsList) {
            JFXCheckBox cb = new JFXCheckBox(ur.getRNAME());
            if (newValue.getUright().equals("Admin")) {
                cb.setSelected(false);
            } else {
                for (Users.uRights u : newValue.getRights()) {
                    if (u.getRCODE() == ur.getRCODE()) {
                        cb.setSelected(true);
                    }
                }
            }
            vbox_rights.getChildren().add(cb);
        }

        txt_uname.setText(newValue.getUNAME());
        txt_fname.setText(newValue.getFNAME());
        if (newValue.getFNAME().equals(" + Create New")) {
            txt_fname.setText("");
        }
        txt_email.setText(newValue.getEmail());
        txt_password.setText(newValue.getPassword());

        if (UcodeisEmail == 0) {
            check_email.setDisable(false);
        } else {
            if (UcodeisEmail != newValue.getUCODE()) {
                check_email.setDisable(true);
            } else {
                check_email.setDisable(false);
            }
        }

        check_email.setSelected(newValue.isEmail());
        check_freeze.setSelected(newValue.isFreeze());

//        check_email.selectedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if(newValue == true) {
//
//                } else {
//                    nUcode = 0;
//                }
//            }
//        });
    }

    private int getIsEmail(ArrayList<Users> users) {
        for (Users u : users) {
            if (u.isEmail() == true) {
                return u.getUCODE();
            }
        }
        return 0;
    }


    @FXML
    void saveChanges(ActionEvent event) {

        System.out.println("Updating user: " + userSel);

        String uname, fname, email, password;
        boolean isEmail, isFreeze;

        uname = txt_uname.getText();
        fname = txt_fname.getText();
        email = txt_email.getText();
        password = txt_password.getText();
        isEmail = check_email.isSelected();
        isFreeze = check_freeze.isSelected();


        if (uname.equals("") || fname.equals("") || email.equals("") || password.equals("")) {
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Required fields are empty");
            return;
        }

        ArrayList<Users.uRights> rights = new ArrayList<>();

        int index = -1;
        for (Node node : vbox_rights.getChildren()) {
            JFXCheckBox jf = (JFXCheckBox) node;
            index++;
            if (index == 0) {
                if (jf.isSelected()) {
                    userSel.setUright("Admin");
                    break;
                } else {
                    userSel.setUright("Not Admin");
                }
                continue;
            }
            if (jf.isSelected())
                rights.add(new Users.uRights(index, ""));
        }

        if (rights.size() == 0 && !userSel.getUright().equals("Admin")) {
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "You need to allot at least one right.");
            return;
        }

        userSel.setUNAME(uname);
        userSel.setFNAME(fname);
        userSel.setEmail(email);
        userSel.setPassword(password);
        userSel.setEmailBool(isEmail);
        userSel.setFreeze(isFreeze);
        userSel.setuRightsList(rights);

        Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to save changes?",
                ButtonType.YES, ButtonType.NO);
        alert2.showAndWait();

        if (alert2.getResult() == ButtonType.YES) {
            if (userSel.getUCODE() == nUcode)
                sql.insertUpdateUser(userSel, 0);   //New
            else
                sql.insertUpdateUser(userSel, 1);   //Old

            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Your changes will be made after you re-login");

            init();
        } else {
            return;
        }

    }

    public void deleteUser(ActionEvent actionEvent) {

        System.out.println(userSel.getUCODE());
        System.out.println(nUcode);
        if (userSel.getUCODE() == nUcode) {
            return;
        }

        Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete User? Freezing it " +
                "may be a better choice.",
                ButtonType.YES, ButtonType.NO);
        alert2.showAndWait();

        if (alert2.getResult() == ButtonType.YES) {
            sql.deleteUser(userSel);

            init();
        } else {
            return;
        }

    }

}
