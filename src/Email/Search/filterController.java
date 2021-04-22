package Email.Search;

import Email.EmailDashController;
import JCode.Toast;
import JCode.FileHelper;
import com.jfoenix.controls.*;
import dashboard.dController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.Users;

import java.net.URL;
import java.util.ResourceBundle;

public class filterController implements Initializable {

    @FXML
    private VBox filters_vbox;
    @FXML
    private JFXButton btn_filters;
    @FXML
    private JFXComboBox<String> combo_filter;

    private static Users user;
    private FileHelper fHelper;

    public filterController() {
        fHelper = new FileHelper();
        user = fHelper.ReadUserDetails();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            combo_filter.getItems().addAll("Add Filters", "Email Type", "Sort By", "Between Dates");
            // "Between TimeStamps", "Between Tickets"
            Platform.runLater(() -> combo_filter.getSelectionModel().select(0));

            setUpFilterAdd();
        }).start();
    }

    private boolean date = false, email = false, order = false;

    private void setUpFilterAdd() {

        Insets inset = new Insets(5, 2, 5, 1);

        combo_filter.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Email Type": {
                    if (email == true) {
                        makeToast();
                        return;
                    }
                    HBox emailType = new HBox();
                    emailType.setSpacing(10);

                    JFXComboBox<String> types = new JFXComboBox<>();
                    types.getItems().addAll("All", "My Emails", "Solved", "UnSolved", "Locked", "Unlocked", "Archived","AllocatedEmail");
                    types.setMinWidth(219);
                    types.setPromptText("Set Email Type");
                    types.setAccessibleText("emailType");

                    JFXButton del = deleteButton(emailType, newValue);
                    emailType.getChildren().addAll(types, del);

                    HBox.setMargin(types, inset);
                    HBox.setMargin(del, inset);

                    emailType.setAccessibleText(newValue);

                    filters_vbox.getChildren().add(emailType);
                    email = true;
                    break;
                }
                case "Sort By": {
                    if (order == true) {
                        makeToast();
                        break;
                    }
                    HBox orderBy = new HBox();
                    orderBy.setSpacing(10);
                    orderBy.setAccessibleText("orderBy");

                    JFXComboBox<String> types = new JFXComboBox<>();
                    types.getItems().addAll("Ticket", "From", "Time");
                    types.setMinWidth(100);
                    types.setAccessibleText("value");
                    types.setPromptText("Pick Value");

                    JFXComboBox<String> asc = new JFXComboBox<>();
                    asc.getItems().addAll("Ascending", "Descending");
                    asc.setMinWidth(98);
                    asc.setAccessibleText("ascdesc");
                    asc.setPromptText("ASC/DESC");

                    JFXButton del = deleteButton(orderBy, newValue);
                    orderBy.getChildren().addAll(types, asc, del);

                    HBox.setMargin(types, inset);
                    HBox.setMargin(asc, inset);
                    HBox.setMargin(del, inset);

                    orderBy.setAccessibleText(newValue);

                    filters_vbox.getChildren().add(orderBy);
                    order = true;
                    break;
                }
                case "Between Dates": {
                    if (date == true) {
                        makeToast();
                        break;
                    }

                    HBox btwDates = new HBox();
                    btwDates.setSpacing(10);
                    try {

                        DatePicker fromDate = new JFXDatePicker();
                        fromDate.setPromptText("From");
                        fromDate.setAccessibleText("fromDate");

                        DatePicker toDate = new JFXDatePicker();
                        toDate.setPromptText("To");
                        toDate.setAccessibleText("toDate");

                        JFXButton del = deleteButton(btwDates, newValue);
                        btwDates.getChildren().addAll(fromDate, toDate, del);

                        HBox.setMargin(fromDate, inset);
                        HBox.setMargin(toDate, inset);
                        HBox.setMargin(del, inset);

                    } catch (Exception e) {
//                        DatePicker fromDate = new DatePicker();
//                        fromDate.setPromptText("From");
//                        fromDate.setAccessibleText("fromDate");
//
//                        DatePicker toDate = new DatePicker();
//                        toDate.setPromptText("To");
//                        toDate.setAccessibleText("toDate");
//
//                        JFXButton del = deleteButton(btwDates, newValue);
//                        btwDates.getChildren().addAll(fromDate, toDate, del);
//
//                        HBox.setMargin(fromDate, inset);
//                        HBox.setMargin(toDate, inset);
//                        HBox.setMargin(del, inset);
                    }

                    btwDates.setAccessibleText(newValue);

                    filters_vbox.getChildren().add(btwDates);
                    date = true;
                    break;
                }
//                    case "Between TimeStamps": {
//                        if (time == true) {
//                            makeToast();
//                            break;
//                        }
//
//                        HBox btwTime = new HBox();
//                        btwTime.setSpacing(10);
//
//                        JFXTimePicker fromTime = new JFXTimePicker();
//                        fromTime.setPromptText("From");
//                        fromTime.setAccessibleText("fromTime");
//
//                        JFXTimePicker toTime = new JFXTimePicker();
//                        toTime.setPromptText("To");
//                        toTime.setAccessibleText("toTime");
//
//                        JFXButton del = deleteButton(btwTime, newValue);
//                        btwTime.getChildren().addAll(fromTime, toTime, del);
//
//                        HBox.setMargin(fromTime, inset);
//                        HBox.setMargin(toTime, inset);
//                        HBox.setMargin(del, inset);
//
//                        btwTime.setAccessibleText(newValue);
//
//                        filters_vbox.getChildren().add(btwTime);
//                        time = true;
//                        break;
//                    }
//                    case "Between Tickets": {
//                        HBox btwTicks = new HBox();
//                        btwTicks.setSpacing(10);
//
//                        JFXTextField fromTicket = new JFXTextField();
//                        fromTicket.setMinWidth(100);
//                        fromTicket.setPromptText("From");
//
//                        JFXTextField toTicket = new JFXTextField();
//                        toTicket.setMinWidth(98);
//                        toTicket.setPromptText("To");
//
//                        JFXButton del = deleteButton(btwTicks, newValue);
//                        btwTicks.getChildren().addAll(fromTicket, toTicket, del);
//
//                        HBox.setMargin(fromTicket, inset);
//                        HBox.setMargin(toTicket, inset);
//                        HBox.setMargin(del, inset);
//
//                        btwTicks.setAccessibleText(newValue);
//
//                        filters_vbox.getChildren().add(btwTicks);
//                        break;
//                    }
                default:
                    break;
            }

        });
    }

    @FXML
    public void setFilters(ActionEvent actionEvent) {

        dController.img_load.setVisible(true);

        new Thread(() -> {
            int filters = -1;
            String whereClause = " WHERE 1 ";
            String orderBy = "";

            boolean a = false;  //If Asked to bring archived
            for (Node e : filters_vbox.getChildren()) {
                filters++;
                HBox h = null;
                if (filters != 0) {
                    h = (HBox) e;
                }
                if (filters > 0) {
                    if (!e.getAccessibleText().equals("Sort By")) {
                        whereClause = whereClause + " AND ";
                    }
                }
                switch (e.getAccessibleText()) {
                    case "combo_filters": {
                        break;
                    }
                    case "Email Type": {        //-----------------------------------------------EMAIL TYPE-----------------
                        String emailClause = "";
                        String flag;
                        for (Node em : h.getChildren()) {
                            if (em.getAccessibleText().equals("del")) {
                                continue;
                            }
                            JFXComboBox<String> type = (JFXComboBox<String>) em;
                            if (type.getSelectionModel().getSelectedItem() != null) {


                                switch (type.getSelectionModel().getSelectedItem()) {
                                    case "All": {
                                        emailClause = " 1 ";
                                        break;
                                    }
                                    case "My Emails": {
                                        emailClause = " SOLVBY = " + user.getUCODE() + " " +
                                                " OR LOCKD = " + user.getUCODE();
                                        break;
                                    }
                                    case "Solved": {
                                        emailClause = " ESOLV = 'S'";
                                        break;
                                    }
                                    case "UnSolved": {
                                        emailClause = " ESOLV = 'N'";
                                        break;
                                    }
                                    case "Locked": {
                                        emailClause = " LOCKD != 0";
                                        break;
                                    }
                                    case "Unlocked": {
                                        emailClause = " LOCKD = 0";
                                        break;
                                    }
                                    case "Archived": {
                                        a = true;
                                        emailClause = " FREZE = 1 ";
                                        break;
                                    }
                                    case "AllocatedEmail": {
                                        emailClause = " isAllocatedFrom = 1 ";
                                        break;
                                    }

                                }
                            } else {
                                emailClause = " 1 ";
                            }
                        }


                        whereClause = whereClause + emailClause;
                        break;

                    }
                    case "Sort By": {          //-----------------------------------------------ORDER
                        // BY-------------------
                        String order = "";
                        for (Node em : h.getChildren()) {
                            switch (em.getAccessibleText()) {
                                case ("value"): {
                                    JFXComboBox<String> types = (JFXComboBox<String>) em;
                                    if (types.getSelectionModel().getSelectedItem() != null) {
                                        order = " ORDER BY ";
                                        switch (types.getSelectionModel().getSelectedItem()) {
                                            case ("Ticket"): {
                                                order = order + " EMNO ";
                                                break;
                                            }
                                            case ("From"): {
                                                order = order + " FRADD ";
                                                break;
                                            }
                                            case ("Time"): {
                                                order = order + " TSTMP ";
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                case ("ascdesc"): {
                                    JFXComboBox<String> asc = (JFXComboBox<String>) em;
                                    if (asc.getSelectionModel().getSelectedItem() != null) {
                                        switch (asc.getSelectionModel().getSelectedItem()) {
                                            case ("Ascending"): {
                                                order = order + " ASC ";
                                                break;
                                            }
                                            case ("Descending"): {
                                                order = order + " DESC ";
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        orderBy = order;
                        break;
                    }
                    case "Between Dates": {     //-------------------------------------Between Dates------------------------
                        String dates = " TSTMP >=";
                        for (Node em : h.getChildren()) {
                            switch (em.getAccessibleText()) {
                                case ("fromDate"): {
                                    JFXDatePicker from = (JFXDatePicker) em;
                                    if (from.getValue() == null) {
                                        Platform.runLater(() -> Toast.makeText((Stage) btn_filters.getScene().getWindow(), "Date Value Cannot be " +
                                                "null"));
                                        return;
                                    }
                                    dates = dates + " '" + from.getValue() + "' AND ";
                                    break;
                                }
                                case ("toDate"): {
                                    JFXDatePicker to = (JFXDatePicker) em;
                                    if (to.getValue() == null) {
                                        Platform.runLater(() -> Toast.makeText((Stage) btn_filters.getScene().getWindow(), "Date Value Cannot be " +
                                                "null"));
                                        return;
                                    }
                                    dates = dates + "TSTMP<'" + to.getValue() + "'";
                                    break;
                                }
                            }
                        }
                        whereClause = whereClause + dates;
                        break;
                    }
                    default:
                        break;

                }
            }

            if (a == false)
                whereClause = whereClause + " AND FREZE = 0";

            fHelper.WriteFilter(whereClause + " " + orderBy);

            Stage stage = (Stage) btn_filters.getScene().getWindow();

            Platform.runLater(() -> {
//                Platform.runLater(() -> Toast.makeText(stage, "Reload Emails for filters to take effect!"));
                EmailDashController.loadEmailsStatic();
                dController.img_load.setVisible(false);
                stage.close();
            });

        }).start();

    }


    private JFXButton deleteButton(HBox hBox, String option) {
        JFXButton del = new JFXButton("x");
        del.setMinSize(25, 25);
        del.setAccessibleText("del");
        del.setOnAction(event -> {
            filters_vbox.getChildren().remove(hBox);
            switch (option) {
                case "Email Type": {
                    email = false;
                    break;
                }
                case "Sort By": {
                    order = false;
                    break;
                }
                case "Between Dates": {
                    date = false;
                    break;
                }
//                    case "Between TimeStamps": {
//                        time = false;
//                        break;
//                    }
                default:
                    break;

            }
            event.consume();
        });

        return del;
    }

    private void makeToast() {
        Toast.makeText((Stage) btn_filters.getScene().getWindow(), "You can only add one of " +
                        "this", 1500,
                500, 500);
    }
}
