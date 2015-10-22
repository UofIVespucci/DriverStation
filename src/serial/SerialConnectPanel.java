package com.serial;

import com.serial.SerialConnectListener;
import jssc.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class SerialConnectPanel extends JPanel {
    private static class BaudRate{
        public String name;
        public int id;
        BaudRate(String name, int id){
            this.name = name;
            this.id = id;
        }
        @Override
        public String toString(){
            return name;
        }
    }
    private static BaudRate[] rates = new BaudRate[]{
        new BaudRate("110   ",SerialPort.BAUDRATE_110    ),
        new BaudRate("300   ",SerialPort.BAUDRATE_300    ),
        new BaudRate("600   ",SerialPort.BAUDRATE_600    ),
        new BaudRate("1200  ",SerialPort.BAUDRATE_1200   ),
        new BaudRate("4800  ",SerialPort.BAUDRATE_4800   ),
        new BaudRate("9600  ",SerialPort.BAUDRATE_9600   ),
        new BaudRate("14400 ",SerialPort.BAUDRATE_14400  ),
        new BaudRate("19200 ",SerialPort.BAUDRATE_19200  ),
        new BaudRate("38400 ",SerialPort.BAUDRATE_38400  ),
        new BaudRate("57600 ",SerialPort.BAUDRATE_57600  ),
        new BaudRate("115200",SerialPort.BAUDRATE_115200 )
    };
    private int baudRate = SerialPort.BAUDRATE_9600;
    private boolean showBaudPanel = false;
    private SerialConnectListener listener;
    private SerialPort connectedPort = null;
    private JButton refreshButton;
    private JButton connectButton;
    private JComboBox dropDown;
    private JComboBox<BaudRate> baudSelect;

    public SerialConnectPanel(SerialConnectListener listener){
        this.listener = listener;
        refreshButton = new JButton(refreshAction);
        connectButton = new JButton(connectAction);
        dropDown = new JComboBox();
        addSerialList(dropDown);
        baudSelect = new JComboBox<BaudRate>(rates);
        //if the default baud rate is in the list, choose it
        for(int i=0; i<rates.length; i++){
            if(rates[i].id == baudRate){
                baudSelect.setSelectedIndex(i);
                break;
            }
        }
        baudSelect.setVisible(false);
        add(refreshButton);
        add(dropDown);
        add(baudSelect);
        add(connectButton);
        setOpaque(false);
    }

    public void setBaudRate(int baudRate){
        this.baudRate = baudRate;
    }

    public void showBaudSelector(boolean show){
        baudSelect.setVisible(show);
    }

    /*
        Sometimes serial port actions can block for many seconds, so connect and
        disconnect are run off the UI thread. These functions control the four
        states and prevent multiple port actions from racing eachother
    */
    private static final String BUTTON_CONNECTING    = "Connecting";
    private static final String BUTTON_CONNECTED     = "Disconnect";
    private static final String BUTTON_DISCONNECTING = "Disabling ";
    private static final String BUTTON_DISCONNECTED  = " Connect  ";
    private boolean inProgress = false;
    private void connect(){
        if(inProgress){
            System.err.println("Connect command issued while a change was in Progress");
            return;
        }
        refreshButton.setEnabled(false);
        dropDown.setEnabled(false);
        connectButton.setEnabled(false);
        connectButton.setText(BUTTON_CONNECTING);
        inProgress = true;
        (new Thread(connectSerial)).start();
    }
    private void connectDone(){
        refreshButton.setEnabled(false);
        dropDown.setEnabled(false);
        connectButton.setEnabled(true);
        connectButton.setText(BUTTON_CONNECTED);
        inProgress = false;
    }
    private void disconnect(){
        if(inProgress){
            System.err.println("Connect command issued while a change was in Progress");
            return;
        }
        refreshButton.setEnabled(false);
        dropDown.setEnabled(false);
        connectButton.setEnabled(false);
        connectButton.setText(BUTTON_DISCONNECTING);
        inProgress = true;
        (new Thread(disconnectSerial)).start();
    }
    private void disconnectDone(){
        refreshButton.setEnabled(true);
        dropDown.setEnabled(true);
        connectButton.setEnabled(true);
        connectButton.setText(BUTTON_DISCONNECTED);
        inProgress = false;
    }

    Action refreshAction = new AbstractAction(){
        {
            String text = "Refresh";
            putValue(Action.NAME, text);
            putValue(Action.SHORT_DESCRIPTION, text);
        }
        public void actionPerformed(ActionEvent e) {
            if(inProgress) return;
            dropDown.removeAllItems();
            addSerialList(dropDown);
            SerialConnectPanel.this.updateUI();
        }
    };

    private void addSerialList(JComboBox box){
        String[] portNames = SerialPortList.getPortNames();
        for(int i = 0; i < portNames.length; i++){
            box.addItem(portNames[i]);
        }
    }

    Action connectAction = new AbstractAction(){
        {
        String text = BUTTON_DISCONNECTED;
        putValue(Action.NAME, text);
        putValue(Action.SHORT_DESCRIPTION, text);
        }
        public void actionPerformed(ActionEvent e){
            if (connectedPort == null) connect();
            else disconnect();
        }
    };

    private Runnable connectSerial = new Runnable(){
        public void run(){
            if(dropDown.getSelectedItem() == null) return;

            SerialPort serialPort = new SerialPort((String)dropDown.getSelectedItem());

            try{
                serialPort.openPort();

                if(baudSelect.isVisible()){
                    baudRate = ((BaudRate)baudSelect.getSelectedItem()).id;
                }

                serialPort.setParams(baudRate,              SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                                              SerialPort.FLOWCONTROL_RTSCTS_OUT);
            } catch(SerialPortException ex){
                System.err.println(ex.getMessage());
                return;
            }

            connectedPort = serialPort;
            listener.connectionEstablished(serialPort);
            connectDone();
        }
    };

    private Runnable disconnectSerial = new Runnable(){
        public void run(){
            listener.disconnectRequest();

            try{
                connectedPort.closePort();
            } catch (Exception e) {
                e.printStackTrace();
            }

            connectedPort = null;
            disconnectDone();
        }
    };
}
