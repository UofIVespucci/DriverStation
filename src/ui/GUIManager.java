package ui;

import com.VespuChat.VespuChatReceiver;
import com.VespuChat.VespuChatTransmitter;
import com.github.sarxos.webcam.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Scene;
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
import ui.toolbox.Toolbox;

//import static org.mockito.Mockito.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    private VideoOverlay videoOverlay;
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
    private RobotSpeed robotSpeed;

    protected VespuChatTransmitter t;
    protected VespuChatReceiver r;

    public Scene createScene() {
        robotSpeed = new RobotSpeed();
        videoOverlay = new VideoOverlay();
        toolboxContainer = new HBox();
        wcStack = new StackPane();
        rManager = new RecordingManager(videoOverlay);
        wcfxPanel = new WCFXPanel(rManager);
        scene = new Scene(toolboxContainer, Color.ALICEBLUE);

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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (voltage > 300) setBatteryIndicator(BatteryStatus.FULL);
                        else if (voltage > 290) setBatteryIndicator(BatteryStatus.HALF);
                        else if (voltage > 280) setBatteryIndicator(BatteryStatus.LOW);
                        else setBatteryIndicator(BatteryStatus.CRITICAL);
                    }
                });
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
        Toolbox toolbox = new Toolbox(scp, rManager, wcfxPanel, robotSpeed);

        wcStack.getChildren().addAll(wcfxPanel, videoOverlay);
        wcStack.getStyleClass().add("tool-scrollpane");
        wcStack.setMinWidth(0);
        wcStack.setMinHeight(0);

        toolboxContainer.getChildren().addAll(toolbox, new VBoxDivider(), wcStack);
        HBox.setHgrow(wcStack, Priority.ALWAYS);

        toolboxContainer.maxHeightProperty().bind(scene.heightProperty());

        return scene;
    }

    private void initKeyListener() {
        robotKeys = new DirectionButtons();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, robotKeys);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, robotKeys);
        initRobotCommandListener(robotKeys);
    }

    protected void initRobotCommandListener(DirectionButtons db){}

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

    public void setBatteryIndicator(BatteryStatus batteryStatus){
        videoOverlay.setBatteryLbl(batteryStatus);
    }

    public void closeStream() {
        wcfxPanel.stopStream();
        rManager.stopRecording();
    }

    public short getSpeed() {
        return robotSpeed.getSpeed();
    }
}
