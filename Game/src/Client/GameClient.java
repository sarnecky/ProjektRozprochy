/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import LoginFromServer.DefaultOptions;
import LoginFromServer.PlayerBeginingData;
import DataToServer.InfoAboutPlayer;
import Enums.*;
import GameFromServer.*;
import LoginFromServer.AnotherPlayers;
import gameplay.*;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author ArkadiuszSadowski
 */
public class GameClient extends Thread {

    private InetAddress ipAddress;
    private Player player;
    private OneField oneField;
    private DatagramSocket socket;
    private int port ;

    public GameClient(Player player) {

        this.player = player;
        boolean correctAddress = false;
        while (!correctAddress) {

            String serverAddress = JOptionPane.showInputDialog("Podaj ip Servera : ");
            String port = JOptionPane.showInputDialog("Podaj port : ");
            this.port = Integer.parseInt(port);
            player.playerName = JOptionPane.showInputDialog("Podaj swój nick w grze :");

            try {
                this.socket = new DatagramSocket();
                this.ipAddress = InetAddress.getByName(serverAddress);
                correctAddress = true;
            } catch (SocketException a) {
                //a.printStackTrace();
                correctAddress = false;
                JOptionPane.showMessageDialog(null, "Niepoprawny adres servera. Spróbuj ponownie");
            } catch (UnknownHostException b) {
                correctAddress = false;
                JOptionPane.showMessageDialog(null, "Niepoprawny adres servera. Spróbuj ponownie");
            }
        }

        playerReadyForGame();

    }

    public void playerReadyForGame() {

        try {

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
            os.flush();
            os.writeObject(player.playerName);
            os.flush();

            //retrieves byte array
            byte[] data = byteStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
            int byteCount = packet.getLength();
            socket.send(packet);
            os.close();

            boolean gameStart = false;
            while (!gameStart) {
                byte[] recvBuf = new byte[2000];
                DatagramPacket packet2 = new DatagramPacket(recvBuf,
                        recvBuf.length);

                socket.receive(packet2);
                int byteCount2 = packet2.getLength();
                ByteArrayInputStream byteStream2 = new ByteArrayInputStream(recvBuf);
                ObjectInputStream is2 = new ObjectInputStream(new BufferedInputStream(byteStream2));
                PlayerBeginingData playerData = (PlayerBeginingData) is2.readObject();
                is2.close();
                if (playerData.flag == 1) {
                    DefaultOptions defaultOpt = (DefaultOptions) playerData;

                    player.field = defaultOpt.oneField;
                    player.x = defaultOpt.x;
                    player.y = defaultOpt.y;
                    player.x_default = defaultOpt.x;
                    player.y_default = defaultOpt.y;
                    player.direction = defaultOpt.direction;

                    player.panel.setGracz();
                    player.panel.setGameStatus("Oczekiwanie na graczy");
                    //player.panel.points = defaultOpt.points;
                    //player.panel.repaint();
                    player.panel.putOnMap(player.x_default, player.y_default, player.field);

                } else if (playerData.flag == 2) {
                    AnotherPlayers a = (AnotherPlayers) playerData;
                    for (int i = 0; i < a.playerCount; i++) {
                        player.panel.putOnMap(a.x[i], a.y[i], a.field[i]);

                    }
                    player.panel.playersNames = a.nameOfPlayers;
                    player.panel.setPlayersInfo();
                    player.panel.setGameStatus("Za chwile sie zacznie");
                } else if (playerData.flag == 3) {
                    player.panel.setGameStatus("GRA W TOKU");
                    gameStart = true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {

        while (true) {

            try {
                byte[] recvBuf = new byte[5000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

                socket.receive(packet);
                int byteCount = packet.getLength();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                //this.player.panel.points = (Map<OneField, ArrayList<Point>>) is.readObject();
                GameInAction game = (GameInAction) is.readObject();
                is.close();

                switch (game.flag) {
                    case 1:
                        GameArea gameArea = (GameArea) game;
                        if (gameArea.field != this.player.field) {
                            this.player.panel.putOnMap(gameArea.x, gameArea.y, gameArea.field);
                        } else if (gameArea.field == this.player.field && gameArea.killed == false) {

                            this.player.panel.putOnMap(gameArea.x, gameArea.y, gameArea.field);

                        } else if (gameArea.field == this.player.field && gameArea.killed == true) {
                            this.player.gameInAction = false;
                        }
                        if (gameArea.bonusToDraw) {
                            this.player.panel.putOnMap(gameArea.xTD, gameArea.yTD, gameArea.bonusTD);

                        }

                        break;
                    case 2:
                        this.player.gameInAction=false;
                        
                        for(OneField field : player.panel.getPoints().keySet()) {
                            player.panel.getPoints().remove(field);
                        }
                        
                        player.panel.points.put(OneField.Empty, new ArrayList<Point>());
                        player.panel.points.put(OneField.B1, new ArrayList<Point>());
                        player.panel.points.put(OneField.B2, new ArrayList<Point>());
                        player.panel.points.put(OneField.B3, new ArrayList<Point>());
                        player.panel.points.put(OneField.B4, new ArrayList<Point>());
                        player.panel.points.put(OneField.G1, new ArrayList<Point>());
                        player.panel.points.put(OneField.G2, new ArrayList<Point>());
                        player.panel.points.put(OneField.G3, new ArrayList<Point>());
                        player.panel.points.put(OneField.G4, new ArrayList<Point>());
                        
                        StartNewGame newGame = (StartNewGame) game;

                        for (int i = 0; i < newGame.playerCount; i++) {
                            player.panel.putOnMap(newGame.x[i], newGame.y[i], newGame.field[i]);
                           
                        }
                        
                        player.x = player.x_default;
                        player.y = player.y_default;
                        player.panel.setGameStatus("Nowa Rozgrywka");
                        player.panel.playersNames = newGame.nameOfPlayers;
                        player.panel.playersLifes = newGame.lifes;
                        player.panel.setPlayersInfo();
                        player.panel.repaint();

                        InfoAboutPlayer o =new InfoAboutPlayer(oneField, port, port, Direction.Right, Bonuses.NoBonus);
                        o.startTheGame=true;
                        sendData(o);
                        
                        break;
                    case 3:
                        player.panel.setGameStatus("GRA W TOKU");
                        player.gameInAction = true;
                        

                    default:
                        break;

                }

                //this.player.panel.repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendData(InfoAboutPlayer info) {

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
            os.flush();
            os.writeObject(info);
            os.flush();

            //retrieves byte array
            byte[] data = byteStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
            int byteCount = packet.getLength();
            socket.send(packet);
            os.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

  

    
    
}
