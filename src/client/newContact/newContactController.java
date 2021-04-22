package client.newContact;

import JCode.CommonTasks;
import JCode.Toast;
import JCode.mysql.mySqlConn;
import client.dash.contactView.contactViewController;
import client.dashBaseController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import objects.ClientProperty;
import objects.ContactProperty;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class newContactController implements Initializable {
    
    
    @FXML
    private Label txt_heading;
    @FXML
    private JFXTextField txt_fname;
    @FXML
    private JFXTextField txt_lname;
    @FXML
    private JFXButton btn_back;
    @FXML
    private JFXTextField txt_email;
    @FXML
    private JFXTextField txt_mobile;
    @FXML
    private JFXTextField txt_addr;
    @FXML
    private JFXTextField txt_city;
    @FXML
    private JFXTextField txt_country;
    @FXML
    private TextArea txt_note;
    @FXML
    private JFXDatePicker date_of_birth;
    @FXML
    private JFXButton btn_save;
    @FXML
    private JFXComboBox client_list;
    
    mySqlConn sql;
    
    static ContactProperty contact;
    
    private List<ClientProperty> allClients;
    
    public static char stInstance;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sql = new mySqlConn();
        contact = new ContactProperty();
        
        Image image = new Image(this.getClass().getResourceAsStream("/res/img/left-arrow.png"));
        btn_back.setGraphic(new ImageView(image));
        btn_back.setAlignment(Pos.CENTER_LEFT);
        btn_back.setTooltip(new Tooltip("Back to Contacts"));
        btn_back.setOnAction(event -> {
            try {
                dashBaseController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("client/dash/contactView/contactView.fxml")));
                
            } catch (IOException e) {
                e.getLocalizedMessage();
//                e.printStackTrace();
            }
        });
        
        allClients = sql.getAllClients("CL_TYPE = 1");
        client_list.getItems().addAll(allClients);
        
        txt_fname.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                //prevents from the new space char
                txt_fname.setText(oldValue);
            }
        });
        
        txt_lname.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(" ")) {
                //prevents from the new space char
                txt_lname.setText(oldValue);
            }
        });
        
        switch (stInstance) {
            case 'N': {      //New
                txt_heading.setText("New Contact");
                contact.setCode(sql.getNewContactCode());
                btn_save.setText("Create");
                break;
            }
            case 'U': {      //Update
                txt_heading.setText("Update Contact");
                contact.setCode(contactViewController.staticContact.getCode());
                btn_save.setText("Update");
                populateContact();
                break;
            }
            default:
                break;
        }
        
    }
    
    public void populateContact() {
        ContactProperty contact = contactViewController.staticContact;
        txt_fname.setText(contact.getFirstName());
        txt_lname.setText(contact.getLastName());
        txt_email.setText(contact.getEmail());
        txt_mobile.setText(contact.getMobile());
        txt_addr.setText(contact.getAddress());
        txt_city.setText(contact.getCity());
        txt_country.setText(contact.getCountry());
        txt_note.setText(contact.getNote());
        
        date_of_birth.setValue(CommonTasks.createLocalDate(contact.getDob()));
        
        for (ClientProperty c : allClients) {
            if (c.getCode() == contact.getClID()) {
                client_list.getSelectionModel().select(c);
                break;
            }
        }
    }
    
    public void saveChanges(ActionEvent actionEvent) {
        String fname = txt_fname.getText(),
                lname = txt_lname.getText(),
                email = txt_email.getText(),
                mobile = txt_mobile.getText(),
                addr = txt_addr.getText(),
                city = txt_city.getText(),
                country = txt_country.getText(),
                note = txt_note.getText(),
                jdate = String.valueOf(date_of_birth.getValue());
        
        
        if (fname.equals("") || lname.equals("") || city.equals("") || country.equals("")) {
            Toast.makeText((Stage) btn_save.getScene().getWindow(), "Required Fields Are Empty");
            return;
        } else {
            String msg = "";
            switch (stInstance) {
                case 'N': {
                    msg = "Are you sure you want to add Contact?";
                    break;
                }
                case 'U': {
                    msg = "Are you sure you want to update Contact?";
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
                
                contact.setFirstName(fname);
                contact.setLastName(lname);
                contact.setAddress(addr);
                contact.setCity(city);
                contact.setCountry(country);
                contact.setNote(note);
                if (jdate.equals("null"))
                    contact.setDob(null);
                else
                    contact.setDob(jdate);
//                contact.setEmails(new String[]{email});
//                contact.setPhones(new String[]{mobile});
                contact.setEmail(email);
                contact.setMobile(mobile);
                
                ClientProperty c = (ClientProperty) client_list.getSelectionModel().getSelectedItem();
                
                if (c != null)
                    contact.setClID(c.getCode());
                

                
                switch (stInstance) {
                    case 'N': {
                        sql.insertContact(contact);
                        break;
                    }
                    case 'U': {
                        sql.updateContact(contact);
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
