package com;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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


public class Main {
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener scl;
    private serial.SerialConnectPanel scp;
    private JTextArea jtf;
    private SerialPort sp;

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
//        scene.getStylesheets().add(Main.class.getResource("/controls.css").toExternalForm());
        scene.getStylesheets().add("controls.css");
        fxPanel.setScene(scene);
    }

    private Scene createScene() {
        BorderPane border = new BorderPane();
        Scene  scene  =  new  Scene(border, Color.ALICEBLUE);
//        scene.getStylesheets().add(getClass().getResource("../css/controls.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("/controls.css").toExternalForm());
//        System.out.println(getClass().getResource("/controls.css").toExternalForm());
        Text  text  =  new  Text();

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
        border.setTop(scp);
        border.setAlignment(scp, Pos.TOP_CENTER);

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
