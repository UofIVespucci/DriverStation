package com;

import com.serial.*;
import jssc.*;
import java.util.*;
import javax.swing.*;

public class Main implements Runnable{
    private final SerialPortEventListener spel;
    private final SerialConnectListener scl;
    private final SerialConnectPanel scp;
    private final JTextArea jtf;
    private SerialPort sp;

    public Main(){
        sp = null;
        spel = new SerialPortEventListener(){
            public void serialEvent(SerialPortEvent serialEvent){
                try{
                    updateText(new String(sp.readBytes()));
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

    private void updateText(String newText){
        jtf.setText(jtf.getText() + newText);
    }

    public void run(){
        JFrame test = new JFrame("Go Vespucci");
        jtf.setEditable(false);

        JPanel container = new JPanel();
        JScrollPane scrollBox = new JScrollPane(jtf);
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.add(scp);
        container.add(scrollBox);
        test.add(container);

        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.pack();
        test.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }
}
