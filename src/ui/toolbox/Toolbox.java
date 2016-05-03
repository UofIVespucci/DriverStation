package ui.toolbox;

import com.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import serial.SerialConnectPanel;
import ui.RecordingManager;
import ui.RobotSpeed;
import ui.WCFXPanel;
import ui.WebcamDropDown;

public class Toolbox extends ScrollPane {
    ControlsCategory cameraControl;
    ControlsCategory connectControl;
    ControlsCategory cameraFaceControl;
    ControlsCategory lightControl;
    ControlsCategory recordControl;
    ControlsCategory speedControl;
    WCFXPanel wcfxPanel;

    public Toolbox(SerialConnectPanel scp,
                   RecordingManager rManager,
                   WCFXPanel getwcfxPanel,
                   RobotSpeed robotSpeed) {
        VBox toolsVBox      = new VBox();
        wcfxPanel = getwcfxPanel;

        cameraControl      = new ControlsCategory("CAMERA");
        connectControl     = new ControlsCategory("COMMUNICATION");
        cameraFaceControl  = new ControlsCategory("CAMERA FACE");
        lightControl       = new ControlsCategory("HEADLIGHT");
        recordControl      = new ControlsCategory("RECORD");
        speedControl       = new ControlsCategory("MOTOR SPEED");

        initCameraControl();
        initCameraFaceControl();
        initLightControl();
        initRecordControl(rManager, wcfxPanel);
        initSpeedControl(robotSpeed);
        connectControl.addControl(scp, null);

        toolsVBox.getChildren().addAll(
                cameraControl,
                connectControl,
                cameraFaceControl,
                lightControl,
                recordControl,
                speedControl
        );

        toolsVBox.getStyleClass().add("toolbox");
        for (Node n : toolsVBox.getChildren()) {
            VBox.setVgrow(n, Priority.ALWAYS);
            ((ControlsCategory) n).initStructure();
        }
        toolsVBox.setSpacing(5);
        setMinHeight(0);

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
                wcfxPanel.setWebcam(wcDropDown.getSelected());
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

    private void initRecordControl(RecordingManager rManager, WCFXPanel wcfxPanel) {
        ToggleButton recordTB = new ToggleButton("RECORD");

        recordControl.addControl(recordTB, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!rManager.getRecordingStatus() && wcfxPanel.getStreamingStatus()) {
                    rManager.record("TestFile.mp4", wcfxPanel.stillProp, 4);
                }
                else if (rManager.getRecordingStatus()) rManager.stopRecording();
                else System.err.println("Error in recording request, wcfxPanel probably isn't streaming");
            }
        });
    }

    private void initSpeedControl(RobotSpeed robotSpeed) {
        Slider speedSlider = new Slider(25, 75, 50);

        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                robotSpeed.setSpeed(newValue.shortValue());
            }
        });

        speedControl.addControl(speedSlider, null);
    }
}
