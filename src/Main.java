package com;

import com.VespuChat.VespuChatTransmitter;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import com.serial.SerialOutputStream;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.*;
import javax.swing.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import ui.*;


public class Main extends Application{
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener scl;
    private serial.SerialConnectPanel scp;
    private JTextArea jtf;
    private SerialPort sp;
    public static GUImgr guiManager = new GUImgr();

    @Override
    public void start(Stage stage) {
        //Instantiate the RobotController object here so that it can be handed off to both the serial and GUI managers
        //RobotController robotController = new RobotController();

        //Both managers need to reference the same SerialConnectPanel, need to instantiate here
        //SerialConnectPanel scp = new SerialConnectPanel();

        //Init serial here. Both managers will need to reference the same serialConnectPanel
        //SerialManager serialManager = new SerialManager(scp, (RobotControllerInterface)robotController);

        //Make GUIManager.createScene take a robotController interface as an argument. This will then pass to the
        //toolbox object owned by GUIManager
        //GUIManager guiManager = new GUIManager(scp, (RobotControllerInterface)robotController);

        Scene scene = createScene(stage);

        stage.setTitle("Vespucci");
        stage.setScene(scene);
        stage.setWidth(640);
        stage.setHeight(480);
        stage.show();
        scene.getStylesheets().add("controls.css");

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                guiManager.closeStream();
            }
        });
    }

    private Scene createScene(Stage stage) {
        return (guiManager.createScene());
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void updateText(String newText){
        jtf.setText(jtf.getText() + newText);
    }
}
