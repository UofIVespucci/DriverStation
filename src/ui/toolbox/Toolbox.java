package ui.toolbox;

import com.Main;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import serial.SerialConnectPanel;
import ui.WebcamDropDown;

public class Toolbox extends ScrollPane {
    ControlsCategory cameraControl;
    ControlsCategory connectControl;
    ControlsCategory cameraFaceControl;
    ControlsCategory lightControl;

    public Toolbox(SerialConnectPanel scp) {
        VBox toolsVBox      = new VBox();

        cameraControl      = new ControlsCategory("CAMERA");
        connectControl     = new ControlsCategory("COMMUNICATION");
        cameraFaceControl  = new ControlsCategory("CAMERA FACE");
        lightControl       = new ControlsCategory("HEADLIGHT");

        initCameraControl();
        initCameraFaceControl();
        initLightControl();
        connectControl.addControl(scp, null);

        toolsVBox.getChildren().addAll(cameraControl, connectControl, cameraFaceControl, lightControl);
        toolsVBox.getStyleClass().add("toolbox");
        for (Node n : toolsVBox.getChildren()) {
            toolsVBox.setVgrow(n, Priority.ALWAYS);
            ((ControlsCategory) n).initStructure();
        }
        toolsVBox.setSpacing(5);
        toolsVBox.setMinHeight(0);
        toolsVBox.maxHeightProperty().bind(heightProperty());
        setMinHeight(0);
//        maxHeightProperty().bind(windowHeightProp);

        getStyleClass().add("tool-scrollpane");
        setContent(toolsVBox);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }

    private void initCameraControl(){
        WebcamDropDown wcDropDown = new WebcamDropDown();

        cameraControl.addControl(new WebcamDropDown(), null);
        cameraControl.addControl(new Button("CONNECT"), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.guiManager.setWebcam(wcDropDown.getSelected());
            }
        });
        cameraControl.addControl(new Button("REFRESH"), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                wcDropDown.getWebcams();
            }
        });
    }

    private void initCameraFaceControl() {
        ToggleButton cameraSwitchTB = new ToggleButton("SWITCH CAMERA");

        cameraFaceControl.addControl(cameraSwitchTB, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.guiManager.cameraSwitch(cameraSwitchTB.isSelected());
            }
        });
    }

    private void initLightControl() {
        ToggleButton brightnessTB = new ToggleButton("LED OFF");
        lightControl.addControl(brightnessTB, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.guiManager.ledBrightness(brightnessTB.isSelected() ? 0.0 : 1.0);
                brightnessTB.setText(brightnessTB.isSelected() ? "LED ON" : "LED OFF");
            }
        });
    }
}