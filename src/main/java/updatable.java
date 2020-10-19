import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public abstract class updatable { // all those who inherit would be able to update the application scene

    public FXMLLoader updateScene(String filename, ActionEvent event) throws Exception{
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // get the scene
        FXMLLoader loader = new FXMLLoader(); // responsible for handling the fxml to UI process
        URL xmlUrl = getClass().getResource(filename); // get fxml source file
        loader.setLocation(xmlUrl); // set conversion source


        Parent root = loader.load(); // load the scene
        stage.setScene(new Scene(root)); // set the scene on stage

        return loader;
    }
}
