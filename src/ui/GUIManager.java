package ui;

import com.Main;
import com.VespuChat.VespuChat;
import com.VespuChat.VespuChatReceiver;
import com.VespuChat.VespuChatTransmitter;
import com.github.sarxos.webcam.*;
import input.KeyControl;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import com.serial.SerialOutputStream;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;

//import static org.mockito.Mockito.*;

import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;

public class GUIManager {
    private ButtonSelector buttonSelector;
    private Toolbox toolbox;
    private VideoOverlay videoOverlay;
    private VBox buttonSelectorContainer;
    private HBox toolboxContainer;
    private StackPane wcStack;
    private Scene scene;
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener connectListener;
    private serial.SerialConnectPanel scp;
    private SerialPort sp;
    private List<PacketReader> readerList;
    private WCFXPanel wcfxPanel;
    private RecordingManager rManager;
    private TextField jtf;

    protected VespuChatTransmitter t;
    protected VespuChatReceiver r;

    public Scene createScene() {
        toolbox = new Toolbox();
        buttonSelector = new ButtonSelector();
        videoOverlay = new VideoOverlay();
        buttonSelectorContainer = new VBox();
        toolboxContainer = new HBox();
        wcStack = new StackPane();
        wcfxPanel = new WCFXPanel();
        rManager = new RecordingManager(wcfxPanel);
        scene = new Scene(buttonSelectorContainer, Color.ALICEBLUE);

        initKeyListener();
        readerList = new ArrayList<PacketReader>();
        readerList.add(new MotorCommand(){
            protected void onReceive(byte left, byte right){
                System.out.println("Received "+left+", "+right);
            }
        });
        connectListener = new SerialConnectListener() {
            public void connectionEstablished(SerialPort newConnection) {
                t = new VespuChatTransmitter(new SerialOutputStream(newConnection));
                r = new VespuChatReceiver(new SerialInputStream(newConnection), readerList);
            }
            public void disconnectRequest() {
                try {
                    r.close();
                    t.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        initSerial();
        toolbox.setScp(scp);

        wcStack.getChildren().addAll(wcfxPanel, videoOverlay);
        wcStack.getStyleClass().add("tool-scrollpane");
        wcStack.setMinWidth(0);
        wcStack.setMinHeight(0);

        toolboxContainer.getChildren().addAll(toolbox, new VBoxDivider(), wcStack);
        toolboxContainer.setHgrow(wcStack, Priority.ALWAYS);

        buttonSelectorContainer.getChildren().addAll(buttonSelector, new VBoxDivider(), toolboxContainer);
        buttonSelectorContainer.setVgrow(toolboxContainer, Priority.ALWAYS);
        buttonSelectorContainer.maxHeightProperty().bind(scene.heightProperty());

        return scene;
    }

    public void setWebcam(Webcam w) {
        wcfxPanel.setWebcam(w);
    }

    private void startRecording() {
        BufferedImage still = wcfxPanel.getStillImage();
        if (!rManager.getRecordingStatus() && wcfxPanel.getStreamingStatus() && still != null) {
            rManager.record("TestFile.mp4");
        }
        else System.err.println(
                "Error in recording request, either already recording, not streaming, or still image is null");
    }

    protected void stopRecording() {
        if (rManager!=null &&  rManager.getRecordingStatus())
            rManager.stopRecording();
    }

    private void initKeyListener() {
        //Add keyboard listener for the scene
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            Main.guiManager.handleInput(event.getCode());
            event.consume();
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            Main.guiManager.handleUpInput(event.getCode());
            event.consume();
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                resizeUI();
            }
        });
    }

    private void initSerial() {
        spel = new SerialPortEventListener(){
            public void serialEvent(SerialPortEvent serialEvent){
                try{
                    jtf.setText(jtf.getText() + new String(sp.readBytes()));
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        scp = new serial.SerialConnectPanel(connectListener);
    }

    public ReadOnlyDoubleProperty getVOWProperty() {
        return videoOverlay.widthProperty();
    }

    public ReadOnlyDoubleProperty getVOHProperty() {
        return videoOverlay.heightProperty();
    }

    public boolean toggleRecording() {
        if (rManager.getRecordingStatus()) {stopRecording(); return false;}
        else {startRecording(); return true;}
    }

    public void resizeUI() {
        if (toolbox!=null && wcStack!=null) {
            toolbox.setMaxHeight((scene.getHeight() - buttonSelector.getHeight()));
            wcStack.setMaxHeight((scene.getWindow().getHeight() - buttonSelector.getHeight()));
        }
    }
}
