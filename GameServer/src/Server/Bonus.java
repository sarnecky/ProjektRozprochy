package Server;

import Enums.OneField;
import Enums.Bonuses;

import java.util.Random;
import windows.MyPanel;

public class Bonus {

    public OneField bon;
    public Integer x, y;
    Server gameServer;

    public Bonus(Server gameServer) {
        this.gameServer = gameServer;
    }

    public void draw() {
        //losowanko, gdzie beda bonusy
   
Random gen = new Random();
        
            switch (gen.nextInt(4)) {
                case 0:
                    bon = OneField.B1; //Ghost
                    break;
                case 1:
                    bon = OneField.B2;//Faster;
                    break;
                case 2:
                    bon = OneField.B3;//Wholes
                    break;
                case 3:
                    bon = OneField.B4;//MoreHealth;
                    break;
            }

            Integer pomoc = gen.nextInt(600) + 10;
            x = pomoc - pomoc % 5;

            pomoc = gen.nextInt(500) + 10;
            y = pomoc - pomoc % 5;

            this.gameServer.putOnMap(bon, x, y);

    }
}
