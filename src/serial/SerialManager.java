package com.serial;

import com.VespuChat.VespuChatReceiver;
import com.VespuChat.VespuChatTransmitter;
import com.control.DirectionButtons;
import javafx.application.Platform;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import com.serial.SerialOutputStream;
import com.serial.*;
import com.VespuChat.*;
import com.VespuChat.messages.*;
import ui.RobotController;
import ui.RobotSpeed;
import ui.streaming.BatteryStatus;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lance on 5/4/2016.
 */
public class SerialManager {
    private SerialPortEventListener spel;
    private com.serial.SerialConnectListener connectListener;
    private serial.SerialConnectPanel scp;
    private SerialPort sp;
    private List<PacketReader> readerList;
    private TextField jtf;
    private DirectionButtons robotKeys;
    private RobotSpeed robotSpeed;
    private RobotController robotController;
    protected VespuChatTransmitter t;
    protected VespuChatReceiver r;

    public void constructSerial() {
        robotController = new RobotController();
        robotKeys = new DirectionButtons();
        robotController.initRobotCommandListener(robotKeys);

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
//                        if (voltage > 300) videoOverlay.setBatteryLbl(BatteryStatus.FULL);
//                        else if (voltage > 290) videoOverlay.setBatteryLbl(BatteryStatus.QUARTER);
//                        else if (voltage > 280) videoOverlay.setBatteryLbl(BatteryStatus.HALF);
//                        else if (voltage > 270) videoOverlay.setBatteryLbl(BatteryStatus.LOW);
//                        else videoOverlay.setBatteryLbl(BatteryStatus.CRITICAL);
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
    }

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
}
