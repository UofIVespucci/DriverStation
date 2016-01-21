package ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import javax.swing.*;
import java.awt.*;

public class GUIManager {
    private WebcamPanel wcPanel;
    private SwingNode wcNode;
    private ButtonSelector buttonSelector;
    private Toolbox toolbox;
    private VideoOverlay videoOverlay;
    private VBox buttonSelectorContainer;
    private HBox toolboxContainer;
    private StackPane wcStack;
    private Scene scene;

    public GUIManager(){
    }

    public Scene createScene() {
        wcNode = new SwingNode();
        toolbox = new Toolbox();
        buttonSelector = new ButtonSelector();
        videoOverlay = new VideoOverlay();
        buttonSelectorContainer = new VBox();
        toolboxContainer = new HBox();
        wcStack = new StackPane();
        scene = new Scene(buttonSelectorContainer, Color.ALICEBLUE);

        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event ->
                System.out.println("Pressed" + event.getCode()));
        wcStack.getChildren().addAll(wcNode, videoOverlay);

        Webcam webcam = Webcam.getDefault();
//        Webcam webcam = Webcam.getWebcamByName("USB 2821 Device 1");
//        wcPanel = setWebcam(webcam);
        setWebcam(webcam);

        if (wcPanel != null)  {
            wcPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            wcNode.setContent(wcPanel);
        }

//        toolboxContainer.setLeft(toolbox);
//        toolboxContainer.setCenter(wcStack);
        toolboxContainer.getChildren().addAll(toolbox,wcStack);

//        ((HBox) scp).setAlignment(Pos.CENTER);
        buttonSelectorContainer.getChildren().addAll(buttonSelector, new VBoxDivider(),toolboxContainer);
        videoOverlay.maxWidthProperty().bind(toolboxContainer.widthProperty());
//        buttonSelector.maxHeightProperty().bind(toolboxContainer.heightProperty().multiply(0.01));

        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
        }

        return scene;
    }

    public void setWebcam(Webcam w)
    {
        wcPanel = null;
        if (w != null) {
            System.out.println("Webcam: " + w.getName());
            w.setViewSize(WebcamResolution.VGA.getSize());
            wcPanel = new WebcamPanel(w);
            wcPanel.setFPSDisplayed(true);
            wcPanel.setDisplayDebugInfo(true);
            wcPanel.setImageSizeDisplayed(true);
            wcPanel.setMirrored(true);
//            return wcPanel;
        } else {
            System.out.println("No webcam detected");
            wcPanel = null;
//            return null;
        }
    }
}
