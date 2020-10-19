import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import javafx.event.ActionEvent;
import javafx.event.Event;

import org.bson.Document;
import java.net.URL;

public class EntranceController extends updatable { // to update the scene

    @FXML
    private Button signIn, signUp, next1, next2;

    @FXML
    private TextField username1, username2, password1, password2;

    @FXML
    private Label pass1Indicator;

    @FXML
    public void signInClicked(ActionEvent event) throws Exception{
        updateScene("/views/signin.fxml", event);
    }

    @FXML
    public void signUpClicked(ActionEvent event) throws Exception{
        updateScene("/views/signup.fxml", event);
    }

    @FXML
    public void home(ActionEvent event) throws Exception{
        updateScene("/views/entrance.fxml", event);
    }

    @FXML
    public void homeScreen(ActionEvent event){
        if (event.getSource() == next1){
           try {
               typedSignUp(event);
           }catch (Exception e){
               e.printStackTrace();
           }
        }else if (event.getSource() == next2){
            typedSignIn(event);
        }
    }

    @FXML
    public void pass(Event event){
        if (event != null && event.getSource() == password1){ // make sure event is not null
            indicatePass(); // respond to the user while he types
        }
    }

    public void typedSignUp(ActionEvent event) throws Exception{
        // register the user in the Database
        if (DatabaseTools.register(username1.getText(), password1.getText())){ // sign the user in once he has signed up
            // try to authenticate the user with the credentials received
            Document document = DatabaseTools.Authenticate(username1.getText(), password1.getText());

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // get the scene
            FXMLLoader loader = new FXMLLoader(); // responsible for handling the fxml to UI process
            URL xmlUrl = getClass().getResource("/views/homeScreen.fxml"); // get fxml source file
            loader.setLocation(xmlUrl); // set conversion source
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            HomeScreenController controller = loader.getController();
            controller.setDoc(document); // put on the different components that match the user
            controller.Personalize();
            MessageBox("Welcome!");
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR); // instantiate an error alert

            alert.setTitle("An error occurred!"); // add title

            // add informative texts
            alert.setHeaderText("Oops... Something went wrong!");
            alert.setContentText("Username already exists or no internet connection!");

            alert.showAndWait();
        }


    }

    public void typedSignIn(ActionEvent event){
        try {
            // try to authenticate the user with the credentials received
            Document document = DatabaseTools.Authenticate(username2.getText(), password2.getText());
            if (document == null){ // if user or password were wrong
                Alert alert = new Alert(Alert.AlertType.ERROR); // instantiate an error alert

                alert.setTitle("An error occurred!"); // add title

                // add informative texts
                alert.setHeaderText("Oops... Something went wrong!");
                alert.setContentText("Please check your Username and Password");

                alert.showAndWait();
                return; // stay in the entrance screen
            }
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // get the scene
            FXMLLoader loader = new FXMLLoader(); // responsible for handling the fxml to UI process
            URL xmlUrl = getClass().getResource("/views/homeScreen.fxml"); // get fxml source file
            loader.setLocation(xmlUrl); // set conversion source
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            HomeScreenController controller = loader.getController();
            controller.setDoc(document); // put on the different components that match the user
            controller.Personalize();
            MessageBox("Welcome Back!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MessageBox(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // instantiate an information alert

        alert.setTitle("Hey There"); // add title

        // add informative texts
        alert.setHeaderText(message);
        alert.setContentText("Glad to have you on board!");

        alert.showAndWait();
    }

    private void indicatePass(){
        String s = ""; // the text to indicate password strength to the user in real time
        int strength = HelperMethods.PasswordStrength(password1.getText()); // calculate password strength
        if (strength % 10 == 2){ // check length
            s = "Length okay, ";
        }else {
            s = "Too short, ";
        }

        if (strength / 10 == 3){ // very strong
            s += "Strong enough!";
            pass1Indicator.setTextFill(Color.GREEN);
        }else if (strength / 10 == 2){ // medium strength
            s += "Medium strength!";
            pass1Indicator.setTextFill(Color.ORANGE);
        }else { // weak password
            s += "Very weak!";
            pass1Indicator.setTextFill(Color.RED);
        }

        pass1Indicator.setText(s);
    }
}