package ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class ButtonSelector extends HBox{
    private static ToggleButton cameraButton;
    private static ToggleButton debugButton;
    private static ToggleButton mapButton;
    private static ToggleGroup toggleGroup;
    private final Region rect = new Region();
    private static StackPane cameraStack;
    private static StackPane debugStack;
    private static StackPane mapStack;

    public ButtonSelector()
    {
        cameraButton = new ToggleButton();
        debugButton = new ToggleButton();
        mapButton = new ToggleButton();
        toggleGroup = new ToggleGroup();

        cameraStack = new StackPane();
        debugStack = new StackPane();
        mapStack = new StackPane();

//        cameraStack.getChildren().addAll(cameraButton, rect);
        cameraStack.getChildren().add(cameraButton);
        debugStack.getChildren().add(debugButton);
        mapStack.getChildren().add(mapButton);

//        rect.setArcHeight(0);
//        rect.setArcWidth(0);
//        rect.setFill(Color.RED);
        rect.getStyleClass().add("carbon-button");

        cameraButton.getStyleClass().add("toggle-camera");
        mapButton.getStyleClass().add("toggle-map");
        debugButton.getStyleClass().add("toggle-debug");
        getChildren().addAll(cameraStack, mapStack, debugStack);

        setHgrow(cameraStack, Priority.ALWAYS);
        setHgrow(mapStack, Priority.ALWAYS);
        setHgrow(debugStack, Priority.ALWAYS);

        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.TOP_CENTER);

        cameraStack.setMaxWidth(Double.MAX_VALUE);
        mapStack.setMaxWidth(Double.MAX_VALUE);
        debugStack.setMaxWidth(Double.MAX_VALUE);

        cameraButton.setMaxWidth(Double.MAX_VALUE);
        mapButton.setMaxWidth(Double.MAX_VALUE);
        debugButton.setMaxWidth(Double.MAX_VALUE);


        cameraButton.setContentDisplay(ContentDisplay.TOP);
        mapButton.setContentDisplay(ContentDisplay.TOP);
        debugButton.setContentDisplay(ContentDisplay.TOP);

        cameraButton.setToggleGroup(toggleGroup);
        mapButton.setToggleGroup(toggleGroup);
        debugButton.setToggleGroup(toggleGroup);


//        showText(true);
        setPadding(new Insets(0, 0, 0, 0));
//        addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                showText(true);
//            }
//        });
//        addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                showText(false);
//            }
//        });
    }

    public void showText(boolean show){
        if (show){
            cameraButton.setText("Camera");
            mapButton.setText("Map");
            debugButton.setText("Debug");
        }
        else {
            cameraButton.setText("");
            mapButton.setText("");
            debugButton.setText("");
        }
    }
}
