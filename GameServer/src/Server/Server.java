package Server;

import LoginFromServer.*;
import GameFromServer.*;
import DataToServer.InfoAboutPlayer;

import gameplay.*;
import Enums.*;
import java.awt.Point;
import windows.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private ArrayList<ClientsPortAddress> clients = new ArrayList<ClientsPortAddress>();
    private OneField oneField;
    private DatagramSocket socket;
    private Map<OneField, ArrayList<Point>> points = new EnumMap<OneField, ArrayList<Point>>(OneField.class);
    private Bonus bonus;

    public Server() {
        points.put(OneField.Empty, new ArrayList<Point>());
        points.put(OneField.B1, new ArrayList<Point>());
        points.put(OneField.B2, new ArrayList<Point>());
        points.put(OneField.B3, new ArrayList<Point>());
        points.put(OneField.B4, new ArrayList<Point>());
        points.put(OneField.G1, new ArrayList<Point>());
        points.put(OneField.G2, new ArrayList<Point>());
        points.put(OneField.G3, new ArrayList<Point>());
        points.put(OneField.G4, new ArrayList<Point>());

        try {
            this.socket = new DatagramSocket(4567);
        } catch (SocketException a) {
            a.printStackTrace();
        }

    }

    public void run() {

        waitingForClients();
        Random gen = new Random();
        bonus = new Bonus(this);
       
        while (true) {
            try {
                byte[] recvBuf = new byte[5000];
                DatagramPacket packet = new DatagramPacket(recvBuf,
                        recvBuf.length);

                socket.receive(packet);

                int byteCount = packet.getLength();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                InfoAboutPlayer o = (InfoAboutPlayer) is.readObject();
                //System.out.println(o.x + "   " + o.y);
                is.close();

                
                if (gen.nextDouble() > 0.95) {
                    bonus.draw();
                }
                else{
                    bonus.bon=null;  
                }

                if (checkCollision(o.x, o.y)) {
                    for (ClientsPortAddress client : clients) {
                        if (client.getIpAddress().equals(packet.getAddress()) && client.getPort() == packet.getPort()) {
                            client.killed = true;
                            client.life--;
                        }
                    }
                } else {
                    putOnMap(o.field, o.x, o.y);
                }

                sendData(o);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void waitingForClients() {

        try {

            while (true) {
                boolean exist = false;
                byte[] recvBuf = new byte[5000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

                socket.receive(packet);

                System.out.println(packet.getAddress() + "   " + packet.getPort());
                for (ClientsPortAddress client : clients) {
                    if (client.getIpAddress().equals(packet.getAddress()) && client.getPort() == packet.getPort()) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {

                    int byteCount = packet.getLength();
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                    ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                    String o = (String) is.readObject();
                    System.out.println("Gracz o nicku " + o + "  został dołączony. :)");
                    is.close();

                    clients.add(new ClientsPortAddress(packet.getAddress(), packet.getPort(), o));
                    sendColorAndDefaultXY(packet.getAddress(), packet.getPort());
                }

                if (clients.size() == 2) {
                    System.out.println("Zaczynamy rozgrywke   ");
                    startTheCombat(2);
                    startTheCombat(3);
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void sendData(InfoAboutPlayer info) {
        GameArea gameToSend = new GameArea(info.x, info.y, info.field,1);
        if(bonus.bon!=null){
            gameToSend.bonusToDraw=true;
            gameToSend.bonusTD=bonus.bon;
            gameToSend.xTD = bonus.x;
            gameToSend.yTD = bonus.y;
        }
        
        int countOfKilled = 0;
        for (ClientsPortAddress client : clients) {
            try {
                gameToSend.killed=client.killed;
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
                ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                os.flush();
                os.writeObject(gameToSend);
                os.flush();
                
                
                if (client.killed) {
                    countOfKilled++;
                }
                
                

                //retrieves byte array
                byte[] data = byteStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length, client.getIpAddress(), client.getPort());
                int byteCount = packet.getLength();
                socket.send(packet);
                os.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (countOfKilled == 1) {
            startNewGame();
        }

    }

    private void sendColorAndDefaultXY(InetAddress ipAddress, int port) {
        try {

            DefaultOptions playerData;

            switch (clients.size()) {
                case 1:
                    playerData = new DefaultOptions(70, 70, oneField.G1, Direction.Right, 1);
                    break;
                case 2:
                    playerData = new DefaultOptions(70, 470, oneField.G2, Direction.Up, 1);
                    break;
                case 3:
                    playerData = new DefaultOptions(570, 470, oneField.G3, Direction.Left, 1);
                    break;
                case 4:
                    playerData = new DefaultOptions(570, 70, oneField.G4, Direction.Down, 1);
                    break;
                default:
                    return;
            }

            for (ClientsPortAddress client : clients) {
                if (client.getIpAddress().equals(ipAddress) && client.getPort() == port) {
                    client.x = playerData.x;
                    client.y = playerData.y;
                    client.field = playerData.oneField;
                }
            }

            putOnMap(playerData.oneField, playerData.x, playerData.y);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
            os.flush();
            os.writeObject(playerData);
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

    public void startTheCombat(int flag) {

        PlayerBeginingData playerData = new PlayerBeginingData();

        if (flag == 3) {

            long current = System.currentTimeMillis();
            while ((System.currentTimeMillis() - current) < 5000);

        } else if (flag == 2) {

            String[] array = new String[clients.size()];
            int[] x = new int[clients.size()];
            int[] y = new int[clients.size()];
            OneField[] o = new OneField[clients.size()];
            int i = 0;

            for (ClientsPortAddress client : clients) {
                array[i] = client.getPlayerName();
                x[i] = client.x;
                y[i] = client.y;
                o[i] = client.field;
                i++;

            }
            playerData = new AnotherPlayers(array, x, y, clients.size(), o);
        }

        try {

            playerData.flag = flag;

            for (ClientsPortAddress client : clients) {

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
                ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                os.flush();
                os.writeObject(playerData);
                os.flush();

                //retrieves byte array
                byte[] data = byteStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length, client.getIpAddress(), client.getPort());
                int byteCount = packet.getLength();
                socket.send(packet);
                os.close();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkCollision(int x, int y) {
        Point target = new Point(x, y);

        if (x > 605 || x < 10 || y > 505 || y < 10) //wyjscie za plansze
        {

            return true;
        } else if (checkCollisionWithOhterFields(target)) {
            return true;
        }
        return false;
    }

    private boolean checkCollisionWithOhterFields(Point target) {

        for (OneField field : this.points.keySet()) {

            for (Point p : this.points.get(field)) {

                if (p.equals(target)) {
                    switch (field) {
                        case B1://bigger

                            delete(target.x, target.y, field);
                            return false;

                        case B2: //faster                            
                            delete(target.x, target.y, field);
                            return false;

                        case B3: //Dot);
                            delete(target.x, target.y, field);
                            return false;

                        case B4: //health
                            delete(target.x, target.y, field);
                            return false;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void putOnMap(OneField field, int x, int y) {
        points.get(field).add(new Point(x, y));
    }

    public void delete(int x, int y, OneField field) {
        points.get(field).remove(new Point(x, y));
    }

    private void startNewGame() {

        for (OneField field : this.points.keySet()) {
            points.remove(field);
        }
        points.put(OneField.Empty, new ArrayList<Point>());
        points.put(OneField.B1, new ArrayList<Point>());
        points.put(OneField.B2, new ArrayList<Point>());
        points.put(OneField.B3, new ArrayList<Point>());
        points.put(OneField.B4, new ArrayList<Point>());
        points.put(OneField.G1, new ArrayList<Point>());
        points.put(OneField.G2, new ArrayList<Point>());
        points.put(OneField.G3, new ArrayList<Point>());
        points.put(OneField.G4, new ArrayList<Point>());

        String[] array = new String[clients.size()];
        int[] x = new int[clients.size()];
        int[] y = new int[clients.size()];
        OneField[] o = new OneField[clients.size()];
        int[] lifes = new int[clients.size()];
        int i = 0;

        for (ClientsPortAddress client : clients) {
            array[i] = client.getPlayerName();
            x[i] = client.x;
            y[i] = client.y;
            o[i] = client.field;
            lifes[i] = client.life;
            client.killed = false;
            i++;

            putOnMap(client.field, client.x, client.y);
        }

        for (int j = 0; j < 2; j++) {
            try {
                GameInAction newGame;

                if (j == 0) {
                    newGame = new StartNewGame(array, x, y, clients.size(), o, lifes, 2);

                } else {
                    long current = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - current) < 5000);
                    newGame = new GameInAction();
                    newGame.flag = 3;
                }

                for (ClientsPortAddress client : clients) {

                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
                    ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                    os.flush();
                    os.writeObject(newGame);
                    os.flush();

                    //retrieves byte array
                    byte[] data = byteStream.toByteArray();
                    DatagramPacket packet = new DatagramPacket(data, data.length, client.getIpAddress(), client.getPort());
                    int byteCount = packet.getLength();
                    socket.send(packet);
                    os.close();

                }

                if (j == 0) {
                    dropNotNeededPackets();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void dropNotNeededPackets() {
        int count = 0;

        while (count != clients.size()) {
            try {
                byte[] recvBuf = new byte[5000];
                DatagramPacket packet = new DatagramPacket(recvBuf,
                        recvBuf.length);

                socket.receive(packet);

                int byteCount = packet.getLength();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
                ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
                InfoAboutPlayer o = (InfoAboutPlayer) is.readObject();
                //System.out.println(o.x + "   " + o.y);
                is.close();

                if (o.startTheGame) {
                    count++;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        }
    }

}
