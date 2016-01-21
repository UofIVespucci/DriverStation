package com;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;
import java.util.List;
import java.util.ArrayList;
import jssc.*;
import javax.swing.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import ui.ButtonSelector;
import ui.Toolbox;
import ui.VBoxDivider;
import ui.VideoOverlay;

import java.awt.*;


public class Main {
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener scl;
    private serial.SerialConnectPanel scp;
    private JTextArea jtf;
    private SerialPort sp;
    private ButtonSelector tabSelector;
    private VideoOverlay vo;

    private void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Vespucci");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        scene.getStylesheets().add("controls.css");
        fxPanel.setScene(scene);
    }

    private Scene createScene() {
        Pane demoBack = new Pane();
        demoBack.getStyleClass().add("video-background");
        VBox split = new VBox();
        Scene  scene  =  new  Scene(split, Color.ALICEBLUE);
        Toolbox tb = new Toolbox();
        BorderPane videoSP = new BorderPane();
        vo = new VideoOverlay();
        tabSelector = new ButtonSelector();

        sp = null;
        jtf = new JTextArea(20,80);
        spel = new SerialPortEventListener(){
            public void serialEvent(SerialPortEvent serialEvent){
                try{
                    jtf.setText(jtf.getText() + new String(sp.readBytes()));
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        scl = new SerialConnectListener(){
            public void connectionEstablished(SerialPort newConnection){
                System.out.println("received a serial port");
                sp = newConnection;
                try{
                    sp.addEventListener(spel);
                } catch (Exception e) { e.printStackTrace(); }
            }
            public void disconnectRequest(){
                System.out.println("received a disconnect request");
                sp = null;
            }
        };
        scp = new serial.SerialConnectPanel(scl);

//        Webcam webcam = Webcam.getDefault();
        Webcam webcam = Webcam.getWebcamByName("USB 2821 Device 1");

        WebcamPanel panel = null;
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setDisplayDebugInfo(true);
            panel.setImageSizeDisplayed(true);
            panel.setMirrored(true);
        } else {
            System.out.println("No webcam detected");
        }

        SwingNode swingNode = new SwingNode();
        if (panel != null)  {
            panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            swingNode.setContent(panel);
        }

        videoSP.setLeft(tb);
        videoSP.setCenter(swingNode);

        ((HBox) scp).setAlignment(Pos.CENTER);
        split.getChildren().addAll(tabSelector, new VBoxDivider(),videoSP);
        vo.maxWidthProperty().bind(split.widthProperty());
        tabSelector.maxHeightProperty().bind(split.heightProperty().multiply(0.01));

        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
        }

        return (scene);
    }

    public static void testSerial(){
        JFrame frame = new JFrame("Vespucci");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final List<PacketReader> rl = new ArrayList<PacketReader>();
        rl.add(new MotorCommand(){
            protected void onReceive(short left, short right){
                System.out.println("Received motor command "+left+" "+right);
            }
        });
        rl.add(new com.VespuChat.messages.Error(){
            protected void onReceive(byte code){
                System.out.println("Error "+(code&0xff));
            }
        });
        rl.add(new com.VespuChat.messages.Debug(){
            protected void onReceive(byte a, byte b, byte c, byte d){
                System.out.println("debug "+
                                            Integer.toHexString(a)+", "+
                                            Integer.toHexString(b)+", "+
                                            Integer.toHexString(c)+", "+
                                            Integer.toHexString(d));
            }
        });

        SerialConnectListener connectListener = new SerialConnectListener(){
            VespuChatTransmitter t = null;
            VespuChatReceiver r = null;
            ScheduledThreadPoolExecutor scheduler = null;
            short counter = 0;
            public void connectionEstablished(SerialPort newConnection){
                t = new VespuChatTransmitter(new SerialOutputStream(newConnection));
                //SerialInputStream sis = new SerialInputStream(newConnection);
                r = new VespuChatReceiver(new SerialInputStream(newConnection), rl);
                scheduler = new ScheduledThreadPoolExecutor(1 /*num cores*/);
                Runnable transmit = new Runnable(){
                    public void run(){
                        counter += 1;
                        byte[] data = com.VespuChat.messages.Error.build((byte)counter);

                        System.out.print("Sending a message ");
                        for(int i=0; i<data.length; i++){
                            System.out.print((data[i]&0xFF)+", ");
                        }
                        System.out.println();

                        t.send(data);
                    }
                };
                scheduler.scheduleAtFixedRate(transmit, 100, 100, TimeUnit.MILLISECONDS);
            }
            public void disconnectRequest(){
                try{
                    scheduler.shutdown();
                    r.close();
                    t.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                serial.SerialConnectPanel panel = new serial.SerialConnectPanel(connectListener);
                panel.showBaudSelector(true);
                Scene scene = new Scene(panel, Color.ALICEBLUE);
                fxPanel.setScene(scene);
            }
        });
    }

    public static void main(String[] args) {
        final Main initMain = new Main();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //initMain.initAndShowGUI();
                testSerial();
            }
        });
    }

    private void updateText(String newText){
        jtf.setText(jtf.getText() + newText);
    }
}
