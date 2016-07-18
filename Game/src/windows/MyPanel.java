package windows;

import gameplay.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import Enums.*;

public class MyPanel extends JPanel {

    Graphics2D g2d;
    JLabel bonus = new JLabel();
    JLabel bonusy = new JLabel();
    JLabel gracz = new JLabel();
    JLabel zycia = new JLabel();
    JLabel gameStatus = new JLabel();
    JLabel playersName = new JLabel();
    JLabel playersData = new JLabel();
    Player player;

    public String[] playersNames;
    public int[] playersLifes = new int[4];

    public Map<OneField, ArrayList<Point>> points = new EnumMap<OneField, ArrayList<Point>>(OneField.class); //pola na planszy  

    public void setPlayer(Player p) {
        this.player = p;
    }

    public void setBonus(Bonuses bonus) {
        this.bonus.setText("Ostatni bonus:  " + bonus);
    }

    public void setBonusy() {
        this.bonusy.setText("<html>Bialy - Ghost <br> Rozowy - Faster  <br> Cyan - Dot <br>Yellow - MoreHealth <br> JasnoSzary - Slower </html>  ");
    }

    public void setGracz() {
        String playerColor = "";

        switch (this.player.field) {
            case G1:
                playerColor = "Czerwony";
                break;
            case G2:
                playerColor = "Niebieski";
                break;
            case G3:
                playerColor = "Czarny";
                break;
            case G4:
                playerColor = "Pomaranczowy";
                break;
            default:
                break;
        }
        this.gracz.setText("<html>Jestes graczem  o nicku : <br>" + player.playerName + "<br>" + "Kolor : " + playerColor + "<br>"
                + "X : " + player.x_default + "<br>" + "Y : " + player.y_default + "</html>");
    }

    public void setGameStatus(String show) {
        this.gameStatus.setText(show);
    }

    public void setZycia(int text) {
        this.zycia.setText("Zycia:  " + text);
    }

    public void setPlayers(String[] playersName) {
        StringBuilder str = new StringBuilder();

    }

    public void setPlayersInfo() {
        StringBuilder str = new StringBuilder();
        str.append("<html> ");
        for (int i = 0; i < this.playersNames.length; i++) {
            str.append(playersNames[i]+" - "+playersLifes[i] + " <br> ");
        }
        str.append("</html>");
        this.playersData.setText(str.toString());
    }

    public Map<OneField, ArrayList<Point>> getPoints() {
        return points;
    }

    public MyPanel() {

        this.playersLifes[0] = 5;
        this.playersLifes[1] = 5;
        this.playersLifes[2] = 5;
        this.playersLifes[3] = 5;
        setPreferredSize(new Dimension(500, 500));

        points.put(OneField.Empty, new ArrayList<Point>());
        points.put(OneField.G1, new ArrayList<Point>());
        points.put(OneField.G2, new ArrayList<Point>());
        points.put(OneField.G3, new ArrayList<Point>());
        points.put(OneField.G4, new ArrayList<Point>());
        points.put(OneField.B1, new ArrayList<Point>());
        points.put(OneField.B2, new ArrayList<Point>());
        points.put(OneField.B3, new ArrayList<Point>());
        points.put(OneField.B4, new ArrayList<Point>());

        setBonus(Bonuses.NoBonus);
        setZycia(5);
        setBonusy();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g2d = (Graphics2D) g;

        //Ustawianie Labeli
        gameStatus.setBounds(700, 50, 200, 40);
        add(gameStatus);

        zycia.setBounds(700, 100, 200, 40);
        add(zycia);

        bonus.setBounds(700, 150, 200, 40);
        add(bonus);

        gracz.setBounds(700, 200, 200, 100);
        add(gracz);

        bonusy.setBounds(700, 300, 200, 100);
        add(bonusy);
        
        playersData.setBounds(700, 400, 200, 100);
        add(playersData);

        // ramka pola do gry
        Rectangle2D rectangle = new Rectangle2D.Double(10, 10, 600, 500);

        g2d.setColor(Color.GREEN);
        g2d.fill(rectangle);
        /*
        //bon.draw();
        for (OneField field : points.keySet()) {
            for (Point p : points.get(field)) {
                Rectangle2D rectangle5 = new Rectangle2D.Double(p.x, p.y, 5, 5);

                switch (field) {
                    case B1:
                        g2d.setColor(Color.WHITE);
                        break;
                    case B2:
                        g2d.setColor(Color.PINK);
                        break;
                    case B3:
                        g2d.setColor(Color.CYAN);
                        break;
                    case B4:
                        g2d.setColor(Color.YELLOW);
                        break;
                    case G1:
                        g2d.setColor(Color.RED);
                        break;
                    case G2:
                        g2d.setColor(Color.BLUE);
                        break;
                    case G3:
                        g2d.setColor(Color.BLACK);
                        break;
                    case G4:
                        g2d.setColor(Color.ORANGE);
                        break;

                }
                g2d.fill(rectangle5);
            }
        }*/
        
        
        
        for (OneField field : points.keySet()) {
                for(int i=0;i<points.get(field).size();i++){

                Rectangle2D rectangle5 = new Rectangle2D.Double(points.get(field).get(i).x,points.get(field).get(i).y, 5, 5);

                switch (field) {
                    case B1:
                        g2d.setColor(Color.WHITE);
                        break;
                    case B2:
                        g2d.setColor(Color.PINK);
                        break;
                    case B3:
                        g2d.setColor(Color.CYAN);
                        break;
                    case B4:
                        g2d.setColor(Color.YELLOW);
                        break;
                    case G1:
                        g2d.setColor(Color.RED);
                        break;
                    case G2:
                        g2d.setColor(Color.BLUE);
                        break;
                    case G3:
                        g2d.setColor(Color.BLACK);
                        break;
                    case G4:
                        g2d.setColor(Color.ORANGE);
                        break;

                }
                g2d.fill(rectangle5);
            }
        }

    }

    public void putOnMap(int x, int y, OneField g) {
        points.get(g).add(new Point(x, y));
        repaint();
    }

    public void delete(int x, int y, OneField g) {
        points.get(g).remove(new Point(x, y));
        repaint();
    }

}
