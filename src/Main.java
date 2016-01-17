package com;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import input.KeyControl;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.event.*;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.serial.*;
import jssc.*;
import javax.swing.*;
import java.awt.event.KeyEvent;

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
    private WebcamPanel panel;

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
        StackPane videoStack = new StackPane();
        tabSelector = new ButtonSelector();
        SwingNode swingNode = new SwingNode();

        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> System.out.println("Pressed" + event.getCode()));
        videoStack.getChildren().addAll(swingNode, vo);

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

        Webcam webcam = Webcam.getDefault();
//        Webcam webcam = Webcam.getWebcamByName("USB 2821 Device 1");

        panel = null;
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
        System.out.println(panel.isFocusable());


        if (panel != null)  {
            panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            swingNode.setContent(panel);
        }

        videoSP.setLeft(tb);
        videoSP.setCenter(videoStack);

        ((HBox) scp).setAlignment(Pos.CENTER);
        split.getChildren().addAll(tabSelector, new VBoxDivider(),videoSP);
        vo.maxWidthProperty().bind(split.widthProperty());
        tabSelector.maxHeightProperty().bind(split.heightProperty().multiply(0.01));

        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
        }

        return (scene);
    }

    public static void main(String[] args) {
        final Main initMain = new Main();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initMain.initAndShowGUI();
            }
        });
    }

    private void updateText(String newText){
        jtf.setText(jtf.getText() + newText);
    }

}
