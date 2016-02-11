package ui;

import com.Main;
import com.github.sarxos.webcam.WebcamPicker;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Collection;

public class Toolbox extends ScrollPane{
    public static int ALLWIDTH = 125;

    private VBox toolsVBox;
    private ToggleButton lightWhiteTB;
    private ToggleButton lightBlueTB;
    private ToggleButton cameraSwitchTB;
    private ToggleButton recordTB;
    private Slider brightnessSlider;
    private Image sunImg;
    private ImageView sunView;
    private Image moonImg;
    private ImageView moonView;

    private Label lightColorLbl;
    private Label cameraSwitchLbl;
    private Label brightnessLbl;
    private Label recordLbl;
    private Label wcLabel;
    private Label scpLabel;

    private GridPane lightColorGrid;
    private GridPane lightColorOver;
    private GridPane cameraSwitchGrid;
    private GridPane cameraSwitchOver;
    private GridPane brightnessGrid;
    private GridPane brightnessOver;
    private GridPane recordGrid;
    private GridPane recordOver;
    private GridPane wcGrid;
    private GridPane wcOver;
    private GridPane scpGrid;
    private GridPane scpOver;

    private AnchorPane brightnessAnchor;

    private WebcamDropDown wcComboBox;
    private Button wcConnectTB;
    private Button wcRefreshTB;

    public Toolbox(){
        toolsVBox = new VBox();
        //Init Components for Brightness Pane
        brightnessOver      = new GridPane();
        brightnessGrid      = new GridPane();
        brightnessAnchor    = new AnchorPane();
        brightnessLbl       = new Label ("HEADLIGHT");
        brightnessSlider    = new Slider();
        sunImg              = new Image("ui/toolbox/Sun-50i.png");
        moonImg             = new Image("ui/toolbox/Moon-50i.png");
        sunView             = new ImageView();
        moonView            = new ImageView();

        //Init Components for Light Color Selector Pane
        lightColorOver      = new GridPane();
        lightColorGrid      = new GridPane();
        lightColorLbl       = new Label ("LIGHT COLOR");
        lightWhiteTB        = new ToggleButton("WHITE LIGHT");
        lightBlueTB         = new ToggleButton("RED LIGHT");

        //Init Components for Camera Switch Pane
        cameraSwitchOver    = new GridPane();
        cameraSwitchGrid    = new GridPane();
        cameraSwitchLbl     = new Label("CAMERA");
        cameraSwitchTB      = new ToggleButton("SWITCH CAMERA");

        //Init Components for Recording Pane
        recordOver          = new GridPane();
        recordGrid          = new GridPane();
        recordLbl           = new Label ("RECORD");
        recordTB            = new ToggleButton("RECORD");

        //Init Components for Webcam Pane
        wcGrid = new GridPane();
        wcOver = new GridPane();
        wcLabel = new Label("WEBCAM");
        wcComboBox = new WebcamDropDown();
        wcConnectTB = new Button("CONNECT");
        wcRefreshTB = new Button("REFRESH");

        //Init Components for Comms Pane
        scpGrid = new GridPane();
        scpOver = new GridPane();
        scpLabel = new Label("COMMUNICATION");


        initLabels(Pos.CENTER, lightColorLbl, cameraSwitchLbl, brightnessLbl, recordLbl, wcLabel, scpLabel);
        initLightColor();
        initCameraSwitch();
        initBrightness();
        initRecordSwitch();
        initWebcamDropDown();
        initRobotPane();

        toolsVBox.getChildren().addAll(wcOver, lightColorOver, brightnessOver, cameraSwitchOver, recordOver, scpOver);
        toolsVBox.getStyleClass().add("toolbox");

        toolsVBox.setVgrow(lightColorOver, Priority.ALWAYS);
        toolsVBox.setVgrow(cameraSwitchOver, Priority.ALWAYS);
        toolsVBox.setVgrow(brightnessOver, Priority.ALWAYS);
        toolsVBox.setVgrow(recordOver, Priority.ALWAYS);
        toolsVBox.setVgrow(scpOver, Priority.ALWAYS);

        getStyleClass().add("tool-scrollpane");
        toolsVBox.setSpacing(5);

        setContent(toolsVBox);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }

    private void initLabels(Pos p, Label... labels)
    {
        for (Label l: labels) {
            l.setAlignment(p);
            l.getStyleClass().add("tool-label");
        }
    }

    private void initWebcamDropDown()
    {
        wcGrid.add(wcComboBox, 0, 1);
        wcGrid.add(wcConnectTB, 0, 2);
        wcGrid.add(wcRefreshTB, 0, 3);
        wcGrid.setVgap(5);
        wcGrid.setPrefWidth(ALLWIDTH);

        wcOver.add(wcLabel, 0, 0);
        wcOver.add(wcGrid, 0, 1);

        wcGrid.getStyleClass().add("tool-item");
        wcOver.getStyleClass().add("tool-item-box");

        wcComboBox.setPrefWidth(ALLWIDTH);
        wcLabel.setPrefWidth(ALLWIDTH);

        wcConnectTB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.guiManager.setWebcam(wcComboBox.getSelected());
            }
        });

        wcRefreshTB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                wcComboBox.getWebcams();
            }
        });
    }

    private void initLightColor(){
        lightColorGrid.add(lightWhiteTB, 0, 1);
        lightColorGrid.add(lightBlueTB, 0, 2);
        lightColorGrid.setVgap(5);
        lightColorGrid.setPrefWidth(ALLWIDTH);

        lightColorOver.add(lightColorLbl, 0, 0);
        lightColorOver.add(lightColorGrid, 0, 1);

        lightColorGrid.getStyleClass().add("tool-item");
        lightColorOver.getStyleClass().add("tool-item-box");

        lightWhiteTB.prefWidthProperty().bind(lightColorGrid.widthProperty());
        lightWhiteTB.getStyleClass().add("tool-button");
        lightBlueTB.prefWidthProperty().bind(lightColorGrid.widthProperty());
        lightBlueTB.getStyleClass().add("tool-button");

        lightColorLbl.minWidthProperty().bind(lightColorGrid.widthProperty());
    }

    private void initCameraSwitch(){
        cameraSwitchGrid.add(cameraSwitchTB, 0, 1);
        cameraSwitchGrid.setVgap(5);
        cameraSwitchGrid.setPrefWidth(ALLWIDTH);

        cameraSwitchOver.add(cameraSwitchLbl, 0, 0);
        cameraSwitchOver.add(cameraSwitchGrid, 0, 1);

        cameraSwitchGrid.getStyleClass().add("tool-item");
        cameraSwitchOver.getStyleClass().add("tool-item-box");

        cameraSwitchTB.prefWidthProperty().bind(cameraSwitchGrid.widthProperty());
        cameraSwitchTB.getStyleClass().add("tool-button");

        cameraSwitchLbl.minWidthProperty().bind(cameraSwitchGrid.widthProperty());
    }

    private void initBrightness(){
        brightnessGrid.add(brightnessAnchor, 0, 1);
        brightnessGrid.add(brightnessSlider, 0, 2);
        brightnessGrid.setVgap(5);
        brightnessGrid.setPrefWidth(ALLWIDTH);

        brightnessAnchor.getChildren().addAll(sunView, moonView);
        brightnessAnchor.setRightAnchor(moonView, 0.0);
        brightnessAnchor.setTopAnchor(sunView, 0.0);
        brightnessAnchor.setLeftAnchor(sunView, 0.0);
        brightnessAnchor.setTopAnchor(moonView, 0.0);

        brightnessOver.add(brightnessLbl, 0, 0);
        brightnessOver.add(brightnessGrid, 0, 1);

        sunView.setImage(sunImg);
        moonView.setImage(moonImg);

        brightnessGrid.getStyleClass().add("tool-item");
        brightnessOver.getStyleClass().add("tool-item-box");
        brightnessSlider.getStyleClass().add("tool-button");

        brightnessLbl.minWidthProperty().bind(brightnessGrid.widthProperty());
    }

    private void initRecordSwitch(){
        recordGrid.add(recordTB, 0, 1);
        recordGrid.setVgap(5);
        recordGrid.setPrefWidth(ALLWIDTH);

        recordOver.add(recordLbl, 0, 0);
        recordOver.add(recordTB, 0, 1);

        recordGrid.getStyleClass().add("tool-item");
        recordOver.getStyleClass().add("tool-item-box");

        recordTB.prefWidthProperty().bind(cameraSwitchGrid.widthProperty());
        recordTB.getStyleClass().add("tool-button");

//        recordLbl.minWidthProperty().bind(recordGrid.widthProperty());
        recordLbl.setMinWidth(ALLWIDTH);
    }

    private void initRobotPane(){
//        scpGrid.add(scp, 0, 1);
        scpGrid.setVgap(5);
        scpGrid.setPrefWidth(ALLWIDTH);

        scpOver.add(scpLabel, 0, 0);
        scpOver.add(scpGrid, 0, 1);

        scpGrid.getStyleClass().add("tool-item");
        scpOver.getStyleClass().add("tool-item-box");

        scpLabel.setMinWidth(ALLWIDTH);
    }

    protected void setScp(serial.SerialConnectPanel scp) {
        scpGrid.add(scp, 0, 1);
    }
}
