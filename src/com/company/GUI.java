package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener{
    public static void main(String args[]){
        //Creating the Frame
        JFrame frame = new JFrame("Laborator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JButton button1 = new JButton("Click");
        button1.setBounds(50,100,60,30);
        frame.getContentPane().add(button1);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
}
