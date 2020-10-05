package Email.SentMail;

import JCode.FileDev;
import JCode.mysql.mySqlConn;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import objects.Email;

import javax.mail.Address;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class sentController implements Initializable {

    @FXML
    private AnchorPane anchor_details;
    @FXML
    private VBox vbox_to;
    @FXML
    private VBox vbox_cc;
    @FXML
    private VBox vbox_bcc;
    @FXML
    private Label label_time;
    @FXML
    private TextArea txt_subject;
    @FXML
    private Label label_cc1;
    @FXML
    private AnchorPane anchor_body;
    @FXML
    private ListView<Email> list_emails;
    @FXML
    private JFXComboBox combo_attach;

    mySqlConn sql;
//    List<Email> sentMails;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sql = new mySqlConn();

        list_emails.getItems().clear();
        list_emails.getItems().addAll(sql.readAllEmailsSent(null));

        anchor_body.setVisible(false);
        anchor_details.setVisible(false);

        //Populating List
        //Creates the changes in the Details Section
        list_emails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            selectedEmail = newValue;
            populateDetails(newValue);
        });

    }

    Address[] to, cc, bcc;

    private void populateDetails(Email email) {
        new Thread(() -> Platform.runLater(() -> {

            label_time.setText(email.getTimeFormatted());

            //----Emails
            vbox_to.getChildren().clear();  //Clearing
            vbox_to.setSpacing(2.0);
            vbox_cc.getChildren().clear();    //Both VBoxes
            vbox_cc.setSpacing(2.0);
            vbox_bcc.getChildren().clear();    //Both VBoxes
            vbox_bcc.setSpacing(2.0);

            to = email.getToAddress();
            cc = email.getCcAddress();
            bcc = email.getBccAddress();
            for (Address f : to) {
                try {
                    Label label = new Label(f.toString());
                    label.setPadding(new Insets(2, 2, 2, 5));
                    vbox_to.getChildren().add(label);
                } catch (NullPointerException ex) {
                    //Because null is saved
                }
            }
            if (cc != null) {
                for (Address c : cc) {
                    try {
                        Label label = new Label(c.toString());
                        label.setPadding(new Insets(2, 5, 2, 5));
                        vbox_cc.getChildren().add(label);
                    } catch (NullPointerException ex) {
                        //Because null is saved
                    }
                }
            }
            if (bcc != null) {
                for (Address c : bcc) {
                    try {
                        Label label = new Label(c.toString());
                        label.setPadding(new Insets(2, 5, 2, 5));
                        vbox_bcc.getChildren().add(label);
                    } catch (NullPointerException ex) {
                        //Because null is saved
                    }
                }
            }

            txt_subject.setText(email.getSubject());
            anchor_details.setVisible(true);

            //----Attachments
            combo_attach.getItems().clear();

            if (email.getAttch() != null) {
                combo_attach.setDisable(false);
                combo_attach.setPromptText("Open Attachment");
                List<FileDev> attFiles = new ArrayList<>();
                for (String c : email.getAttch().split("\\^")) {
                    FileDev file = new FileDev(c);
                    attFiles.add(file);
                }
                combo_attach.getItems().addAll(attFiles);

            } else {
                combo_attach.setPromptText("No Attachments");
                combo_attach.setDisable(true);
            }

            //----Ebody
            anchor_body.getChildren().clear();
            TextArea eBody = new TextArea(email.getBody());
            eBody.setWrapText(true);
            eBody.setPrefSize(anchor_body.getWidth(), anchor_body.getHeight());
            anchor_body.getChildren().add(eBody);
            AnchorPane.setLeftAnchor(eBody, 0.0);
            AnchorPane.setRightAnchor(eBody, 0.0);
            AnchorPane.setTopAnchor(eBody, 0.0);
            AnchorPane.setBottomAnchor(eBody, 0.0);
            eBody.setEditable(false);
            if (!anchor_body.isVisible()) {
                anchor_body.setVisible(true);
            }

        })).start();
    }



}
