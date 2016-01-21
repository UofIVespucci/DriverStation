package ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

        initWebcam();
        initKeyListener();

        wcStack.getChildren().addAll(wcNode, videoOverlay);

        toolboxContainer.getChildren().addAll(toolbox,wcStack);
        buttonSelectorContainer.getChildren().addAll(buttonSelector, new VBoxDivider(),toolboxContainer);
//        videoOverlay.maxWidthProperty().bind(toolboxContainer.widthProperty());

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
        } else {
            System.out.println("No webcam detected");
            wcPanel = null;
        }
    }

    private void initKeyListener(){
        //Add keyboard listener for the scene
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event ->
                //Temporary, soon to be obsolete keyboard listener event
                System.out.println("Pressed" + event.getCode()));
    }

    private void initWebcam(){
        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
        }

        Webcam webcam = Webcam.getDefault();
//        Webcam webcam = Webcam.getWebcamByName("USB 2821 Device 1");
        setWebcam(webcam);

        if (wcPanel != null)  {
            wcPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            wcNode.setContent(wcPanel);
        }

    }
}
