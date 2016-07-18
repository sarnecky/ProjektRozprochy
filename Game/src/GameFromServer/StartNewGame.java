/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GameFromServer;

import Enums.OneField;

/**
 *
 * @author ArkadiuszSadowski
 */

public class StartNewGame extends GameInAction{
    public int playerCount;
    public String[] nameOfPlayers;
    public int[] x;
    public int[] y ;
    public OneField[] field;
    public int[] lifes;
    
    public StartNewGame(String[] array,int[] x,int[] y , int count,OneField[] o,int[] lifes,int flag){
        this.field = o;
        this.x = x;
        this.y = y;
        this.playerCount = count;
        this.nameOfPlayers = array;
        this.lifes=lifes;  
        this.flag=flag;
    }
    
    
    
}
