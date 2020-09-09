package Email.GlobalSearching;

import Email.EmailDashController;
import JCode.CommonTasks;
import JCode.FileDev;
import JCode.mysql.mySqlConn;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import objects.Email;
import objects.Users;

import javax.mail.Address;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class EmailSearch implements Initializable {
    @FXML
    private JFXTextField search_email;
    @FXML
    private AnchorPane anchor_body, anchor_details,anchor_fix;
    @FXML
    private Label label_ticket, label_time, label_locked, label_created, title_created, title_locked, label_from, label_error;
    @FXML
    private TextArea txt_subject;
    @FXML
    private VBox vbox_from, vbox_cc;
    @FXML
    private JFXComboBox<FileDev> combo_attach;
    private Users user;
    private mySqlConn sql;
    EmailDashController emailDashController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sql = new mySqlConn();
        searchEmail();
        //Attaching listener to attaching combo box
        combo_attach.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(newValue);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        anchor_fix.setMinWidth(800);
        anchor_fix.setMinHeight(500);
        anchor_fix.setMinSize(800,500);

    }
    // This method search email which is search by user
    public void searchEmail() {
        //Search OnClick
        search_email.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                label_error.setVisible(true);
                String search = null;
                search=search_email.getText();
                if(search.isEmpty() ){
                    anchor_details.setVisible(false);
                    anchor_body.setVisible(false);
                    label_error.setText("Enter Ticket No");
                    return;
                }
                try {

                    int ticketNo = Integer.parseInt(search);
                    System.out.println("ticket no "+ticketNo);
                    Email emails = sql.readSearchEmail(ticketNo);//Search Email through Query
                    if (emails == null) {
                        anchor_details.setVisible(false);
                        anchor_body.setVisible(false);
                        label_error.setText("Ticket Not Found");
                    }else {
                        label_error.setVisible(false);
                        populateDetails(emails);
                    }
                } catch (NumberFormatException ex) {
                    anchor_details.setVisible(false);
                    anchor_body.setVisible(false);
                    label_error.setText("Invalid Ticket No");
                }

            }
        });
    }

    Address[] from, cc;
// This method display the details of search
    public void populateDetails(Email email) {
        new Thread(() -> Platform.runLater(() -> {
            try {
                label_ticket.setText(String.valueOf(email.getEmailNo()));
            } catch (NullPointerException e) {
                return;
            }

            label_time.setText(email.getTimeFormatted());

            vbox_from.getChildren().clear();  //Clearing
            vbox_from.setSpacing(2.0);
            vbox_cc.getChildren().clear();    //Both VBoxes
            vbox_cc.setSpacing(2.0);

            label_from.setText("From:");
            if (emailDashController.Email_Type == 1) {
                from = email.getFromAddress();
                title_locked.setText("Locked By: ");
                label_locked.setText(email.getLockedByName());

                if (email.getManual() != '\0') {
                    label_created.setVisible(true);
                    title_created.setVisible(true);
                    label_created.setText(email.getCreatedBy());
                } else {
                    label_created.setVisible(false);
                    title_created.setVisible(false);
                }

            } else if (emailDashController.Email_Type == 2) {
                from = email.getFromAddress();
            } else if (emailDashController.Email_Type == 4) {
                label_from.setText("To:");
                from = email.getToAddress();
                title_locked.setText("Sent By User: ");
                label_locked.setText(email.getUser());
            }
            cc = email.getCcAddress();
            if (from != null) {
                for (Address f : from) {
                    try {
                        Label label = new Label(f.toString());
                        label.setPadding(new Insets(2, 2, 2, 5));
                        label.getStyleClass().add("moduleDetails");
                        vbox_from.getChildren().add(label);
                    } catch (NullPointerException ex) {
                        //Because null is saved
                    }
                }
            }
            if (cc != null) {
                for (Address c : cc) {
                    try {
                        Label label = new Label(c.toString());
                        label.setPadding(new Insets(2, 5, 2, 5));
                        label.getStyleClass().add("moduleDetails");
                        vbox_cc.getChildren().add(label);
                    } catch (NullPointerException ex) {
                        //Because null is saved
                    }
                }
            }

            txt_subject.setText(email.getSubject());

            anchor_details.setVisible(true);
            //----Attachments
            combo_attach.getItems().clear();

            if (email.getAttch() == null || email.getAttch().equals("")) {
                combo_attach.setPromptText("No Attachments");
                combo_attach.setDisable(true);
            } else if (!email.getAttch().equals("")) {
                combo_attach.setPromptText("Open Attachment");
                combo_attach.setDisable(false);
                List<FileDev> attFiles = new ArrayList<>();
                for (String c : email.getAttch().split("\\^")) {
                    FileDev file = new FileDev(c);
                    attFiles.add(file);
                }
                combo_attach.getItems().addAll(attFiles);
            }
            //----Ebody
            anchor_body.getChildren().clear();

//            TextArea eBody = new TextArea(email.getBody());
            WebView eBody = new WebView();

            eBody.getEngine().loadContent(email.getBody());

//            System.out.println("NOT SETTING UP FONT");
            eBody.getEngine().setUserStyleSheetLocation("data:,body { font: 15px Calibri; }");
//            eBody.setWrapText(true);
            eBody.setPrefSize(anchor_body.getWidth(), anchor_body.getHeight());
            anchor_body.getChildren().add(eBody);
            AnchorPane.setLeftAnchor(eBody, 0.0);
            AnchorPane.setRightAnchor(eBody, 0.0);
            AnchorPane.setTopAnchor(eBody, 0.0);
            AnchorPane.setBottomAnchor(eBody, 0.0);
            //            eBody.setEditable(false);
            if (!anchor_body.isVisible()) {
                anchor_body.setVisible(true);
            }
            if (email.getSolvFlag() == 'S') {    //If Email is solved disable all buttons
                String display = email.getLockedByName();
                if (email.getLockTime() != null)
                    display = display + " ( " + CommonTasks.getDateDiff(email.getLockTime(), email.getSolveTime(), TimeUnit.MINUTES) + " ) ";
                label_locked.setText(display);
                title_locked.setText("Solved By: ");

            }
//            eBody.setEditable(false);
            if (!anchor_body.isVisible()) {
                anchor_body.setVisible(true);
            }
        })).start();
    }

}
