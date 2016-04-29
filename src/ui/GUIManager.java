package ui;

import com.Main;
import com.VespuChat.VespuChat;
import com.VespuChat.VespuChatReceiver;
import com.VespuChat.VespuChatTransmitter;
import com.github.sarxos.webcam.*;
import input.KeyControl;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import com.serial.SerialOutputStream;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;
import com.control.DirectionButtons;

//import static org.mockito.Mockito.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;

public class GUIManager
{
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
    private DirectionButtons robotKeys;

    protected VespuChatTransmitter t;
    protected VespuChatReceiver r;

    public Scene createScene()
    {
//        Webcam testw = Webcam.getDefault();
//        testw.open(true);
//        testw.addWebcamListener(new WebcamListener() {
//            @Override
//            public void webcamOpen(WebcamEvent webcamEvent) {
//
//            }
//
//            @Override
//            public void webcamClosed(WebcamEvent webcamEvent) {
//
//            }
//
//            @Override
//            public void webcamDisposed(WebcamEvent webcamEvent) {
//
//            }
//
//            @Override
//            public void webcamImageObtained(WebcamEvent webcamEvent) {
////                System.out.println(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SS").format(new Date()));
//            }
//        });


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
            protected void onReceive(short left, short right){
                System.out.println("Received "+left+", "+right);
            }
        });
        readerList.add(new com.VespuChat.messages.Error(){
            protected void onReceive(byte num){
                System.out.println("Error  "+num);
            }
        });
        readerList.add(new com.VespuChat.messages.PositionData(){
            protected void onReceive(short left, short right, short voltage){
                System.out.println("Encoders at "+left+", "+right+" "+voltage);
            }
        });
        connectListener = new SerialConnectListener(){
            public void connectionEstablished(SerialPort newConnection){
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

    private void startRecording()
    {
        BufferedImage still = wcfxPanel.getStillImage();
        if (!rManager.getRecordingStatus() && wcfxPanel.getStreamingStatus() && still != null) {
//            Current (for testing purposes) framerate of 30
            rManager.record("test", "mp4", 30, still.getWidth(), still.getHeight());
        }
        else System.err.println(
                "Error in recording request, either already recording, not streaming, or still image is null");
    }

    private void stopRecording()
    {
        if (rManager.getRecordingStatus()) rManager.stopRecording();
    }

    private void initKeyListener() {
        robotKeys = new DirectionButtons();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, robotKeys);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, robotKeys);
        initRobotCommandListener(robotKeys);
    }

    protected void initRobotCommandListener(DirectionButtons db){}

    private void initSerial()
    {
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

    public boolean toggleRecording()
    {
        if (rManager.getRecordingStatus()) {stopRecording(); return false;}
        else {startRecording(); return true;}
    }
}
