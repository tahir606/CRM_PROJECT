package lead.details;

import JCode.GUIConstructor;
import JCode.mysql.mySqlConn;
import client.dashBaseController;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lead.LeadDashController;
import lead.newLead.NewLeadController;
import lead.view.LeadViewController;
import objects.Lead;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeadDetailsController implements Initializable {
    @FXML
    private Label txt_fname;
    @FXML
    private Label txt_company;
    @FXML
    private Label txt_website;
    @FXML
    private Label txt_email;
    @FXML
    private Label txt_mobile;
    @FXML
    private TextArea txt_desc;
    @FXML
    private JFXButton btn_back;
    @FXML
    private JFXButton btn_edit;
    @FXML
    private VBox notes_list;

    private mySqlConn sql;
    
    private Lead lead;

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
                e.printStackTrace();
            }
        });

        lead = LeadViewController.staticLead;
        populateDetails();

    }

    private void populateDetails() {
        txt_fname.setText(lead.getFullNameProperty());
        txt_company.setText(lead.getCompany());
        txt_website.setText(lead.getWebsite());
        txt_email.setText(lead.getEmail());
        txt_mobile.setText(lead.getPhone());
        txt_desc.setText(lead.getNote());

//        btn_email.setOnAction(event -> {
//            EResponseController.stTo = contact.getEmail();
//            EResponseController.stInstance = 'N';
//            inflateEResponse(1);
//        });

        btn_edit.setOnAction(event -> {
            NewLeadController.stInstance = 'U';
            try {
                LeadDashController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("lead/newLead/new_lead.fxml")));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    
        new GUIConstructor(notes_list, sql, lead).generalConstructor(3);
    }
}
