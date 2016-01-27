package com;

import com.VespuChat.VespuChatTransmitter;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.serial.SerialOutputStream;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;
import java.util.List;
import java.util.ArrayList;

import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import jssc.*;
import javax.swing.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import ui.*;


public class Main {
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener scl;
    private serial.SerialConnectPanel scp;
    private JTextArea jtf;
    private SerialPort sp;
    public static GUImgr guiManager = new GUImgr();

    private void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Vespucci");
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(640, 480);
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
        return (guiManager.createScene());
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
