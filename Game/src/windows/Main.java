package windows;


import gameplay.Player;

import java.awt.EventQueue;

import javax.swing.*;

import Enums.OneField;

public class Main {
	public static void main(String[] args) {
		
                final MyPanel panel = new MyPanel(); //panel rozgrywki
		final Player seba = new Player(panel);


		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				new MyFrame(seba, panel);
				
			}
		});

                Thread th = new Thread(seba);
		th.start();
	}

}
