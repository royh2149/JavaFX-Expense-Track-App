import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class SettingsDialog extends Dialog {

    private Document doc;

    public SettingsDialog(Document doc){
        // add a submit button
        ButtonType closeDialog = new ButtonType("Done", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(closeDialog);

        this.doc = doc;

        TabPane tabs = new TabPane(); // create the tabs object

        // create the layouts for the tabs
        HBox uspsLayout = new HBox();
        VBox exportLayout = new VBox();

        // Username & Password tab buttons
        Button changeUsername = new Button("Chane Username");
        Button changePassword = new Button("Change Password");
        Button resetAccount = new Button("Reset Account");

        changeUsername.setOnAction(event -> {
            ChangePropertyDialog d = new ChangePropertyDialog("username");
            Optional res = d.showAndWait();
            Document document = d.getChange();

            if (res.isPresent()){
                if (res.get() == d.closeDialog){
                    Alert alert; // declare the alert

                    if (DatabaseTools.changeUsername(doc.get("username").toString(), d.inp.getText())){
                        alert = new Alert(Alert.AlertType.INFORMATION); // instantiate an error alert

                        alert.setTitle("Username Changed Successfully"); // add title

                        // add informative texts
                        alert.setHeaderText("Operation Succeeded");
                        alert.setContentText("Please remember your new username!");
                        this.doc.remove("username");
                        this.doc.put("username", d.inp.getText());
                    }else {
                        alert = new Alert(Alert.AlertType.ERROR); // instantiate an error alert

                        alert.setTitle("An error occurred!"); // add title

                        // add informative texts
                        alert.setHeaderText("Oops... Something went wrong!");
                        alert.setContentText("Username probably already taken or connection problems.");
                    }

                    alert.showAndWait(); // launch the alert
                }
            }
        });

        changePassword.setOnAction(event -> {
            ChangePropertyDialog d = new ChangePropertyDialog("password");
            Optional res = d.showAndWait();

            Document document = d.getChange();

            if (res.isPresent()){
                if (res.get() == d.closeDialog){
                    Alert alert; // declare the alert

                    // make sure passwords are identical and try update it
                    if (d.inp.getText().equals(d.confirmInp.getText()) && DatabaseTools.changePassword(doc.get("username").toString(), d.inp.getText())){
                        alert = new Alert(Alert.AlertType.INFORMATION); // instantiate an error alert

                        alert.setTitle("Password Changed Successfully"); // add title

                        // add informative texts
                        alert.setHeaderText("Operation Succeeded");
                        alert.setContentText("Please remember your new password!");

                        this.doc.remove("passwd");
                        this.doc.put("passwd", d.inp.getText());
                    }else {
                        alert = new Alert(Alert.AlertType.ERROR); // instantiate an error alert

                        alert.setTitle("An error occurred!"); // add title

                        // add informative texts
                        alert.setHeaderText("Oops... Something went wrong!");
                        alert.setContentText("Please check the Details you entered!");
                    }

                    alert.showAndWait(); // launch the alert
                }
            }
        });

        resetAccount.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getButtonTypes().add(ButtonType.CANCEL);

            alert.setTitle("Confirm Your Action"); // add title

            // add informative texts
            alert.setHeaderText("Are you sure you want to reset your account???");
            alert.setContentText("Operation cannot be reverted");

            Optional res = alert.showAndWait();

            if (((ButtonType)res.get()).getText().equals("OK")){
                DatabaseTools.resetAccount((String)this.doc.get("username"));
                System.out.println("User details were reset!");
            }

            this.doc.remove("actions");
            this.doc.put("actions", new ArrayList<Document>());
            this.doc.remove("balance");
            this.doc.put("balance", 0.0);
        });

        // add the buttons to the tab layout
        uspsLayout.getChildren().addAll(changeUsername, changePassword, resetAccount);


        ComboBox<String> timespan = new ComboBox<String>();
        ComboBox<String> typeSelect = new ComboBox<String>();

        timespan.getItems().addAll("Everything", "Last Week", "Last Month", "Last Year"); // add possible timestamps
        timespan.setValue("Everything"); // set default value as everything
        typeSelect.getItems().addAll("both", "income", "outcome"); // add type possible values
        typeSelect.setValue("both"); // set default value as both

        Label l1 = new Label("Select report time span:");
        Label l2 = new Label("Select report action type:");

        Button confirm = new Button("Export");
        confirm.setOnAction(event -> {
            this.export(timespan.getValue(), typeSelect.getValue());
        });

        HBox hb1 = new HBox();
        hb1.getChildren().addAll(l1, timespan);
        HBox hb2 = new HBox();
        hb2.getChildren().addAll(l2, typeSelect);

        exportLayout.getChildren().addAll(hb1, hb2, confirm);


        // first tab - username and password
        Tab usps = new Tab("Username & Password", uspsLayout);
        usps.setClosable(false); // prevent tab close

        // second tab - export to CSV
        Tab export = new Tab("Export to CSV", exportLayout);
        export.setClosable(false); // prevent tab close

        tabs.getTabs().addAll(usps, export); // add the tabs to the tabPane

        this.setGraphic(tabs); // set the view
    }

    public void export(String timespan, String type){
        ArrayList<Document> actionsList = new ArrayList<>();
        LocalDateTime startDate = HelperMethods.getStartDate(timespan);
        for(Document cur : (ArrayList<Document>)this.doc.get("actions")){
            // determine whether the current action is income or outcome - and if it should be counted
            if (cur.get("type").toString().equals(type) || type.equals("both")){
                // make sure the action is in the requested timespan
                if (HelperMethods.convertToLocalDateTimeViaInstant((Date) cur.get("date")).isAfter(startDate)){
                    // if all the terms apply, update the amount of money in the category
                    actionsList.add(cur);
                }
            }
        }
        try {
            HelperMethods.generateCSV(actionsList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Document getDoc(){
        return this.doc;
    }
}
