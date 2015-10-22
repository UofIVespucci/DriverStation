package com;

import java.util.*;
import javax.swing.*;

public class Main implements Runnable{
    public Main(){

    }

    public void run(){
        JFrame test = new JFrame("Hello");
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.pack();
        test.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }
}
