package gui;

import JCode.FileHelper;
import JCode.Toast;
import JCode.mysql.mySqlConn;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.*;

public class NotesConstructor {

    TextArea noteTxt;
    JFXButton btnAdd;

    TabPane tabPane;
    Tab tab;
    VBox notes_list, main_box;
    mySqlConn sql;
    Email email;
    ContactProperty contact;
    ClientProperty client;
    Lead lead;
    ProductProperty product;
    private FileHelper fHelper =new FileHelper();

    //    public NotesConstructor(VBox notes_list, mySqlConn sql, ContactProperty contact) {
//        this.notes_list = notes_list;
//        this.sql = sql;
//        this.contact = contact;
//    }
    public NotesConstructor(TabPane tabPane, mySqlConn sql, ContactProperty contact) {
        this.tabPane = tabPane;
        notes_list = new VBox();
        tab = new Tab("Notes");
        this.sql = sql;
        this.contact = contact;
    }

    public NotesConstructor(TabPane tabPane, mySqlConn sql, ClientProperty client) {
        this.tabPane = tabPane;
        notes_list = new VBox();
        tab = new Tab("Notes");
        this.sql = sql;
        this.client = client;
    }

    public NotesConstructor(TabPane tabPane, mySqlConn sql, Lead lead) {
        this.tabPane = tabPane;
        notes_list = new VBox();
        tab = new Tab("Notes");
        this.sql = sql;
        this.lead = lead;
    }

    public NotesConstructor(TabPane tabPane, mySqlConn sql, ProductProperty product) {
        this.tabPane = tabPane;
        notes_list = new VBox();
        tab = new Tab("Notes");
        this.sql = sql;
        this.product = product;
    }

    public NotesConstructor(TabPane tabPane, mySqlConn sql, Email email) {
            this.tabPane = tabPane;
            notes_list = new VBox();
            tab = new Tab("Notes");
            this.sql = sql;
            this.email = email;

    }

    public void constructingContactNotes(int choice) {
        ContactProperty contact = sql.getParticularContact(this.contact);
        notes_list.getChildren().clear();
        for (Note note : contact.getContactNotes())
            constructNote(note, choice);

        createNew(choice);
    }

    public void constructingClientNotes(int choice) {
        ClientProperty client = sql.getParticularClient(this.client);
        notes_list.getChildren().clear();
        for (Note note : client.getNotes())
            constructNote(note, choice);

        createNew(choice);
    }

    public void constructingLeadNotes(int choice) {
        Lead lead = sql.getParticularLead(this.lead);
        notes_list.getChildren().clear();
        for (Note note : lead.getNotes())
            constructNote(note, choice);

        createNew(choice);
    }

    public void constructingProductNotes(int choice) {
        ProductProperty product = sql.getParticularProduct(this.product);
        notes_list.getChildren().clear();
        for (Note note : product.getNotes())
            constructNote(note, choice);

        createNew(choice);
    }

    public void constructingEmailNotes(int choice) {
        Email email = sql.getParticularEmail(this.email);
        if(email==null)
            return;
        notes_list.getChildren().clear();
        for (Note note : email.getNotes())
            constructNote(note, choice);
        createNew(choice);
    }

    private void constructNote(Note note, int choice) {
        //The First Part
        HBox body = new HBox();
        body.getChildren().clear();
        body.setSpacing(5);
        TextArea area = new TextArea(note.getText());
        if (choice == 5) {
            area.setMaxWidth(210);
            area.setMinWidth(210);
        }
        area.setWrapText(true);
        area.setEditable(false);
        area.setMinHeight(50);
        area.setMaxHeight(50);
        area.setStyle("-fx-background-color: #fcfcfc;" +
                "-fx-background: transparent;");
        body.getChildren().add(area);
        //The Second Part
        HBox details = new HBox();
        details.getChildren().clear();
        details.setSpacing(10);
        details.setMinHeight(25);
        details.setPadding(new Insets(3));
        Label createdBy = new Label(note.getCreatedByName()),
                createdOn = new Label(note.getCreatedOn());

        if (choice == 5) {      //        if Emails Clicked then set the width of label
            createdOn.setMinWidth(100);
            createdBy.setMinWidth(100);
        } else {                //         else remain same for all
            createdOn.setMinWidth(150);
            createdBy.setMinWidth(280);
        }
        JFXButton options = new JFXButton();
        Image image = new Image(getClass().getResourceAsStream("/res/img/options.png"));
        options.setGraphic(new ImageView(image));
        details.getChildren().addAll(createdOn, createdBy, options);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delItem = new MenuItem("Delete"),
                editItem = new MenuItem("Edit");
        editItem.setOnAction(t -> {
            if(note.getCreatedBy()  == fHelper.ReadUserDetails().getUCODE()){
                area.setEditable(true);
                area.setStyle("-fx-background: #fcfcfc;");
                area.requestFocus();
                JFXButton saveBtn = new JFXButton("Save"),
                        cancelBtn = new JFXButton("Cancel");
                if (choice == 5) {  //   if Emails Clicked then set the width of save and cancel button
                    saveBtn.setMinWidth(30);
                    cancelBtn.setMinWidth(30);
                } else {  //   remain same
                    saveBtn.setMinWidth(60);
                    cancelBtn.setMinWidth(60);
                }
                saveBtn.setButtonType(JFXButton.ButtonType.RAISED);
                saveBtn.setStyle("-fx-background-color: #e8f0ff;");
                cancelBtn.setButtonType(JFXButton.ButtonType.RAISED);
                cancelBtn.setStyle("-fx-background-color: #e8f0ff;");

                saveBtn.setOnAction(event -> {
                    note.setText(area.getText().toString());
                    switch (choice) {
                        case 1:
                            sql.updateNote(note, contact);
                            break;
                        case 2:
                            sql.updateNote(note, client);
                            break;
                        case 3:
                            sql.updateNote(note, lead);
                            break;
                        case 4:
                            sql.updateNote(note, product);
                            break;
                        case 5:
                            sql.updateNote(note, email);
                            break;
                    }
                    generalConstructor(choice);
                });
                cancelBtn.setOnAction(event -> {
                    body.getChildren().remove(saveBtn);
                    body.getChildren().remove(cancelBtn);
                    area.setEditable(false);
                    area.setStyle("-fx-background-color: #fcfcfc;" +
                            "-fx-background: transparent;");
                });

                body.getChildren().addAll(saveBtn, cancelBtn);
            }else{
                Toast.makeText((Stage) area.getScene().getWindow(), "Note cannot be edit");
                return;
            }
        });
        delItem.setOnAction(t -> {
            if(note.getCreatedBy()  == fHelper.ReadUserDetails().getUCODE()){
                switch (choice) {
                    case 1:
                        sql.deleteNote(note, contact);
                        break;
                    case 2:
                        sql.deleteNote(note, client);
                        break;
                    case 3:
                        sql.deleteNote(note, lead);
                        break;
                    case 4:
                        sql.deleteNote(note, product);
                        break;
                    case 5:
                        sql.deleteNote(note, email);
                        break;
                }
                generalConstructor(choice);
            }else{
                Toast.makeText((Stage) area.getScene().getWindow(), "Note cannot be deleted");
                return;
            }
        });
        contextMenu.getItems().addAll(editItem, delItem);
        options.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                contextMenu.show(options, me.getScreenX(), me.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        VBox box = new VBox();
        box.getChildren().clear();
//            box.setStyle("-fx-border-color: #033300;");
        box.getChildren().addAll(body, details);
        notes_list.setSpacing(10);
        notes_list.getChildren().add(0, box);
    }

    public void createNew(int choice) {
        HBox addNew = new HBox();
        addNew.getChildren().clear();
        addNew.setSpacing(5);
        noteTxt = new TextArea();
        noteTxt.setMinHeight(50);
        noteTxt.setMaxHeight(50);
        if (choice == 5) {  //      if Emails Clicked then set the width of text area
            noteTxt.setMinWidth(260.0);
            noteTxt.setMaxWidth(260.0);
        }
        noteTxt.setPromptText("New Note");
        btnAdd = new JFXButton();
        btnAdd.setText("Add");
        btnAdd.setButtonType(JFXButton.ButtonType.RAISED);
        btnAdd.setStyle("-fx-background-color: #efefef;");
        addNew.getChildren().addAll(noteTxt, btnAdd);
        notes_list.getChildren().add(addNew);

        btnAdd.setOnAction(event -> {
            String note = noteTxt.getText().toString();
            if (note.trim().equals("")) {
                Toast.makeText((Stage) noteTxt.getScene().getWindow(), "Note cannot be empty");
                return;
            } else {
                switch (choice) {
                    case 1:
                        sql.addNote(note, contact);
                        break;
                    case 2:
                        sql.addNote(note, client);
                        break;
                    case 3:
                        sql.addNote(note, lead);
                        break;
                    case 4:
                        sql.addNote(note, product);
                        break;
                    case 5:
                        sql.addNote(note, email);
                        break;
                }
                generalConstructor(choice);
            }
        });
    }

    public void generalConstructor(int choice) {
        switch (choice) {
            case 1: { //Contact
                constructingContactNotes(choice);
                break;
            }
            case 2: { //Clients
                constructingClientNotes(choice);
                break;
            }
            case 3: { //Clients
                constructingLeadNotes(choice);
                break;
            }
            case 4: {
                constructingProductNotes(choice);
                break;
            }
            case 5: {

                constructingEmailNotes(choice);
                break;
            }

        }
        notes_list.setPrefHeight(500);
        ScrollPane sp = new ScrollPane(notes_list);
        sp.getStyleClass().add("scroll-view");
        tab.setContent(sp);
        tabPane.getTabs().add(tab);
    }


}
