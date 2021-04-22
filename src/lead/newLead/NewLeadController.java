package lead.newLead;

import JCode.Toast;
import JCode.mysql.mySqlConn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lead.LeadDashController;
import lead.view.LeadViewController;
import objects.Lead;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewLeadController implements Initializable {

    @FXML
    private JFXTextField txt_fname, txt_lname, txt_company, txt_website, txt_city, txt_country, txt_email, txt_mobile;
    @FXML
    private TextArea txt_note;
    @FXML
    private JFXButton btn_back, btn_save;
    @FXML
    private JFXComboBox<String> combo_source;
    @FXML
    private HBox hbox_other;
    @FXML
    private Label txt_heading;

    public static char stInstance;

    private Lead lead;
    private mySqlConn sql;

    public static int noOfFields = 2;   //No of emails and phones

    private TextArea otherTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sql = new mySqlConn();

        Image image = new Image(this.getClass().getResourceAsStream("/res/img/left-arrow.png"));
        btn_back.setGraphic(new ImageView(image));
        btn_back.setAlignment(Pos.CENTER_LEFT);
        btn_back.setTooltip(new Tooltip("Back to Leads"));
        btn_back.setOnAction(event -> {
            try {
                LeadDashController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("lead/view/lead_view.fxml")));

            } catch (IOException e) {
                e.getLocalizedMessage();
//                e.printStackTrace();
            }
        });

        combo_source.getItems().addAll(sql.getAllSources());
        combo_source.getItems().add("Other");
        combo_source.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equalsIgnoreCase("Other")) {
                otherTextArea = new TextArea();
                otherTextArea.setPromptText("Other Source");
                hbox_other.getChildren().add(otherTextArea);
            } else {
                hbox_other.getChildren().clear();
            }
        });


        if (stInstance == 'N') {
            btn_save.setText("Add");
            lead = new Lead();
            lead.setCode(sql.getNewLeadCode());
            txt_heading.setText("New Lead");
        } else if (stInstance == 'U') {
            btn_save.setText("Update");
            populateLead();
            txt_heading.setText("Update Lead");
        }
    }

    private void populateLead() {
        lead = LeadViewController.staticLead;

        txt_fname.setText(lead.getFirstName());
        txt_lname.setText(lead.getLastName());
        txt_website.setText(lead.getWebsite());
        txt_company.setText(lead.getCompany());
        txt_email.setText(lead.getEmail());
        txt_mobile.setText(lead.getPhone());
        txt_city.setText(lead.getCity());
        txt_country.setText(lead.getCountry());
        txt_note.setText(lead.getNote());

        if (lead.getOtherText() == null) {
            combo_source.getSelectionModel().select(lead.getSource() - 1);
        } else {
            combo_source.getSelectionModel().select("Other");
            otherTextArea.setText(lead.getOtherText());
        }
    }

    public void saveChanges(ActionEvent actionEvent) {

        String fname = txt_fname.getText(),
                lname = txt_lname.getText(),
                email = txt_email.getText(),
                phone = txt_mobile.getText(),
                company = txt_company.getText(),
                city = txt_city.getText(),
                country = txt_country.getText(),
                desc = txt_note.getText(),
                website = txt_website.getText();

        int source = combo_source.getSelectionModel().getSelectedIndex();

        if (fname.equals("") || lname.equals("") || city.equals("") || country.equals("") || company.equals("")) {
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Required Fields Are Empty");
            return;
        } else {
            String msg = "";
            switch (stInstance) {
                case 'N': {
                    msg = "Are you sure you want to add Lead?";
                    break;
                }
                case 'U': {
                    msg = "Are you sure you want to update Lead?";
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

                fname = fname.substring(0, 1).toUpperCase() + fname.substring(1);   //Make First Letter to Uppercase
                lname = lname.substring(0, 1).toUpperCase() + lname.substring(1);

                lead.setFirstName(fname);
                lead.setLastName(lname);
                lead.setCompany(company);
                lead.setCity(city);
                lead.setCountry(country);
                lead.setNote(desc);
                lead.setEmail(email);
                lead.setPhone(phone);
                lead.setWebsite(website);
                lead.setSource(combo_source.getSelectionModel().getSelectedIndex() + 1);
                if (combo_source.getSelectionModel().getSelectedItem().equalsIgnoreCase("Other")) {
                    if (otherTextArea.getText().toString().trim().length() < 1) {
                        Toast.makeText((Stage) btn_save.getScene().getWindow(), "Other source text cannot be empty");
                        return;
                    } else {
                        lead.setOtherText(otherTextArea.getText().toString());
                    }
                } else {
                    lead.setOtherText(null);
                }

                switch (stInstance) {
                    case 'N': {
                        sql.insertLead(lead);
                        break;
                    }
                    case 'U': {
                        sql.updateLead(lead);
                        break;
                    }
                    default: {
                        break;
                    }
                }

            } else {
                return;
            }
        }
    }
}
