package com;

import com.serial.*;
import jssc.*;
import java.util.*;
import javax.swing.*;

public class Main implements Runnable{
    public Main(){

    }

    public void run(){
        JFrame test = new JFrame("Go Vespucci");
        JTextArea jtf = new JTextArea(20,80);
        jtf.setEditable(false);
        SerialPort sp = null;
        SerialConnectListener scl = new SerialConnectListener(){
            public void connectionEstablished(SerialPort newConnection){
                System.out.println("received a serial port");
                jtf.setText(jtf.getText() + "Connected\n");
            }
            public void disconnectRequest(){
                System.out.println("received a disconnect request");
                jtf.setText(jtf.getText() + "Disconnected\n");
            }
        };
        SerialConnectPanel scp = new SerialConnectPanel(scl);

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
