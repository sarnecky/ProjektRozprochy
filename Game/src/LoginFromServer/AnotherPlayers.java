/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoginFromServer;

import Enums.OneField;

/**
 *
 * @author ArkadiuszSadowski
 */
public class AnotherPlayers extends PlayerBeginingData{
     
    public int playerCount;
    public String[] nameOfPlayers;
    public int[] x;
    public int[] y ;
    public OneField[] field;
    
    public AnotherPlayers(String[] array,int[] x,int[] y , int count,OneField[] o ){
        this.field = o;
        this.x = x;
        this.y = y;
        this.playerCount = count;
        this.nameOfPlayers = array;
        
        
    }
    
}
