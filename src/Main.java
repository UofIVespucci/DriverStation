package com;

import com.VespuChat.VespuChatTransmitter;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        Scene scene = createScene(stage);
        stage.setTitle("Vespucci");
        stage.setScene(scene);
        stage.setWidth(640);
        stage.setHeight(480);
        stage.show();
        scene.getStylesheets().add("controls.css");
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

    @Override
    public void stop(){
        guiManager.closeStream();
    }
}
