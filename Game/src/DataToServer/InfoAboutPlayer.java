package DataToServer;

import java.io.Serializable;

import Enums.*;

public class InfoAboutPlayer extends Object implements Serializable{
        
    
        public OneField field;
	public int x,y;
        public Direction direction;
        public Bonuses bonus;
	public boolean startTheGame = false;
        public InfoAboutPlayer(OneField field, int x, int y,Direction direction,Bonuses bonus)
	{
		this.field = field;
		this.x=x;
		this.y=y;
                this.direction = direction;
                this.bonus = bonus;
	}
        
}
