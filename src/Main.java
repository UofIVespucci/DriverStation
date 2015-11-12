package com;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.serial.*;
import javafx.stage.Stage;
import jssc.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import javafx.application.*;
import javafx.scene.*;
import ui.ButtonSelector;


public class Main {
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener scl;
    private serial.SerialConnectPanel scp;
    private JTextArea jtf;
    private SerialPort sp;
    private ButtonSelector tabSelector;

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
        SplitPane split = new SplitPane();
        Scene  scene  =  new  Scene(split, Color.ALICEBLUE);
        StackPane stack = new StackPane();
        Text  text  =  new  Text();
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

        text.setX(40);
        text.setY(100);
        text.setFont(new Font(25));
        text.setText("Welcome JavaFX!");

        ((HBox)scp).setAlignment(Pos.CENTER);
        split.getItems().addAll(tabSelector, scp);
        split.setOrientation(Orientation.VERTICAL);
        tabSelector.maxHeightProperty().bind(split.heightProperty().multiply(0.01));
        split.setDividerPositions(0.01);
//        split.setStyle("-fx-background-color: transparent;");

//        split.setAlignment(scp, Pos.TOP_CENTER);
//        tabSelector.showText(tabSelector.isHover());

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
