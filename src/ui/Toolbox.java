package ui;

import com.github.sarxos.webcam.WebcamPicker;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class Toolbox extends HBox{
    public static int ALLWIDTH = 100;

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
    private WebcamPicker picker;

    private Label lightColorLbl;
    private Label cameraSwitchLbl;
    private Label brightnessLbl;
    private Label recordLbl;

    private GridPane lightColorGrid;
    private GridPane lightColorOver;
    private GridPane cameraSwitchGrid;
    private GridPane cameraSwitchOver;
    private GridPane brightnessGrid;
    private GridPane brightnessOver;
    private GridPane recordGrid;
    private GridPane recordOver;

    private AnchorPane brightnessAnchor;

    public Toolbox(){
        toolsVBox = new VBox();

        lightWhiteTB = new ToggleButton("WHITE LIGHT");
        lightBlueTB = new ToggleButton("RED LIGHT");
        cameraSwitchTB = new ToggleButton("SWITCH CAMERA");
        recordTB = new ToggleButton("RECORD");
        brightnessSlider = new Slider();

        sunImg = new Image("ui/toolbox/Sun-50i.png");
        sunView = new ImageView();
        moonImg = new Image("ui/toolbox/Moon-50i.png");
        moonView = new ImageView();

        lightColorLbl = new Label  ("HEADLIGHT COLOR");
        lightColorLbl.setAlignment(Pos.CENTER);
        cameraSwitchLbl = new Label("CAMERA DIRECTION");
        cameraSwitchLbl.setAlignment(Pos.CENTER);
        brightnessLbl = new Label  ("LED BRIGHTNESS");
        brightnessLbl.setAlignment(Pos.CENTER);
        recordLbl = new Label      ("RECORD");
        recordLbl.setAlignment(Pos.CENTER);

        lightColorGrid = new GridPane();
        lightColorOver = new GridPane();
//        lightColorGrid.prefWidthProperty().bind(this.widthProperty());
        cameraSwitchGrid = new GridPane();
        cameraSwitchOver = new GridPane();
        brightnessGrid = new GridPane();
        brightnessOver = new GridPane();
        recordGrid = new GridPane();
        recordOver = new GridPane();

        brightnessAnchor = new AnchorPane();

        lightColorGrid.add(lightWhiteTB, 0, 1);
        lightColorGrid.add(lightBlueTB, 0, 2);
        lightColorGrid.setVgap(5);
        lightColorGrid.setPrefWidth(ALLWIDTH);
//        lightColorGrid.maxWidthProperty().bind(toolsVBox.widthProperty());
//        lightColorGrid.prefWidthProperty().bind(lightColorOver.widthProperty());
        cameraSwitchGrid.add(cameraSwitchTB, 0, 1);
//        cameraSwitchGrid.prefWidthProperty().bind(cameraSwitchOver.widthProperty());
        cameraSwitchGrid.setVgap(5);
        cameraSwitchGrid.setPrefWidth(ALLWIDTH);
        brightnessGrid.add(brightnessAnchor, 0, 1);
        brightnessGrid.add(brightnessSlider, 0, 2);
//        brightnessGrid.prefWidthProperty().bind(brightnessOver.widthProperty());
        brightnessGrid.setVgap(5);
        brightnessGrid.setPrefWidth(ALLWIDTH);
        recordGrid.add(recordTB, 0, 1);
//        recordGrid.prefWidthProperty().bind(recordOver.widthProperty());
        recordGrid.setVgap(5);
        recordGrid.setPrefWidth(ALLWIDTH);

        sunView.setImage(sunImg);
        moonView.setImage(moonImg);

        brightnessAnchor.getChildren().addAll(sunView, moonView);
        brightnessAnchor.setRightAnchor(moonView, 0.0);
        brightnessAnchor.setTopAnchor(sunView, 0.0);
        brightnessAnchor.setLeftAnchor(sunView, 0.0);
        brightnessAnchor.setTopAnchor(moonView, 0.0);

        lightColorOver.add(lightColorLbl, 0, 0);
        lightColorOver.add(lightColorGrid, 0, 1);
        cameraSwitchOver.add(cameraSwitchLbl, 0, 0);
        cameraSwitchOver.add(cameraSwitchGrid, 0, 1);
        brightnessOver.add(brightnessLbl, 0, 0);
        brightnessOver.add(brightnessGrid, 0, 1);
        recordOver.add(recordLbl, 0, 0);
        recordOver.add(recordGrid, 0, 1);

        SwingNode sn = new SwingNode();
        sn.setContent(picker);
        toolsVBox.getChildren().addAll(sn , lightColorOver, brightnessOver, cameraSwitchOver, recordOver);
        toolsVBox.getStyleClass().add("toolbox");
        lightColorLbl.getStyleClass().add("tool-label");
        cameraSwitchLbl.getStyleClass().add("tool-label");
        brightnessLbl.getStyleClass().add("tool-label");
        recordLbl.getStyleClass().add("tool-label");

//        toolsVBox.prefWidthProperty().bind(maxWidthProperty());
//        toolsVBox.minWidthProperty().bind(maxWidthProperty());
        toolsVBox.setSpacing(0);

        getChildren().addAll(toolsVBox, new VBoxDivider());

        toolsVBox.setVgrow(lightColorOver, Priority.ALWAYS);
        lightColorGrid.getStyleClass().add("tool-item");
        lightColorOver.getStyleClass().add("tool-item-box");
        toolsVBox.setVgrow(cameraSwitchOver, Priority.ALWAYS);
        cameraSwitchGrid.getStyleClass().add("tool-item");
        cameraSwitchOver.getStyleClass().add("tool-item-box");
        toolsVBox.setVgrow(brightnessOver, Priority.ALWAYS);
        brightnessGrid.getStyleClass().add("tool-item");
        brightnessOver.getStyleClass().add("tool-item-box");
        recordGrid.getStyleClass().add("tool-item");
        recordOver.getStyleClass().add("tool-item-box");
        toolsVBox.setVgrow(recordOver, Priority.ALWAYS);

        lightWhiteTB.prefWidthProperty().bind(lightColorGrid.widthProperty());
        lightWhiteTB.getStyleClass().add("tool-button");
        lightBlueTB.prefWidthProperty().bind(lightColorGrid.widthProperty());
        lightBlueTB.getStyleClass().add("tool-button");
        cameraSwitchTB.prefWidthProperty().bind(cameraSwitchGrid.widthProperty());
        cameraSwitchTB.getStyleClass().add("tool-button");
        recordTB.prefWidthProperty().bind(cameraSwitchGrid.widthProperty());
        recordTB.getStyleClass().add("tool-button");
        brightnessSlider.getStyleClass().add("tool-button");

        getStyleClass().add("htoolbox");
        toolsVBox.setSpacing(5);
        lightColorLbl.minWidthProperty().bind(lightColorGrid.widthProperty());
        cameraSwitchLbl.minWidthProperty().bind(cameraSwitchGrid.widthProperty());
        brightnessLbl.minWidthProperty().bind(brightnessGrid.widthProperty());
        recordLbl.minWidthProperty().bind(recordGrid.widthProperty());

//        toolsVBox.maxWidth(toolsVBox.getWidth());
//        maxWidthProperty().bind(prefWidthProperty());
//        maxWidth(getWidth());
    }
}
