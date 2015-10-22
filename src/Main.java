package com;

import com.serial.*;
import jssc.*;
import java.util.*;
import javax.swing.*;

public class Main implements Runnable{

    SerialPort sp;
    SerialPortEventListener spel;
    SerialConnectListener scl;
    SerialConnectPanel scp;
    JTextArea jtf;

    public Main(){
        sp = null;
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
        scp = new SerialConnectPanel(scl);
        jtf = new JTextArea(20,80);
    }

    public void run(){
        JFrame test = new JFrame("Go Vespucci");
        jtf.setEditable(false);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.add(scp);
        container.add(jtf);
        test.add(container);

        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.pack();
        test.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }
}
