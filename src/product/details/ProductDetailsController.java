package product.details;

import JCode.CommonTasks;
import gui.TasksConstructor;
import gui.NotesConstructor;
import JCode.mysql.mySqlConn;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import objects.ProductModule;
import objects.ProductProperty;
import product.ProductDashController;
import product.newProduct.NewProductController;
import product.view.ProductViewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductDetailsController implements Initializable {

    @FXML
    private Label txt_pname;
    @FXML
    private Label txt_price;
    @FXML
    private Label txt_startedOn;
    @FXML
    private TextArea txt_desc;
    @FXML
    private JFXButton btn_back;
    @FXML
    private JFXButton btn_edit;
    @FXML
    private VBox notes_list;
    @FXML
    private VBox vbox_main;
    @FXML
    private VBox open_activities_list;
    @FXML
    private VBox closed_activities_list;
    @FXML
    private VBox vbox_modules;
    private static VBox vbox_modulesS;

    private static ProductProperty product;
    public static ProductModule selectedModule;
    private static mySqlConn sql;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vbox_modulesS = vbox_modules;
        sql = new mySqlConn();

        Image image = new Image(this.getClass().getResourceAsStream("/res/img/left-arrow.png"));
        btn_back.setGraphic(new ImageView(image));
        btn_back.setAlignment(Pos.CENTER_LEFT);
        btn_back.setTooltip(new Tooltip("Back to Products"));
        btn_back.setOnAction(event -> {
            try {
                ProductDashController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("product/view/product_view.fxml")));

            } catch (IOException e) {
                e.getLocalizedMessage();
//                e.printStackTrace();
            }
        });

        btn_edit.setOnAction(event -> {
            NewProductController.stInstance = 'U';
            try {
                ProductDashController.main_paneF.setCenter(
                        FXMLLoader.load(
                                getClass().getClassLoader().getResource("product/newProduct/new_product.fxml")));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        populateDetails();
        init(this.getClass().getResource("unlock_dialog.fxml"));

    }

    public static void init(URL path) {
        ProductProperty prod = sql.getProductModuleStates(product);
        populateModules(prod, path);
    }

    private void populateDetails() {
        product = ProductViewController.staticProduct;
        txt_pname.setText(product.getName());
        txt_price.setText(String.valueOf(product.getPrice()));
        txt_desc.setText(product.getDesc());
        txt_startedOn.setText(product.getFormattedDate());

        vbox_modules.setSpacing(10);
    
        TabPane tabPane = new TabPane();
        tabPane.setMinWidth(600);
        new NotesConstructor(tabPane, sql, product).generalConstructor(4);
        new TasksConstructor(tabPane, product).generalConstructor(4);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);
    
        vbox_main.getChildren().add(tabPane);
    }

    private static void populateModules(ProductProperty product, URL path) {
        vbox_modulesS.getChildren().clear();
        for (ProductModule module : product.getProductModules()) {
            HBox box = new HBox();
            box.setSpacing(10);
            box.setPadding(new Insets(5, 5, 5, 5));
            Label name = new Label();
            name.setText(module.getName());
            name.setMinWidth(60);
            name.setMaxWidth(60);

            TextArea desc = new TextArea();
            desc.setMaxWidth(200);
            desc.setMaxHeight(60);
            desc.setWrapText(true);
            desc.setEditable(false);
            desc.setText(module.getDesc());
            desc.getStyleClass().add("scroll-view");

            JFXButton btnState = new JFXButton();
            if (module.getState() == '\0')
                module.setState(0);

            switch (module.getState()) {
                case 0: {
                    btnState.getStyleClass().removeAll();
                    btnState.setStyle("-fx-background-color: #feffe0;");
                    btnState.setDisable(false);
                    break;
                }
                case 1: {
                    btnState.getStyleClass().removeAll();
                    btnState.getStyleClass().add("unlockedEmail");
                    btnState.setDisable(false);
                    break;
                }
                case 2: {   //When module is not locked by you
                    btnState.getStyleClass().removeAll();
                    btnState.getStyleClass().add("unlockedEmail");
                    btnState.setDisable(true);
                    break;
                }
            }

            btnState.setText((module.getState() == 0 ? "LOCK" : "UNLOCK"));
            btnState.setMinWidth(60);
            btnState.setOnAction(event -> {
                selectedModule = module;
                switch (module.getState()) {
                    case 0: {
                        sql.lockModule(module);
                        init(path);
                        break;
                    }
                    case 1: {
                        UnlockDialogController.fromPane = 'P';
                        CommonTasks.inflateDialog("Confirmation?", path);
                        break;
                    }
                    case 2: {
                        break;
                    }
                }
            });

            box.getChildren().addAll(name, desc, btnState);
            box.getStyleClass().add("moduleDetails");
            vbox_modulesS.getChildren().add(box);
        }
    }
}
