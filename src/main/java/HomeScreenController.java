import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.bson.Document;

import org.json.JSONArray;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import java.text.DecimalFormat;
import java.math.RoundingMode;

public class HomeScreenController extends updatable { // to update the scene

    @FXML
    private Label balanceLabel, username;

    @FXML
    private ListView list; // actions list

    @FXML
    private Button add, settings;

    private Document doc; // the user's info
    private static final double MAX = 1000000;
    public static final String DEFAULT_VAL = "Everything";
    public static HashMap<String, Paint> ctColors = new HashMap<String, Paint>(){{
        put("Food", Color.BLUE);
        put("Salary", Color.GREENYELLOW);
        put("Media & Telekom", Color.RED);
        put("Clothing", Color.ORANGE);
        put("Entertainment", Color.LIGHTPINK);
        put("Pharmacy", Color.PURPLE);
        put("Fixed Payments", Color.BROWN);
        put("Other", Color.BLACK);
    }};

    public void setDoc(Document doc) {
        this.doc = doc;
    }

//    // This method is called upon fxml load
//    public void initialize(URL location, ResourceBundle resources) {
//        ctColors.p
//    }

    // Will decide which type of content to display
    public void Personalize(){
        if (!(this.doc == null)){ // make sure the document is not null - authentication made successfully

            DecimalFormat df = new DecimalFormat("#.##"); // create formatter to store 2 decimal places only
            df.setRoundingMode(RoundingMode.HALF_EVEN); // round the balance according to universal rules

            Double balance = (double) this.doc.get("balance"); // get user's balance
            balance = Double.valueOf(df.format(balance)); // apply format rules
            String name = (String) this.doc.get("username"); // get users username


            if (!(this.doc.get("actions") == null)){ // ensure actions are not empty
                this.list.getItems().clear(); // remove all items
                ArrayList actions = (ArrayList) this.doc.get("actions"); // get all the actions

                for (Object action : actions) { // iterate through the actions as Documents
                    Document document = (Document) action; // convert to Document

                    HBox hbox = new HBox(); // labels' horizontal layout
                    hbox.setSpacing(30); // set spacing

                    // create date label
                    LocalDateTime date = HelperMethods.convertToLocalDateTimeViaInstant((Date) document.get("date"));
                    String strDate = date.getMonth().name() + " " + date.getDayOfMonth() + ", " + date.getYear();
                    Label dateLabel = new Label(strDate);
                    // take care of the styling
                    dateLabel.getStylesheets().add("/styles/table.css");
                    dateLabel.getStyleClass().add("dates");

                    // create action type label
                    Label type = new Label(document.get("type").toString());
                    // take care of the styling
                    type.getStyleClass().add("types");
                    type.getStylesheets().add("/styles/table.css");

                    // create action sum label
                    Label sum = new Label(document.get("sum").toString());
                    // take care of the styling
                    sum.getStylesheets().add("/styles/table.css");
                    sum.getStyleClass().add("sums");

                    // create action category label
                    Label category = new Label(document.get("category").toString());
                    // take care of the styling
                    category.getStyleClass().add("categories");
                    category.getStylesheets().add("/styles/table.css");
                    category.setTextFill(ctColors.get(category.getText()));

                    // Set sum label color according to the action type
                    if (type.getText().equals("outcome")){
                        sum.setTextFill(Color.RED);
                    }else if (type.getText().equals("income")){
                        sum.setTextFill(Color.GREEN);
                    }


                    hbox.getChildren().addAll(dateLabel, type, sum, category); // add the label to the layout
                    this.list.getItems().add(hbox); // add the created layout to the list view

                }
            }
            this.username.setText("Howdy, " + name); // Welcome the user
            this.balanceLabel.setText(balance.toString()); // set the user balance as the main label

            // Set balance label color according to the amount
            if (balance >= 0){
                this.balanceLabel.setTextFill(Color.GREEN);
            }else {
                this.balanceLabel.setTextFill(Color.RED);
            }
        }
    }

    public void addAction(ActionEvent event) {

        Document newAction = new Document(); // the action to be created

        Dialog<ButtonType> dialog = new Dialog(); // create the dialog
        VBox vbox1 = new VBox(); // date input vbox
        VBox vbox2 = new VBox(); // type input vbox
        VBox vbox3 = new VBox(); // sum input vbox
        VBox vbox4 = new VBox(); // category input vbox


        HBox hbox = new HBox(); // main layout
        hbox.setSpacing(dialog.getWidth()/3); // span the components broadly upon the dialog

        DatePicker datePicker = new DatePicker(); // date picking object
        vbox1.getChildren().addAll(new Label("Date:"), datePicker); // add the needed components for the dates label

        ComboBox typeSelect = new ComboBox(); // create the type picking widget
        typeSelect.getItems().addAll("Income", "Outcome"); // add the 2 possible options
        typeSelect.setValue("Income"); // set default value as income
        vbox2.getChildren().addAll(new Label("Type:"), typeSelect); // add the needed components for the type label


//        Spinner<Double> sumSelect = new Spinner<Double>();
//        // Value factory.
//        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, MAX, 0);
//        sumSelect.setValueFactory(valueFactory);
        TextField sumSelect = new TextField();
        vbox3.getChildren().addAll(new Label("Sum:"), sumSelect); // add the needed components for the sum label

        ComboBox categorySelect = new ComboBox(); // create the type picking widget
        categorySelect.getItems().addAll(ctColors.keySet()); // add the 2 possible options
        categorySelect.setValue("Other"); // set default value as income
        vbox4.getChildren().addAll(new Label("Category:"), categorySelect); // add the needed components for the type label

        // add submit and cancel buttons
        ButtonType closeDialog = new ButtonType("Submit", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType cancelDialog = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelDialog, closeDialog);

        hbox.getChildren().addAll(vbox1, vbox2, vbox3, vbox4); // add the layouts

        dialog.setGraphic(hbox); /// add the layout to the dialog

        Optional<ButtonType> result = dialog.showAndWait(); // to consider which button was clicked

        // ensure dialog closed properly and determine next action
        if (result.isPresent()) {
            if (result.get() == closeDialog){ // if the submit button was clicked
                try {
                    // get the date and format it as needed
                    LocalDate date1 = datePicker.getValue();
                    LocalDateTime date = date1.atTime(LocalTime.now());

                    // add the properties to the date
                    newAction.append("type", typeSelect.getValue().toString().toLowerCase());
                    newAction.append("date", date);
                    newAction.append("category", categorySelect.getValue().toString());
                    newAction.append("sum", Double.valueOf(sumSelect.getText()));

                    // update the user's actions in the database
                    DatabaseTools.insertAction(this.doc.get("username").toString(), newAction);

                    // update local document
                    this.doc = DatabaseTools.Authenticate(this.doc.get("username").toString(), this.doc.get("passwd").toString());

                    Personalize(); // update the screen
                }catch (Exception e){ // if a misleading info was entered, inform the user
                    Alert alert = new Alert(Alert.AlertType.ERROR); // instantiate an error alert

                    alert.setTitle("An error occurred!"); // add title

                    // add informative texts
                    alert.setHeaderText("Oops... Something went wrong!");
                    alert.setContentText("Please check the Details you entered!");

                    alert.showAndWait();
                }
            }
            dialog.close(); // in any case, close the dialog if one of the buttons was clicked
        }
    }

    public void openSettings(ActionEvent event){
        SettingsDialog dialog = new SettingsDialog(this.doc);
        dialog.showAndWait();
        this.doc = dialog.getDoc();
        Personalize();
    }

    public void statsPage(ActionEvent event) throws Exception{
        FXMLLoader loader = updateScene("/views/stats.fxml", event);
        StatsController controller = loader.getController();
        controller.setDoc(this.doc); // put on the different components that match the user
        controller.insertValues(StatsController.TIME_OPTIONS); // set the ComboBox options
        controller.insertValues1(new String[]{"outcome", "income"}); // add the possible action types
        controller.actionType.setValue("outcome");
        controller.setBoxValue(DEFAULT_VAL); // start at a default value
        controller.Personalize();
    }


}
