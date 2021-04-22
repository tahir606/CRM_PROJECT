package product;

import dashboard.dController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import product.newProduct.NewProductController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductDashController implements Initializable {

    @FXML
    private BorderPane main_pane;
    @FXML
    private MenuBar menu_products;

    public static BorderPane main_paneF;
    private ImageView img_loader = dController.img_load;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        main_paneF = main_pane;

        populateContactsMenuBar();

        inflatePane("view/product_view.fxml");
    }

    private void populateContactsMenuBar() {

        Menu menuNew = new Menu("New");

        MenuItem newProduct = new MenuItem("New Product");
        newProduct.setOnAction(event -> {
            NewProductController.stInstance = 'N';
            inflatePane("newProduct/new_product.fxml");
        });
        menuNew.getItems().addAll(newProduct);

        menu_products.getMenus().add(menuNew);
    }

    private void inflatePane(String pane) {
        img_loader.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        main_pane.setCenter(FXMLLoader.load(getClass().getResource(pane)));
                    } catch (IOException e) {
                        e.getLocalizedMessage();
//                        e.printStackTrace();
                    }
                    img_loader.setVisible(false);
                });
            }
        }).start();
    }

}
