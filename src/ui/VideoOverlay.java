package ui;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class VideoOverlay extends GridPane{
    private static int SPACING = 10;

    private HBox switchHBox;
    private ToggleButton switchButton;
    private ToggleButton recordButton;
    private Label facingText;

    private StackPane switchStack;
    private StackPane recordStack;

    public VideoOverlay(){
        switchHBox = new HBox();
        switchButton = new ToggleButton();
        recordButton = new ToggleButton();
        switchStack = new StackPane();
        recordStack = new StackPane();
        facingText = new Label();

        switchButton.getStyleClass().add("toggle-switch");
        recordButton.getStyleClass().add("toggle-record");
        facingText.getStyleClass().add("text-facing");

        switchHBox.getChildren().addAll(switchButton, facingText);
        switchHBox.setSpacing(SPACING);
        switchHBox.setAlignment(Pos.BOTTOM_LEFT);

        add(switchHBox, 0, 1);
        add(recordButton, 1, 1);

        setHgrow(switchHBox, Priority.ALWAYS);
        setHgrow(recordButton, Priority.ALWAYS);
        setVgrow(switchHBox, Priority.ALWAYS);
        setVgrow(recordButton, Priority.ALWAYS);

        setValignment(switchHBox, VPos.BOTTOM);
        setValignment(recordButton, VPos.BOTTOM);
        setHalignment(switchHBox, HPos.LEFT);
        setHalignment(recordButton, HPos.RIGHT);

        setPadding(new Insets(SPACING, SPACING, SPACING, SPACING));


        switchButton.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switchButton.setStyle("-fx-graphic: url('ui/video/Refresh-50-Glow.png');");
            }
        });
        switchButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switchButton.setStyle("-fx-graphic: url('ui/video/Refresh-50.png');");
            }
        });


        recordButton.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                recordButton.setStyle("-fx-graphic: url('ui/video/Aperture-50-Glow.png');");
            }
        });
        recordButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                recordButton.setStyle("-fx-graphic: url('ui/video/Aperture-50.png');");
            }
        });


        switchButton.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (switchButton.isSelected())
                    facingText.setStyle("-fx-graphic: url('ui/video/Facing-Fwd.png');");
                else
                    facingText.setStyle("-fx-graphic: url('ui/video/Facing-Bck.png');");
            }
        });

        getStyleClass().add("video-background");
    }
}
