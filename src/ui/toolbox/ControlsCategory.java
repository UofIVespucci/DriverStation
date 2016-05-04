package ui.toolbox;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

/**
 * Created by Lance on 5/1/2016.
 */
public class ControlsCategory extends GridPane {
    public static int ALLWIDTH = 125;

    Label label;
    GridPane coreGrid;
    String categoryName;
    public ArrayList<Node> controls = new ArrayList<>();

    public ControlsCategory(String getCategoryName) {
        categoryName = getCategoryName;
    }

    public void initStructure() {
        label = new Label(categoryName);
        coreGrid = new GridPane();

        label.setAlignment(Pos.CENTER);
        label.setMinWidth(ALLWIDTH);
        label.getStyleClass().add("tool-label");

        coreGrid.setVgap(5);
        coreGrid.setPrefWidth(ALLWIDTH);
        coreGrid.getStyleClass().add("tool-item");

        add(label, 0, 0);
        add(coreGrid, 0, 1);
        getStyleClass().add("tool-item-box");

        for (int i = 0; i < controls.size(); i++) {
            try { ((Control) controls.get(i)).setMinWidth(ALLWIDTH); }
            catch (ClassCastException e) { /*This control does not support a setMaxWidth method*/ }
            coreGrid.add(controls.get(i), 0, i);
        }
    }

    public int addControl(Node control, EventHandler<ActionEvent> keyEvent) {
        controls.add(control);
        if (control instanceof ToggleButton) {
            ((ToggleButton) control).setOnAction(keyEvent);
            control.getStyleClass().add("tool-button");

            return 0; //Successfully added eventHandler
        } else if (control instanceof Button) {
            ((Button) control).setOnAction(keyEvent);
            control.getStyleClass().add("tool-button");
        } else if (control instanceof Slider) {
            control.getStyleClass().add("tool-slider");
        }
        return 1; //Unknown control, could not add proper eventHandler
    }
}
