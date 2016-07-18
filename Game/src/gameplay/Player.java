package gameplay;

import DataToServer.InfoAboutPlayer;
import java.awt.Color;
import java.awt.Panel;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import windows.MyPanel;
import Enums.Bonuses;
import Enums.Direction;
import Enums.OneField;
import Client.GameClient;

public class Player implements Runnable {

    public int x_default, y_default, x, y, size, health = 5;
    public String playerName;
    public Direction direction; // kierunek w ktorym zmierza gracz
    public Bonuses bonus; //aktualny bonus gracza
    public MyPanel panel;
    ArrayList<Point> FieldsOfPlayer; //pola na ktorych obecnie znajduje sie gracz, ulatwi potem usuwanie przy kolizji
    public OneField field; //typ pola jaki obsadza dany gracz
    public int wholes = 0;
    public boolean gameInAction = true;

    GameClient client;

    int timer;

    public Player(MyPanel panel) {
        this.panel = panel;
        this.panel.setPlayer(this);
        this.direction = Direction.Empty;
        this.FieldsOfPlayer = new ArrayList();
        this.bonus = Bonuses.NoBonus;

    }

    public String getField() {
        return field.toString();
    }

    public Bonuses getBonus() {
        return bonus;
    }

    public int getHealth() {
        return health;
    }

    public void killPlayer() {
        panel.getPoints().remove(field); //usuniecie konkretnego weza z tablicy wyswietlania
        panel.repaint();
        FieldsOfPlayer.clear();
        this.health -= 1;

        panel.setZycia(this.health);

        if (this.health > 0) {
            bonus = null;
            this.x = this.x_default;
            this.y = this.y_default;
            FieldsOfPlayer.add(new Point(this.x_default, this.y_default));
            panel.points.put(field, FieldsOfPlayer);
        }
    }

    private boolean checkCollision(int x, int y) throws UnknownHostException, IOException, ClassNotFoundException {
        Point target = new Point(x, y);

        if (x > 605 || x < 10 || y > 505 || y < 10) //wyjscie za plansze
        {
            if (bonus == Bonuses.Ghost || bonus == Bonuses.Dot) {
                switch (direction) {
                    case Up:
                        this.y = 510;
                        break;
                    case Down:
                        this.y = 10;
                        break;
                    case Right:
                        this.x = 10;
                        break;
                    case Left:
                        this.x = 610;
                        break;
                }
                // return true;
            } else {
                this.health--;
                killPlayer();
                this.x = x_default;
                this.y = y_default;
                System.out.println("ilosc zyc:" + health);
            }
        } else if (checkCollisionWithOhterFields(target)) {
            //System.out.println("kolizja z innym graczem");
            killPlayer();//jezeli ten punkt na ktory chcemy wejsc jest na planszy, killamy snejka
        } else //nowy ruch danego gracza
        {
            panel.putOnMap(x, y, field);
            FieldsOfPlayer.add(new Point(x, y));
        }

        return true;
    }

    private boolean checkCollisionWithOhterFields(Point target) {

        for (OneField field : panel.points.keySet()) {

            for (Point p : panel.points.get(field)) {

                if (p.equals(target) && (target.x != x_default || target.y != y_default)) {
                    switch (field) {
                        case B1://bigger
                            //todo
                            panel.setBonus(bonus = Bonuses.Ghost);
                            panel.delete(target.x, target.y, field);
                            return false;
                        case B2: //faster
                            panel.setBonus(bonus = Bonuses.Faster);

                            panel.delete(target.x, target.y, field);
                            return false;
                        case B3: //Lesser
                            panel.setBonus(bonus = Bonuses.Dot);
                            panel.delete(target.x, target.y, field);
                            return false;
                        case B4: //health
                            panel.setBonus(bonus = Bonuses.MoreHealth);
                            health += 1;
                            panel.setZycia(health);
                            panel.delete(target.x, target.y, field);
                            return false;
                        case B5: //slower

                            panel.setBonus(bonus = Bonuses.Slower);

                            panel.delete(target.x, target.y, field);
                            return false;
                    }
                    if (bonus == Bonuses.Ghost) {
                        return false;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void run() {

        this.client = new GameClient(this);
        client.start();

        while (true) {
            long lastTime = System.nanoTime();
            double nsPerTick = 3000000000D / 60D;

            int ticks = 0;
            int frames = 0;

            long lastTimer = System.currentTimeMillis();
            double delta = 0;
            while (gameInAction) {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerTick;
                lastTime = now;
                boolean shouldRender = false;

                while (delta >= 1) {
                    ticks++;
                    delta -= 1.0;
                    shouldRender = true;
                }

                if (shouldRender) {
                    frames++;
                    move();
                }

                if (System.currentTimeMillis() - lastTimer >= 1000) {
                    lastTimer += 1000;

                    frames = 0;
                    ticks = 0;
                }
            }
        }
    }

    public void move() {
        int add = 0;
        wholes++;
        if (wholes == 10) {
            wholes = 0;
            add = 5;
        }
        //System.out.println(name + " juz smiga");

        switch (direction) {
            case Up:
                y -= 5 + add;
                break;
            case Down:
                y += 5 + add;
                break;
            case Right:
                x += 5 + add;
                break;
            case Left:
                x -= 5 + add;
                break;
            case Empty:
                break;
        }
        try {

            client.sendData(new InfoAboutPlayer(field, x, y, direction, bonus));
            if (false) {
                checkCollision(x, y);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
