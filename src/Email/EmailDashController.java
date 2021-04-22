package Email;

import Email.EResponse.EResponseController;
import JCode.*;
import JCode.mysql.mySqlConn;
import JSockets.JClient;
import JSockets.JServer;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dashboard.dController;
import gui.NotesConstructor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.*;

import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailDashController implements Initializable {


    @FXML
    private AnchorPane anchor_body, anchor_details, anchor_remarks;
    @FXML
    private Label label_ticket, label_time, label_locked, label_created, title_created, label_from, title_locked, label_count, tittle_solveTime, label_solveTime;
    @FXML
    private TextArea txt_subject;
    @FXML
    private JFXButton btn_lock, btn_solv, btn_unlock, btn_resolve;
    @FXML
    private JFXComboBox<String> combo_respond;
    @FXML
    private VBox category_box, vbox_from, vbox_cc, vbox_filter;
    @FXML
    private JFXComboBox<FileDev> combo_attach;
    @FXML
    private JFXComboBox<Users> selectUser;
    @FXML
    private MenuBar menu_bar;
    @FXML
    private JFXTextField search_txt;
    @FXML
    private ListView<Email> list_emails;

    private mySqlConn sql;
    private FileHelper fHelper;
    private Users user;

    private static ImageView imgLoader = dController.img_load;
    public static String subject, body;

    public static Email selectedEmail = null;

    public static ListView<Email> list_emailsF;

    public static int Email_Type = 1;

    public int ticketNumberLatest,
            generalNumberLatest;
    private int ticketLastNumberSQL, generalLastNumberSQL;
    TabPane tabPane = new TabPane();

    public EmailDashController() {
    }

    public static String isAdmin;
    List<Users> usersList = new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imgLoader.setVisible(true);

        list_emailsF = list_emails;
        anchor_details.setVisible(false);
        sql = new mySqlConn();
        fHelper = new FileHelper();

        user = fHelper.ReadUserDetails();
        isAdmin = sql.getUserRight(user.getUCODE());

        //Setting icons notifier for unread emails
        ticketNumberLatest = fHelper.ReadLastEmailNumber(1);
        generalNumberLatest = fHelper.ReadLastEmailNumber(2);

        ticketLastNumberSQL = sql.getLatestEmailNo(1);
        generalLastNumberSQL = sql.getLatestEmailNo(2);

        if (ticketLastNumberSQL > ticketNumberLatest) {
            tickets.setText("Tickets*");
        } else {
            tickets.setText("Tickets");
        }

        if (generalLastNumberSQL > generalNumberLatest) {
            allMail.setText("General*");
        } else {
            allMail.setText("General");
        }

        fHelper.WriteLastEmailNumber(1, ticketLastNumberSQL);
        fHelper.WriteLastEmailNumber(2, generalLastNumberSQL);

        populateCategoryBoxes();
        populateMenuBar();
        populateFilters();

        tabPane.setMinWidth(100);
        tabPane.setMinHeight(100);

        list_emails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            selectedEmail = newValue;
            populateDetails(selectedEmail);
            populateRemarks();
        });
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

        //Attaching and Adding to the Respond Combo Boxes
        combo_respond.getItems().addAll("Respond", "Reply", "Forward");
        combo_respond.getSelectionModel().select(0);
        combo_respond.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Respond")) {
                return;
            }
            Email sEmail = selectedEmail;

            EResponseController.stTo = sEmail.getFromAddressCommaString();
            EResponseController.stCc = sEmail.getCcAddressCommaString();
            if (newValue.equals("Reply")) {
                EResponseController.stInstance = 'R';
                EResponseController.stSubject = "RE: " + sEmail.getSubject();
                EResponseController.stBody = "\n\n\n" + "On " + sEmail.getTimeFormatted() + ", " + sEmail.getFromAddress
                        ()[0].toString() + " wrote:\n" + sEmail.getBody();
            } else if (newValue.equals("Forward")) {
                if (sEmail.getAttch() == null || sEmail.getAttch().isEmpty()) {
                    EResponseController.stSubject = "FW: " + sEmail.getSubject();
                    EResponseController.stInstance = 'F';
                    EResponseController.stBody = sEmail.getBody();
                    EResponseController.stAttach = null;
                } else {
                    EResponseController.stSubject = "FW: " + sEmail.getSubject();
                    EResponseController.stInstance = 'F';
                    EResponseController.stBody = sEmail.getBody();
                    EResponseController.stAttach = sEmail.getAttch();
                }
            }
            inflateEResponse(1);
            combo_respond.getSelectionModel().select(0);
        });


        List<Users> usersList2 = sql.getAllUsers();
        for (Users c : usersList2) {
            usersList.add(c);
        }
        selectUser.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isAdmin.equals("Admin")) {
                if (newValue == null) {
                    return;
                } else {
                    if (sql.updateLockEmail(newValue, selectedEmail, 1)) {
                        loadEmailsStatic();
                        reloadInstances();
                    }
                }
            }
        });

    }

    public void populateRemarks() {
        tabPane.getTabs().clear();
        new NotesConstructor(tabPane, sql, selectedEmail).generalConstructor(5);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);
        if (!anchor_remarks.getChildren().contains(tabPane)) // if anchorPane not contains tabPane then add tabPane
            anchor_remarks.getChildren().add(tabPane);
    }

    private void changeEmailType(int type, JFXButton btn) {
        tickets.getStyleClass().remove("btnMenuBoxPressed");
        allMail.getStyleClass().remove("btnMenuBoxPressed");
        outbox.getStyleClass().remove("btnMenuBoxPressed");
        sentMail.getStyleClass().remove("btnMenuBoxPressed");

        btn.getStyleClass().add("btnMenuBoxPressed");

        if (type != Email_Type) {
            Email_Type = type;
            selectedEmail = null;
        }

        if (type != 1) {
            combo_respond.setDisable(false);
        }

        if (type == 1)
            vbox_filter.setVisible(true);
        else
            vbox_filter.setVisible(false);

        //To set styling to individual cell
        list_emails.setCellFactory(new Callback<ListView<Email>, ListCell<Email>>() {
            @Override
            public ListCell<Email> call(ListView<Email> param) {
                ListCell<Email> cell = new ListCell<Email>() {
                    @Override
                    protected void updateItem(Email item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            Platform.runLater(() -> setText(item.toString()));
//                          This is so that the new emails would be displayed. Otherwise the formatting doesnt apply
                            if (item.getEmailNo() == list_emails.getItems().get(list_emails.getItems().size() - 1).getEmailNo()) {
                                switch (Email_Type) {
                                    case 1:
                                        ticketNumberLatest = ticketLastNumberSQL;
                                        break;
                                    case 2:
                                        generalNumberLatest = generalLastNumberSQL;
                                        break;
                                }
                            }

                            boolean newEmail = false;

                            switch (Email_Type) {
                                case 1: {
                                    if (item.getEmailNo() > ticketNumberLatest) {
                                        if (!getStyleClass().contains("unreadEmail")) {
                                            getStyleClass().add("unreadEmail");
                                            newEmail = true;
                                            break;
                                        }
                                    } else {
                                        getStyleClass().remove("unreadEmail");
                                        break;
                                    }
                                }
                                case 2: {
                                    if (item.getEmailNo() > generalNumberLatest) {
                                        if (!getStyleClass().contains("unreadEmail")) {
                                            getStyleClass().add("unreadEmail");
                                            newEmail = true;
                                            break;
                                        }
                                    } else {
                                        getStyleClass().remove("unreadEmail");
                                        break;
                                    }
                                }
                            }

                            if (newEmail == false && Email_Type == 1) {
                                if (item.getLockd() == 0) {
                                    if (!getStyleClass().contains("unlockedEmail")) {
                                        getStyleClass().add("unlockedEmail");
                                    }
                                } else {
                                    getStyleClass().remove("unlockedEmail");
                                }
                            } else {    //If email type other than tickets is selected no styling should be shown
                                getStyleClass().remove("unlockedEmail");

                            }
                        } else {
                            Platform.runLater(() -> setText(""));
                            getStyleClass().remove("unlockedEmail");
                        }
                    }
                };
                return cell;
            }
        });


        //Right click menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem archiveItem = new MenuItem("Move to Archive");
        archiveItem.setOnAction(t -> {
            Email email = list_emails.getSelectionModel().getSelectedItem();
            if (email.getLockd() != '\0') {     //If Email is locked
                if (email.getLockd() == user.getUCODE()) {      //If Email is locked by YOU
                    sql.ArchiveEmail(Email_Type, " EMNO = " + email.getEmailNo());
                    loadEmails();
                } else {                        //If Email is locked but NOT by you
                    Toast.makeText((Stage) btn.getScene().getWindow(), "You are not allowed to archive this email.");
                }
            } else {                            //If Email is not locked
                sql.ArchiveEmail(Email_Type, " EMNO = " + email.getEmailNo());
                loadEmails();
            }
        });
        contextMenu.getItems().add(archiveItem);


        if (Email_Type == 2) {
            MenuItem createTicket = new MenuItem("Create Ticket");
            createTicket.setOnAction(t -> {
                Email selectedItem = list_emails.getSelectionModel().getSelectedItem();
                selectedItem.setManual(user.getUCODE());
                sql.insertEmailManual(selectedItem);
                sql.ArchiveEmail(Email_Type, " EMNO = " + selectedItem.getEmailNo());
                loadEmails();
            });
            contextMenu.getItems().add(createTicket);
        }

        if (Email_Type == 3) { //resend email
            MenuItem sendAgain = new MenuItem("Send Again");
            sendAgain.setOnAction(t -> {
                Email selectedItem = list_emails.getSelectionModel().getSelectedItem();
                emailControl.sendEmail(selectedItem, null);
                loadEmails();
            });
            contextMenu.getItems().add(sendAgain);
        }
        list_emails.setContextMenu(contextMenu);
        list_emails.setOnContextMenuRequested(event -> event.consume());

        //Display Emails
        loadEmails();
    }

    //help
    private List<Email> checkIfEmailsExist() {
        List<Email> emails = null;
        switch (Email_Type) {
            case 1:     //Tickets
                emails = sql.readAllEmails(Filters.readFromFile());
                break;
            case 2:     //General
                emails = sql.readAllEmailsGeneral(" WHERE FREZE = 0");
                break;
            case 3:     //Outbox
                emails = sql.readAllEmailsSent(" AND SENT = 0 ");
                break;
            case 4:     //Sent
                emails = sql.readAllEmailsSent(null);
                break;
        }

        if (emails == null) {
            emails = new ArrayList<>();
            Email nEm = new Email();
            nEm.setSubject("Emails Found");
            nEm.setEmailNo(0);
            nEm.setFromAddress(new Address[]{new InternetAddress()});
            nEm.setAttch("");
            emails.add(nEm);
            list_emails.setDisable(true);
            enableDisable(1);
        } else {
            list_emails.setDisable(false);
        }
        return emails;
    }

    private void inflateArchive() {
        inflateWindow("Archive", "Archive/archive.fxml");
    }

    private void inflateSearch() {
        inflateWindow("Search", "GlobalSearching/SearchEmail.fxml");
    }

    //OPENING RESPONSE STAGE
    private void inflateEResponse(int i) {
        EResponseController.choice = i;
        String title = "";
        switch (i) {
            case 1:
                title = "New Email";
                break;
            case 2:
                title = "New Ticket";
                break;
        }
        inflateWindow(title, "EResponse/EResponse.fxml");
    }

    //OPENING THE FILTER
    private void inflateFilters() {
        inflateWindow("Filters", "Search/filter.fxml");
    }

    private void inflateWindow(String title, String path) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Parent root1 = fxmlLoader.load();
            Stage stage2 = new Stage();
            stage2.setTitle(title);
            stage2.setScene(new Scene(root1));
            trayHelper tray = new trayHelper();
            tray.createIcon(stage2);
            Platform.setImplicitExit(true);
            stage2.sizeToScene();
            stage2.show();
        } catch (IOException e) {
            e.getLocalizedMessage();
//            e.printStackTrace();
        }
    }

    private void instantiateEmail() {
        EResponseController.stTo = "";
        EResponseController.stSubject = "";
        EResponseController.stBody = "";
        EResponseController.stInstance = 'N'; //N for New.
    }

    //Track Down which load Email is Running
    public void loadEmails() {
        Email temp = selectedEmail;
        list_emails.setItems(null);
        selectedEmail = temp;   //Because when list_emails is emptied selected email becomes null

        //making list filterable
        ObservableList<Email> dataObj = FXCollections.observableArrayList(checkIfEmailsExist());
        FilteredList<Email> filteredList = new FilteredList<>(dataObj, s -> true);

        search_txt.textProperty().addListener((observable, oldValue, newValue) -> setSearch(filteredList));

        setSearch(filteredList);

        list_emails.setItems(filteredList);

        if (selectedEmail == null) {
            enableDisable(1);
            imgLoader.setVisible(false);
            return;
        }

        //Reselect email from list
        int index = -1;
        boolean isFound = false;
        for (Email email : list_emails.getItems()) {
            index++;
            if (email.getEmailNo() == selectedEmail.getEmailNo()) {
                isFound = true;
                break;
            }
        }
        if (isFound) {
            list_emails.getSelectionModel().select(index);
            populateDetails(selectedEmail);
        } else {
            enableDisable(1);
        }

        //When Email is marked as solved
        if (this.solvIndex != -1) {
            list_emails.getSelectionModel().select(this.solvIndex);
            this.solvIndex = -1;
        }

        imgLoader.setVisible(false);
    }

    public static void loadEmailsStatic() {      //Load Emails from other controller
        imgLoader.setVisible(true);
        if (Email_Type == 1) {
            Platform.runLater(() -> tickets.fire());
        } else if (Email_Type == 2) {
            Platform.runLater(() -> allMail.fire());
        }
    }

    public void onLock(ActionEvent actionEvent) {

        imgLoader.setVisible(true);
        sql.lockEmail(selectedEmail, 1);
        loadEmailsStatic();
        reloadInstances();
    }


    public int solvIndex = -1;  //To select the next email in the list after solve
    private ESetting eSetting;

    public void onSolv(ActionEvent actionEvent) {
        solvIndex = -1;

        eSetting = sql.getEmailSettings();
        solvIndex = list_emails.getSelectionModel().getSelectedIndex();
        if (!eSetting.isSolv()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to mark this as solved?\n" +
                    "This action cannot be taken back. No response will be issued.",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                if (selectedEmail.getIsResolve() == selectedEmail.getLockd() && isAdmin.equals("Admin") && selectedEmail.getIsAllocatedBy() == user.getUCODE()) {
                    sql.solvEmail(selectedEmail, "S", selectedEmail.getLockd(), false, ""); // S for solved
                } else if (selectedEmail.getLockd() == user.getUCODE()) {
                    sql.solvEmail(selectedEmail, "S", user.getUCODE(), false, ""); // S for solved
                }

                loadEmailsStatic();
                reloadInstances();
            } else {
                return;
            }
        } else {
            inflateWindow("Confirmation", "SolvedDialog/SolvedDialog.fxml");
        }
    }

    public void onUnlock(ActionEvent actionEvent) {
        imgLoader.setVisible(true);

        Email email = selectedEmail;
//        if (isAdmin.equals("Admin") && email.getIsAllocatedTo() == 1) {
        if (email.getIsAllocatedBy() == user.getUCODE()) {
            sql.updateLockEmail(null, email, 0);
        } else if (user.getUCODE() == email.getLockd()) {
            sql.lockEmail(email, 0);
        }

        loadEmailsStatic();
        reloadInstances();
    }

    static JFXButton allMail = new JFXButton("General"),
            tickets = new JFXButton("Tickets"),
            outbox = new JFXButton("Outbox"),
            sentMail = new JFXButton("Sent");

    private void populateCategoryBoxes() {
        prepBtn(tickets);
        tickets.setOnAction(event -> changeEmailType(1, tickets));

        prepBtn(allMail);
        allMail.setOnAction(event -> changeEmailType(2, allMail));

        prepBtn(outbox);
        outbox.setOnAction(event -> changeEmailType(3, outbox));

        prepBtn(sentMail);
        sentMail.setOnAction(event -> changeEmailType(4, sentMail));

        category_box.getChildren().addAll(tickets, allMail, outbox, sentMail);

        switch (Email_Type) {
            case 1:
                tickets.fire();
                break;
            case 2:
                allMail.fire();
                break;
            case 3:
                sentMail.fire();
                break;
            case 4:
                outbox.fire();
                break;
        }
    }

    Address[] from, cc;

    public void populateDetails(Email email) {
        imgLoader.setVisible(true);
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
            if (Email_Type == 1) {
                selectUser.getItems().clear();
                if (email.getIsAllocatedBy() == 0) {
                    selectUser.setPromptText("Lock");
                } else {
                    selectUser.setPromptText("Allocated");
                }
                selectUser.setDisable(false);

                selectUser.getItems().addAll(usersList);

                from = email.getFromAddress();
                title_locked.setText("Locked By: ");
                if (email.getIsAllocatedBy() == 0) {
                    label_locked.setText(email.getLockedByName());
                } else {
                    label_locked.setText(email.getLockedByName() + " ( " + email.getIsAllocatedByName() + " )");
                }


                if (email.getManual() != '\0') {
                    label_created.setVisible(true);
                    title_created.setVisible(true);
                    label_created.setText(email.getCreatedBy());
                } else {
                    label_created.setVisible(false);
                    title_created.setVisible(false);
                }

            } else if (Email_Type == 2) {
                tittle_solveTime.setText("");
                label_solveTime.setText("");
                from = email.getFromAddress();
            } else if (Email_Type == 4 || Email_Type == 3) {
                tittle_solveTime.setText("");
                label_solveTime.setText("");
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

            if (isAdmin.equals("Admin")) {
                selectUser.setVisible(true);
            }

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

            if (Email_Type != 1) {  //If Email Type is General or Sent
                btn_lock.setVisible(false);
                btn_unlock.setVisible(false);
                btn_solv.setVisible(false);
                imgLoader.setVisible(false);
                return;
            } else {
                btn_lock.setVisible(true);
                btn_unlock.setVisible(true);
                btn_solv.setVisible(true);

            }

            //Locked/Solved Label
            //Buttons
            if (email.getSolvFlag() == 'S') {    //If Email is solved disable all buttons
                String display = email.getLockedByName();
                if (email.getLockTime() != null)
                    display = display + " ( " + CommonTasks.getDateDiff(email.getLockTime(), email.getSolveTime(), TimeUnit.MINUTES) + " ) ";
                label_locked.setText(display);
                label_solveTime.setText(email.getSolveTime());
                enableDisable(2);
            } else if (email.getIsAllocatedBy() != 0) {
                if (email.getIsResolve() == 0 && isAdmin.equals("Admin") && email.getIsAllocatedBy() == user.getUCODE()) { //email is allocated but not resolve it can be unlocked by same admin
                    label_solveTime.setText(email.getAllocateTime());
                    enableDisable(10);
                } else if (email.getIsResolve() != 0 && isAdmin.equals("Admin") && email.getIsAllocatedBy() == user.getUCODE()) {  //  email is allocated and resolved admin can solve
                    label_solveTime.setText(email.getResolveTime());
                    enableDisable(8);
                } else if (email.getIsResolve() != 0) { // email is allocated and resolve by user resolved disable and other button is invisible
                    label_solveTime.setText(email.getResolveTime());
                    enableDisable(7);
                } else if (email.getLockd() == user.getUCODE()) {  // email is allocated and user is same and the resolve button is visible
                    label_solveTime.setText(email.getAllocateTime());
                    enableDisable(6);
                } else if (email.getLockd() != user.getUCODE()) {  // email is allocated and user is same and the resolve button is visible
                    tittle_solveTime.setText("Allocate Time: ");
                    label_solveTime.setText(email.getAllocateTime());
                    enableDisable(4);
                } else {
                    tittle_solveTime.setText("Locked Time: ");
                    enableDisable(4); // email is allocated
                }
            } else {    //If Email is not solved
                if (email.getLockd() != '\0') {     //If Email is locked
                    if (email.getLockd() == user.getUCODE()) {      //If Email is locked by YOU
                        label_solveTime.setText(email.getLockTime());
                        enableDisable(3);
                    } else {
                        tittle_solveTime.setText("Locked Time: ");
                        label_solveTime.setText(email.getLockTime());
                        enableDisable(4);
                    }
                } else {//If Email is not locked
                    if (isAdmin.equals("Admin")) { // email is locked by admin
                        enableDisable(9);
                    } else {
                        enableDisable(5); // email is locked by user
                    }
                }
            }

            imgLoader.setVisible(false);
        })).start();
    }

    private static WritableImage convertToJavaFXImage(byte[] raw, final double width, final double height) {
        WritableImage image = new WritableImage(Integer.parseInt(String.valueOf(width)), Integer.parseInt(String.valueOf(height)));
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(raw);
            BufferedImage read = ImageIO.read(bis);
            image = SwingFXUtils.toFXImage(read, null);
        } catch (IOException ex) {
            Logger.getLogger(EmailDashController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    private JFXComboBox sortBy, ascDesc;
    private JFXCheckBox solved, unSolved, locked, unLocked, lockedByMe, hideReminders, archived, allocatedEmail;

    private int filterChoice = 0; //1 == tickets 2 == general

    private void populateFilters() {
        vbox_filter.setSpacing(10);

        Filters filter = Filters.readFromFile();

        //Sort By (ComboBox)
        HBox sort = new HBox();
        sortBy = new JFXComboBox();
        setUpCombo(sortBy, "Sorted By", new String[]{"Tickets", "From", "Subject"});
        sortBy.setOnAction(event -> saveFilters());
        sortBy.getSelectionModel().select(filter.getSortBy());

        ascDesc = new JFXComboBox();
        setUpCombo(ascDesc, "Asc/Desc", new String[]{"Asc", "Desc"});
        ascDesc.setOnAction(event -> saveFilters());
        ascDesc.getSelectionModel().select(filter.getAscDesc());
        sort.getChildren().addAll(sortBy, ascDesc);
        vbox_filter.getChildren().add(sort);

        //Checkboxes
        solved = new JFXCheckBox("Solved");
        solved.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        setUpCheck(solved);
        solved.setSelected(filter.isSolved());

        unSolved = new JFXCheckBox("Unsolved");
        unSolved.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        setUpCheck(unSolved);
        unSolved.setSelected(filter.isUnsolved());

        locked = new JFXCheckBox("Locked");
        locked.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        setUpCheck(locked);
        locked.setSelected(filter.isLocked());

        unLocked = new JFXCheckBox("Unlocked");
        setUpCheck(unLocked);
        unLocked.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        unLocked.setSelected(filter.isUnlocked());

        lockedByMe = new JFXCheckBox("Locked By Me");
        setUpCheck(lockedByMe);
        lockedByMe.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        lockedByMe.setSelected(filter.isLockedByMe());

        hideReminders = new JFXCheckBox("Hide Reminders");
        setUpCheck(hideReminders);
        hideReminders.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        hideReminders.setSelected(filter.isHideReminders());

        archived = new JFXCheckBox("Archived");
        setUpCheck(archived);
        archived.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        archived.setSelected(filter.isArchived());

        allocatedEmail = new JFXCheckBox("Pending Emails");
        setUpCheck(allocatedEmail);
        allocatedEmail.selectedProperty().addListener((observable, oldValue, newValue) -> saveFilters());
        allocatedEmail.setSelected(filter.isAllocate());


    }

    private void populateMenuBar() {
        Menu newMenu = new Menu("New");
        MenuItem newEmail = new MenuItem("New Email");
        newEmail.setOnAction(event -> {
            instantiateEmail();
            inflateEResponse(1);
        });
        newMenu.getItems().add(newEmail);
        MenuItem newTicket = new MenuItem("New Ticket");
        newTicket.setOnAction(event -> {
            instantiateEmail();
            inflateEResponse(2);
        });
        newMenu.getItems().add(newTicket);
        menu_bar.getMenus().add(newMenu);

        Menu edit = new Menu("Edit");
        MenuItem reload = new MenuItem("Refresh");
        reload.setOnAction(event -> loadEmails());
        edit.getItems().add(reload);

        MenuItem filter = new MenuItem("Filters");
        filter.setOnAction(event -> inflateFilters());
        edit.getItems().add(filter);

        MenuItem archive = new MenuItem("Move to Archive");
        archive.setOnAction(event -> inflateArchive());
        edit.getItems().add(archive);

        MenuItem search = new MenuItem("Email Search");
        search.setOnAction(event -> inflateSearch());
        edit.getItems().add(search);

        menu_bar.getMenus().add(edit);
    }

    private void prepBtn(JFXButton btn) {
        btn.setMinHeight(50);
        btn.setMinWidth(60);
        btn.getStyleClass().add("btnMenuBox");
        btn.setAlignment(Pos.CENTER_LEFT);
    }

    public static void reloadInstances() {
        // comment this line
        new Thread(() -> {
            if (dController.isServer == true) {
                JServer.broadcastMessages("R");
            } else
                JClient.sendMessage("R");   //Function was made so that if ever this feature is not needed i can just
        }).start();
    }

    private void saveFilters() {
//        imgLoader.setVisible(true);
        try {
            Filters filter = new Filters();
            filter.setSortBy(sortBy.getSelectionModel().getSelectedItem().toString());
            filter.setAscDesc(ascDesc.getSelectionModel().getSelectedItem().toString());
            filter.setSolved(solved.isSelected());
            filter.setUnsolved(unSolved.isSelected());
            filter.setLocked(locked.isSelected());
            filter.setUnlocked(unLocked.isSelected());
            filter.setLockedByMe(lockedByMe.isSelected());
            filter.setHideReminders(hideReminders.isSelected());
            filter.setArchived(archived.isSelected());

            filter.setAllocate(allocatedEmail.isSelected());


            filter.writeToFile();

            loadEmailsStatic();
        } catch (NullPointerException e) {
            System.out.println("Save Filters x--x : Line 959 ");
        }
    }

    private void setSearch(FilteredList<Email> filteredList) {
        enableDisable(1);
        String filter = search_txt.getText();
        if (filter == null || filter.length() == 0) {
            filteredList.setPredicate(s -> true);
        } else {
            filteredList.setPredicate(s -> s.toString().toUpperCase().contains(filter.toUpperCase()));
        }
        label_count.setText("Displaying " + filteredList.size() + " Emails");
    }

    private void setUpCheck(JFXCheckBox combo) {
        combo.getStyleClass().add("check_box_style");
        VBox.setMargin(combo, new Insets(5, 5, 0, 10));
        vbox_filter.getChildren().add(combo);
    }

    private void setUpCombo(JFXComboBox combo, String prompt, String[] options) {
        combo.setPromptText(prompt);
        combo.setMinWidth((vbox_filter.getPrefWidth() * 0.4) - 5);
        combo.getItems().addAll(options);
        combo.getStyleClass().add("check_box_style");
        HBox.setMargin(combo, new Insets(0, 5, 0, 5));
    }

    public void enableDisable(int i) {

        if (i == 1) {   //Disable Everything
            anchor_body.setVisible(false);
            anchor_details.setVisible(false);

            imgLoader.setVisible(false);
        } else if (i == 2) {    //If email is solved
            title_locked.setText("Solved By: ");
            tittle_solveTime.setText("Solved Time: ");
            btn_lock.setDisable(true);
            btn_lock.setVisible(true);
            btn_unlock.setDisable(true);
            btn_unlock.setVisible(false);
            btn_solv.setDisable(true);
            btn_resolve.setVisible(false);
            selectUser.setVisible(false);
            combo_respond.setDisable(true);
        } else if (i == 3) {    //If email is locked by you
            tittle_solveTime.setText("Locked Time: ");
            btn_lock.setVisible(false);
            btn_unlock.setVisible(true);
            btn_unlock.setDisable(false);
            btn_solv.setDisable(false);     //Only someone who has locked the email can solve it.
            btn_resolve.setVisible(false); // new
            selectUser.setDisable(true);
            combo_respond.setDisable(false);
        } else if (i == 4) {    //If email is locked but not by you

            btn_unlock.setVisible(false);
            btn_lock.setVisible(true);
            btn_lock.setDisable(true);
            btn_solv.setDisable(true);
            btn_resolve.setVisible(false);
            selectUser.setVisible(false); // new
            combo_respond.setDisable(true);
        } else if (i == 5) {    //If email is not locked
            label_solveTime.setText("");
            tittle_solveTime.setText("");
            btn_lock.setDisable(false);
            btn_lock.setVisible(true);
            btn_unlock.setVisible(false);
            btn_solv.setDisable(true);
            btn_resolve.setVisible(false); // new
            combo_respond.setDisable(true);
        } else if (i == 6) {    //Resolve button display for same user
            tittle_solveTime.setText("Allocate Time: ");
            btn_lock.setDisable(false);
            btn_lock.setVisible(false);
            btn_unlock.setVisible(false);
            btn_solv.setVisible(false);
            btn_solv.setDisable(true);
            selectUser.setVisible(false);
            btn_resolve.setVisible(true);
            btn_resolve.setDisable(false);
            combo_respond.setDisable(true);
        } else if (i == 7) {    // email is resolved the user end expect admin the resolved button is disable
            tittle_solveTime.setText("Resolved Time: ");
            btn_lock.setDisable(false);
            btn_lock.setVisible(false);
            btn_unlock.setVisible(false);
            btn_solv.setDisable(false);
            btn_solv.setVisible(false);
            selectUser.setVisible(false);
            btn_resolve.setVisible(true);
            btn_resolve.setDisable(true);
            combo_respond.setDisable(true);
        } else if (i == 8) {    // email solved button enable for admin
            tittle_solveTime.setText("Resolved Time: ");
            btn_lock.setDisable(false);
            btn_lock.setVisible(false);
            btn_unlock.setVisible(false);
            btn_solv.setDisable(false);
            btn_solv.setVisible(true);
            selectUser.setVisible(false);
            btn_resolve.setVisible(false);
            btn_resolve.setDisable(true);
            combo_respond.setDisable(true);
        } else if (i == 9) { // email is not locked and display for admin select user and lock button
            label_solveTime.setText("");
            tittle_solveTime.setText("");
            btn_lock.setDisable(false);
            btn_lock.setVisible(true);
            btn_unlock.setVisible(false);
            btn_solv.setDisable(true);
            selectUser.setDisable(false);
            btn_resolve.setVisible(false); // new
            combo_respond.setDisable(true);
        } else if (i == 10) { //if email is locked and it can be unlocked by same admin
            tittle_solveTime.setText("Allocate Time: ");
            btn_lock.setVisible(false);
            btn_unlock.setVisible(true);
            btn_unlock.setDisable(false);
            btn_solv.setVisible(true);
            btn_solv.setDisable(true);     //
            btn_resolve.setVisible(false); // new
            combo_respond.setDisable(false);
        }
    }


    public void onResolve(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to mark this as Resolve?\n" +
                "This action cannot be taken back.",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            sql.updateEmailForResolve(selectedEmail);
            loadEmailsStatic();
            reloadInstances();
        } else {
            return;
        }

    }
}
