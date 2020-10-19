import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import org.bson.Document;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;

public class Entrance extends Application{

    final static private int WIDTH = 600;
    final static private int HEIGHT = 600;
    protected Stage stage;

    @Override
    public void start (Stage primaryStage) throws Exception{

        this.stage = primaryStage; // to access the stage from other classes

        InputStream iconStream = getClass().getResourceAsStream("/imgs/icon.png"); // read the icon file
        stage.getIcons().add(new Image(iconStream)); // add the icon

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // get screen size
        primaryStage.setX(screenSize.width/3); // X position of the upper left corner
        primaryStage.setY(screenSize.height/4); // Y position of the upper left corner
        primaryStage.setWidth(WIDTH); // window width
        primaryStage.setHeight(HEIGHT); // window height

        primaryStage.setTitle("Welcome to Expense Tracker!"); // set title

        FXMLLoader loader = new FXMLLoader(); // responsible for handling the fxml to UI process
        URL xmlUrl = getClass().getResource("/views/entrance.fxml"); // get fxml source file
        loader.setLocation(xmlUrl); // set conversion source

        Parent root = loader.load(); // load the jfx structure

        DatabaseTools.createDB();

        // show the scene
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}