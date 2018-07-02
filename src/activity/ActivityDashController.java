package activity;

import activity.task.NewTaskController;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActivityDashController implements Initializable {

    @FXML
    private BorderPane main_pane;
    @FXML
    private MenuBar menu_activity;

    public static BorderPane main_paneF;
    private ImageView img_loader = dController.img_load;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        main_paneF = main_pane;

        populateContactsMenuBar();

        inflatePane("view/lead_view.fxml");
    }

    private void populateContactsMenuBar() {

        Menu menuNew = new Menu("New");

        MenuItem newProduct = new MenuItem("New Task");
        newProduct.setOnAction(event -> {
            NewTaskController.stInstance = 'N';
            inflatePane("task/new_task.fxml");
        });
        menuNew.getItems().addAll(newProduct);

        menu_activity.getMenus().add(menuNew);
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
                        e.printStackTrace();
                    }
                    img_loader.setVisible(false);
                });
            }
        }).start();
    }
    
}
