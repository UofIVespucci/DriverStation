package ui;

import com.Main;
import com.VespuChat.VespuChat;
import com.VespuChat.VespuChatReceiver;
import com.VespuChat.VespuChatTransmitter;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import input.KeyControl;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import com.serial.SerialOutputStream;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;

//import static org.mockito.Mockito.*;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.util.ArrayList;
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
    private JTextArea jtf;
    private WCFXPanel wcfxPanel;

    protected VespuChatTransmitter t;
    protected VespuChatReceiver r;

    public Scene createScene()
    {
        toolbox = new Toolbox();
        buttonSelector = new ButtonSelector();
        videoOverlay = new VideoOverlay();
        buttonSelectorContainer = new VBox();
        toolboxContainer = new HBox();
        wcStack = new StackPane();
        wcfxPanel = new WCFXPanel();
        scene = new Scene(buttonSelectorContainer, Color.ALICEBLUE);

        initKeyListener();
        readerList = new ArrayList<PacketReader>();
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

        wcStack.getChildren().addAll(videoOverlay, wcfxPanel, scp);
        toolboxContainer.getChildren().addAll(toolbox, new VBoxDivider(), wcStack);
        toolboxContainer.setHgrow(wcStack, Priority.SOMETIMES);

        buttonSelectorContainer.getChildren().addAll(buttonSelector, new VBoxDivider(),toolboxContainer);

        return scene;
    }

    public void setWebcam(Webcam w) {
        wcfxPanel.setWebcam(w);
    }

    private void initKeyListener()
    {
        //Add keyboard listener for the scene
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event ->
                Main.guiManager.handleInput(event.getCode()));
    }

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
}
