/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoginFromServer;

import Enums.Direction;
import Enums.OneField;

/**
 *
 * @author ArkadiuszSadowski
 */
public class DefaultOptions extends PlayerBeginingData {
    
    public int x,y;
    public OneField oneField;
    public Direction direction;
    
    public DefaultOptions(int x, int y, OneField oneField, Direction direction,int flag) {
    this.x =x;
    this.y = y;
    this.oneField =oneField;  
    this.direction = direction;
    this.flag = flag;
    }
    
}
