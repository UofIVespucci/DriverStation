package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class VideoOverlay extends AnchorPane{
    private static int SPACING = 10;

    private Label batteryLbl;
    private Label recordLbl;

    public VideoOverlay() {
        batteryLbl = new Label();
        recordLbl = new Label();

        batteryLbl.getStyleClass().add("video-battery-full");

        getChildren().addAll(batteryLbl, recordLbl);
        setTopAnchor(recordLbl, 10.0);
        setRightAnchor(recordLbl, 10.0);
        setBottomAnchor(batteryLbl, 10.0);
        setRightAnchor(batteryLbl, 10.0);

        setPadding(new Insets(SPACING, SPACING, SPACING, SPACING));
    }

    protected void setRecording(Boolean isRecording) {
        if (isRecording) {recordLbl.getStyleClass().addAll("video-record");}
        else {recordLbl.getStyleClass().clear();}
    }

    protected void setBatteryLbl(BatteryStatus batteryStatus){
        batteryLbl.getStyleClass().clear();
        switch (batteryStatus){
        case FULL:
            batteryLbl.getStyleClass().addAll("video-battery-full");
            break;
        case QUARTER:
            batteryLbl.getStyleClass().addAll("video-battery-quarter");
            break;
        case HALF:
            batteryLbl.getStyleClass().addAll("video-battery-half");
            break;
        case LOW:
            batteryLbl.getStyleClass().addAll("video-battery-low");
            break;
        case CRITICAL:
            batteryLbl.getStyleClass().addAll("video-battery-critical");
            break;
        }
    }
}
