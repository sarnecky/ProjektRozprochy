/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameFromServer;

import Enums.*;

/**
 *
 * @author ArkadiuszSadowski
 */
public class GameArea extends GameInAction {

    public OneField field;
    public int x, y;
    public boolean killed;

    public boolean bonusToDraw;
    public OneField bonusTD;
    public int xTD, yTD;
    
    

    public GameArea(int x, int y, OneField field ,int flag) {
        this.flag = flag;
        this.x = x;
        this.y = y;
        this.field = field;

    }

}
