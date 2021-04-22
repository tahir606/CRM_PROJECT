package lead.view;

import JCode.mysql.mySqlConn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lead.LeadDashController;
import objects.Lead;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeadViewController implements Initializable {
    
    @FXML
    private TableView<Lead> table_product;
    @FXML
    private TableColumn<Lead, String> col_name;
    @FXML
    private TableColumn<Lead, String> col_cname;
    @FXML
    private TableColumn<Lead, String> col_website;
    @FXML
    private TableColumn<Lead, String> col_city;
    @FXML
    private TableColumn<Lead, String> col_country;
    @FXML
    private AnchorPane toolbar_products;
    @FXML
    private Label txt_no;
    
    public static Lead staticLead;
    private mySqlConn sql;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sql = new mySqlConn();
    
        toolbar_products.setVisible(false);

//        table_contact.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        col_name.setCellValueFactory(new PropertyValueFactory<>("fullNameProperty"));
        col_cname.setCellValueFactory(new PropertyValueFactory<>("company"));
        col_website.setCellValueFactory(new PropertyValueFactory<>("website"));
        col_city.setCellValueFactory(new PropertyValueFactory<>("city"));
        col_country.setCellValueFactory(new PropertyValueFactory<>("country"));
        
        table_product.getItems().setAll(sql.getAllLeads(null));

//        table_contact.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
//            selectedContacts = table_contact.getSelectionModel().getSelectedItems();
//
//            if (selectedContacts == null || selectedContacts.size() == 0) {
//                toolbar_contacts.setVisible(false);
//                return;
//            } else {
//                toolbar_contacts.setVisible(true);
//                txt_no.setText(String.valueOf(selectedContacts.size()));
//            }
//
//        });
        
        table_product.setRowFactory(tv -> {
            TableRow<Lead> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    staticLead = row.getItem();
                    try {
                        LeadDashController.main_paneF.setCenter(
                                FXMLLoader.load(
                                        getClass().getClassLoader().getResource
                                                ("lead/details/lead_details.fxml")));
                    } catch (IOException e) {
                        e.getLocalizedMessage();
//                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }
}
