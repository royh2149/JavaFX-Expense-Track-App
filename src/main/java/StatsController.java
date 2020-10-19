import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.time.LocalDate;

public class StatsController extends updatable { // to update the scene

    @FXML
    private PieChart mainChart;

    @FXML
    protected ComboBox timespan, actionType;

    private Document doc;

    public static final String[] TIME_OPTIONS = {"Everything", "Last Week", "Last Month", "Last Year"};

    public void setDoc(Document doc){
        this.doc = doc;
    }

    public void setBoxValue(String val){
        this.timespan.setValue(val);
    }

    public void insertValues(String[] vals){
        for (String s: vals){
            this.timespan.getItems().add(s);
        }
    }

    public void insertValues1(String[] vals){
        for (String s: vals){
            this.actionType.getItems().add(s);
        }
    }

    @FXML
    public void home(ActionEvent event) throws Exception {
        FXMLLoader loader = updateScene("/views/homeScreen.fxml", event);

        HomeScreenController controller = loader.getController();
        controller.setDoc(this.doc); // put on the different components that match the user
        controller.Personalize();
    }

    public void Personalize(){
        String start = this.timespan.getValue().toString(); // get the date to begin the calculation from
        LocalDateTime startDate = HelperMethods.getStartDate(start); // set the starting date
        setGraph(this.actionType.getValue().toString(), startDate); // create the graph
    }



    public void setGraph(String type, LocalDateTime startDate){
        this.mainChart.getData().clear(); // reset the chart
        ArrayList<Document> actions = (ArrayList<Document>)this.doc.get("actions"); // get the actions property
        Set<String> keys = HomeScreenController.ctColors.keySet(); // get all the categories
        HashMap<String, Double> stats = new HashMap<>(); // create the stats hashmap

        for (String name : keys){ // add a key for each category
            stats.put(name, 0.0);
        }

        if (actions == null){
            this.mainChart.setTitle("No Actions Yet!");
        }

        for (Document action : actions){ // check every action
            if (action.get("type").toString().equals(type)){ // determine whether the current action is income or outcome
                String category = action.get("category").toString(); // get the current category

                // make sure the action is in the requested timespan
                if (HelperMethods.convertToLocalDateTimeViaInstant((Date) action.get("date")).isAfter(startDate)){
                    // if all the terms apply, update the amount of money in the category
                    stats.put(category , stats.get(category) + (Double)action.get("sum"));
                }
            }
        }

        for (String name : keys){
            if (stats.get(name) == 0.0){ // it is redundant to display categories without influence
                continue;
            }
            PieChart.Data slice = new PieChart.Data(name, stats.get(name)); // create the slice
            this.mainChart.getData().add(slice); // add it to the chart
            slice.setName(name + " - " + stats.get(name)); // view the category name and amount of money
        }
        this.mainChart.setLegendVisible(false); // it is not necessary to show the legend
    }
}
