import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.bson.Document;

import java.util.HashMap;

public class ChangePropertyDialog extends Dialog<Document> {

    public TextField inp;
    public PasswordField confirmInp;
    public String prop;
    public ButtonType closeDialog, cancelDialog;

    public ChangePropertyDialog(String property){

        this.prop = property;
        VBox layout = new VBox(); // main layout
        layout.setSpacing(20); // space the elements inside

        HBox inputBox = new HBox();
        inputBox.setSpacing(20); // space the elements inside
        Label desc = new Label("Enter your new " + property + ":");
        inp = (property.equals("password")) ? new PasswordField() : new TextField();
        inputBox.getChildren().addAll(desc, inp); // add the widgets to the layout
        layout.getChildren().add(inputBox);

        if (property.equals("password")){ // add validation only if the field is password
            HBox confirmBox = new HBox(); // create another layout
            confirmBox.setSpacing(20); // space the elements inside
            Label confirm = new Label("Confirm your password:"); // add input indicator
            confirmInp = new PasswordField(); // hide the characters typed
            confirmBox.getChildren().addAll(confirm, confirmInp); // add the widgets to the layout

            Label strength = new Label(); // label to indicate the user on his password strength

            inp.setOnKeyPressed(keyEvent -> {
                int str = HelperMethods.PasswordStrength(inp.getText()); // calculate password strength
                String s = (str % 10 == 2) ? "Length okay, " : "Too short, ";

                if (str / 10 == 3){ // very strong
                    s += "Strong enough!";
                    strength.setTextFill(Color.GREEN);
                }else if (str / 10 == 2){ // medium strength
                    s += "Medium strength!";
                    strength.setTextFill(Color.ORANGE);
                }else { // weak password
                    s += "Very weak!";
                    strength.setTextFill(Color.RED);
                }

                strength.setText(s);
            });


            layout.getChildren().addAll(confirmBox, strength);
        }

        closeDialog = new ButtonType("Submit", ButtonBar.ButtonData.CANCEL_CLOSE);
        cancelDialog = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        this.getDialogPane().getButtonTypes().addAll(cancelDialog, closeDialog); // add cancel and submit buttons
        this.setGraphic(layout); // add the layout to the dialog
    }

    public Document getChange(){
        Document doc = new Document();
        doc.put("type", this.prop);
        doc.put("newValue", inp.getText());
        if (this.prop.equals("password")){
            doc.put("opPass", confirmInp.getText());
        }

        return doc;
    }


}
