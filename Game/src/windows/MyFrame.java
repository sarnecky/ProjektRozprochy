package windows;

import gameplay.Player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Enums.Direction;

public class MyFrame extends JFrame implements KeyListener {

    private MyPanel panel; //panel rozgrywki
    int x = 70, y = 70;//glowa
    Player player;

    public MyFrame(Player player, MyPanel panel) {
        super("Snakes");
        addKeyListener(this);
        this.player = player;
        this.panel = panel;
        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) //strzalka w prawo
        {
            if (player.direction != Direction.Left) {
                player.direction = Direction.Right;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) //strzalka w gore
        {
            if (player.direction != Direction.Down) {
                player.direction = Direction.Up;
            }

        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) //strzalka w dol
        {
            if (player.direction != Direction.Up) {
                player.direction = Direction.Down;
            }

        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) //strzalka w lewo
        {
            if (player.direction != Direction.Right) {
                player.direction = Direction.Left;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

    }
}
