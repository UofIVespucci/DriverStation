package ui;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class VideoOverlay extends GridPane{
    private ToggleButton switchButton;
    private ToggleButton recordButton;

    public VideoOverlay(){
        switchButton = new ToggleButton();
        recordButton = new ToggleButton();

        switchButton.getStyleClass().add("toggle-switch");
        recordButton.getStyleClass().add("toggle-record");

        add(switchButton, 0, 1);
        add(recordButton, 1, 1);

        setHgrow(switchButton, Priority.ALWAYS);
        setHgrow(recordButton, Priority.ALWAYS);
        setVgrow(switchButton, Priority.ALWAYS);
        setVgrow(recordButton, Priority.ALWAYS);

        setValignment(switchButton, VPos.TOP);
        setValignment(recordButton, VPos.TOP);
        setHalignment(switchButton, HPos.LEFT);
        setHalignment(recordButton, HPos.RIGHT);

        getStyleClass().add("video-background");
    }
}
